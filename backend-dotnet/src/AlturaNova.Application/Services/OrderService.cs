using AlturaNova.Application.Common.Mapping;
using AlturaNova.Application.Common.Payments;
using AlturaNova.Application.DTOs.Orders;
using AlturaNova.Application.Interfaces;
using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Enums;
using AlturaNova.Domain.Exceptions;
using AlturaNova.Domain.Interfaces;

namespace AlturaNova.Application.Services;

/// <summary>Implements checkout, retrieval, payment, and lifecycle transitions for orders.</summary>
public sealed class OrderService(
    ICartRepository carts,
    IProductRepository products,
    IOrderRepository orders,
    IUserRepository users,
    IPaymentGateway paymentGateway,
    IPickupStationRepository pickupStations,
    IUnitOfWork unitOfWork) : IOrderService
{
    private const decimal PickupFeePercent = 0.02m;
    private const decimal ShippingFeePercent = 0.05m;

    public async Task<OrderResponse> CheckoutAsync(Guid? userId, CheckoutRequest request, CancellationToken ct = default)
    {
        var deliveryMethod = ParseDeliveryMethod(request.DeliveryMethod);

        if (deliveryMethod == DeliveryMethod.Pickup)
        {
            if (request.PickupStationId is null)
                throw new ConflictException("A pickup station must be selected for pickup delivery.");

            var station = await pickupStations.GetByIdAsync(request.PickupStationId.Value, ct)
                ?? throw new ConflictException("The selected pickup station is not available.");
            if (!station.IsActive)
                throw new ConflictException("The selected pickup station is no longer active.");
        }

        // Determine items: use server-side cart (authenticated) or request body (guest).
        List<(Guid VariantId, int Quantity, Guid ProductId, string Title, string Sku,
              string ThumbnailUrl, decimal UnitPrice, string Currency, Guid VendorId)> lineItems;

        if (userId.HasValue)
        {
            var cart = await carts.GetActiveByUserAsync(userId.Value, ct);
            if (cart is null || cart.Items.Count == 0)
                throw new ConflictException("Your cart is empty.");

            lineItems = cart.Items.OrderBy(i => i.CreatedAt).Select(i => (
                i.VariantId,
                i.Quantity,
                i.ProductId,
                i.Title,
                "", // SKU resolved below from variant
                i.ThumbnailUrl,
                i.UnitPrice,
                i.Currency,
                Guid.Empty // VendorId resolved below from variant
            )).ToList();

            // Clear server-side cart after snapshotting
            cart.Items.Clear();
            cart.UpdatedAt = DateTimeOffset.UtcNow;
        }
        else
        {
            // Guest: items must be provided in the request body
            if (request.Items is null || request.Items.Count == 0)
                throw new ConflictException("Cart items are required for guest checkout.");

            if (string.IsNullOrWhiteSpace(request.Email))
                throw new ConflictException("An email address is required for guest checkout.");

            // Resolve each item from the database
            lineItems = new List<(Guid VariantId, int Quantity, Guid ProductId, string Title, string Sku,
                                  string ThumbnailUrl, decimal UnitPrice, string Currency, Guid VendorId)>();

            foreach (var item in request.Items)
            {
                var variant = await products.GetVariantAsync(item.VariantId, ct)
                    ?? throw new ConflictException($"Variant {item.VariantId} is no longer available.");

                var product = variant.Product;
                var vendorId = product?.VendorId ?? Guid.Empty;

                lineItems.Add((
                    item.VariantId,
                    item.Quantity,
                    variant.ProductId,
                    product?.Name ?? variant.Title,
                    variant.Sku,
                    product?.Images.FirstOrDefault()?.Url ?? "",
                    variant.Price,
                    variant.Currency,
                    vendorId
                ));
            }
        }

        var currency = lineItems.First().Currency;
        var order = new Order
        {
            UserId = userId,
            GuestEmail = !userId.HasValue ? request.Email : null,
            Status = OrderStatus.Pending,
            DeliveryMethod = deliveryMethod,
            PickupStationId = request.PickupStationId,
            Currency = currency
        };

        decimal subtotal = 0m;

        foreach (var item in lineItems)
        {
            var variant = await products.GetVariantAsync(item.VariantId, ct)
                ?? throw new ConflictException($"Product for cart item '{item.Title}' is no longer available.");

            if (item.Quantity > variant.InventoryQuantity)
                throw new ConflictException(
                    $"Insufficient stock for '{item.Title}': {variant.InventoryQuantity} available, {item.Quantity} requested.");

            variant.InventoryQuantity -= item.Quantity;

            var lineTotal = item.UnitPrice * item.Quantity;
            subtotal += lineTotal;

            order.Items.Add(new OrderItem
            {
                OrderId = order.Id,
                ProductId = item.ProductId,
                VariantId = item.VariantId,
                VendorId = item.VendorId,
                ProductName = item.Title,
                Sku = item.Sku,
                ThumbnailUrl = item.ThumbnailUrl,
                UnitPrice = item.UnitPrice,
                Quantity = item.Quantity,
                LineTotal = lineTotal,
                Currency = item.Currency
            });
        }

        order.Subtotal = subtotal;
        order.ShippingFee = deliveryMethod == DeliveryMethod.Pickup
            ? Math.Round(subtotal * PickupFeePercent, 2)
            : Math.Round(subtotal * ShippingFeePercent, 2);
        order.Total = subtotal + order.ShippingFee;
        order.ShippingAddress = MapAddress(order.Id, request.ShippingAddress);

        await orders.AddAsync(order, ct);
        await unitOfWork.SaveChangesAsync(ct);

        return order.ToResponse();
    }

    public async Task<OrderListResponse> GetOrdersAsync(Guid userId, CancellationToken ct = default)
    {
        var list = await orders.GetByUserAsync(userId, ct);
        return new OrderListResponse(list.Select(o => o.ToResponse()).ToList(), list.Count);
    }

    public async Task<OrderResponse> GetOrderAsync(Guid userId, Guid orderId, CancellationToken ct = default)
    {
        var order = await orders.GetByIdForUserAsync(orderId, userId, ct)
            ?? throw new NotFoundException("Order not found.");
        return order.ToResponse();
    }

    public async Task<PaymentInitiationResponse> InitiatePaymentAsync(Guid? userId, Guid orderId, string? callbackUrl, CancellationToken ct = default)
    {
        var order = userId.HasValue
            ? await orders.GetTrackedByIdForUserAsync(orderId, userId.Value, ct)
            : await orders.GetTrackedByIdAsync(orderId, ct)
              ?? throw new NotFoundException("Order not found.");

        if (order.Status != OrderStatus.Pending)
            throw new ConflictException($"Only pending orders can be paid; this order is {order.Status}.");

        // Resolve email: try user account first, then fall back to guest email stored on order.
        string email;
        if (userId.HasValue)
        {
            var user = await users.GetByIdAsync(userId.Value, ct)
                ?? throw new NotFoundException("User not found.");
            email = user.Email;
        }
        else
        {
            email = order.GuestEmail
                ?? throw new ConflictException("An email address is required for guest payment.");
        }

        var amountSubunits = ToSubunits(order.Total);
        var reference = BuildReference(order.Id);

        var init = await paymentGateway.InitializeAsync(
            new PaymentInitializationRequest(
                amountSubunits,
                paymentGateway.DefaultCurrency,
                email,
                reference,
                callbackUrl),
            ct);

        order.PaymentReference = init.Reference;
        order.UpdatedAt = DateTimeOffset.UtcNow;
        orders.Update(order);
        await unitOfWork.SaveChangesAsync(ct);

        return new PaymentInitiationResponse(
            order.Id,
            paymentGateway.ProviderName,
            init.AuthorizationUrl,
            init.AccessCode,
            init.Reference,
            paymentGateway.PublicKey,
            amountSubunits,
            paymentGateway.DefaultCurrency);
    }

    public async Task<OrderResponse> VerifyPaymentAsync(Guid? userId, Guid orderId, CancellationToken ct = default)
    {
        var order = userId.HasValue
            ? await orders.GetTrackedByIdForUserAsync(orderId, userId.Value, ct)
            : await orders.GetTrackedByIdAsync(orderId, ct)
              ?? throw new NotFoundException("Order not found.");

        if (order.Status == OrderStatus.Paid)
            return order.ToResponse();

        if (string.IsNullOrWhiteSpace(order.PaymentReference))
            throw new ConflictException("No payment has been initiated for this order.");

        await ApplyVerificationAsync(order, ct);
        await unitOfWork.SaveChangesAsync(ct);
        return order.ToResponse();
    }

    public async Task ConfirmPaymentByReferenceAsync(string reference, CancellationToken ct = default)
    {
        if (string.IsNullOrWhiteSpace(reference))
            return;

        var order = await orders.GetTrackedByPaymentReferenceAsync(reference, ct);
        if (order is null || order.Status == OrderStatus.Paid)
            return;

        await ApplyVerificationAsync(order, ct);
        await unitOfWork.SaveChangesAsync(ct);
    }

    private async Task ApplyVerificationAsync(Order order, CancellationToken ct)
    {
        var verification = await paymentGateway.VerifyAsync(order.PaymentReference!, ct);

        if (verification.Status != PaymentStatus.Success)
            return;

        if (verification.AmountSubunits != ToSubunits(order.Total))
            throw new ConflictException("The paid amount does not match the order total.");

        order.Status = OrderStatus.Paid;
        order.PaidAt = DateTimeOffset.UtcNow;
        order.UpdatedAt = DateTimeOffset.UtcNow;
        orders.Update(order);
    }

    public async Task<OrderResponse> CancelAsync(Guid userId, Guid orderId, CancellationToken ct = default)
    {
        var order = await orders.GetTrackedByIdForUserAsync(orderId, userId, ct)
            ?? throw new NotFoundException("Order not found.");

        if (order.Status is not (OrderStatus.Pending or OrderStatus.Paid))
            throw new ConflictException($"Orders in status {order.Status} cannot be cancelled.");

        await RestockAsync(order, ct);
        order.Status = OrderStatus.Cancelled;
        order.UpdatedAt = DateTimeOffset.UtcNow;
        orders.Update(order);

        await unitOfWork.SaveChangesAsync(ct);
        return order.ToResponse();
    }

    public async Task<OrderListResponse> GetAllAsync(CancellationToken ct = default)
    {
        var list = await orders.GetAllAsync(ct);
        return new OrderListResponse(list.Select(o => o.ToResponse()).ToList(), list.Count);
    }

    public async Task<OrderResponse> UpdateStatusAsync(Guid orderId, OrderStatus status, CancellationToken ct = default)
    {
        var order = await orders.GetTrackedByIdAsync(orderId, ct)
            ?? throw new NotFoundException("Order not found.");

        if (order.Status == OrderStatus.Cancelled)
            throw new ConflictException("A cancelled order cannot change status.");

        if (status == OrderStatus.Cancelled)
            await RestockAsync(order, ct);

        order.Status = status;
        order.UpdatedAt = DateTimeOffset.UtcNow;
        orders.Update(order);

        await unitOfWork.SaveChangesAsync(ct);
        return order.ToResponse();
    }

    private async Task RestockAsync(Order order, CancellationToken ct)
    {
        foreach (var item in order.Items)
        {
            var variant = await products.GetVariantAsync(item.VariantId, ct);
            if (variant is not null)
                variant.InventoryQuantity += item.Quantity;
        }
    }

    private static DeliveryMethod ParseDeliveryMethod(string? value) =>
        value?.ToLowerInvariant() switch
        {
            "pickup" => DeliveryMethod.Pickup,
            "shipping" or "delivery" or null => DeliveryMethod.Shipping,
            _ => throw new ConflictException($"Unknown delivery method '{value}'.")
        };

    private static long ToSubunits(decimal amount) => (long)Math.Round(amount * 100m, MidpointRounding.AwayFromZero);

    private static string BuildReference(Guid orderId) => $"altura_{orderId:N}_{Guid.NewGuid():N}"[..40];

    private static OrderAddress MapAddress(Guid orderId, AddressRequest a) => new()
    {
        OrderId = orderId,
        FirstName = a.FirstName.Trim(),
        LastName = a.LastName.Trim(),
        Line1 = a.Line1.Trim(),
        Line2 = a.Line2,
        City = a.City.Trim(),
        State = a.State,
        PostalCode = a.PostalCode.Trim(),
        Country = a.Country.Trim().ToUpperInvariant(),
        Phone = a.Phone
    };
}
