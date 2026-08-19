using AlturaNova.Application.DTOs.Orders;
using AlturaNova.Domain.Enums;

namespace AlturaNova.Application.Interfaces;

/// <summary>Order operations scoped to a single authenticated user, plus admin transitions.</summary>
public interface IOrderService
{
    /// <summary>
    /// Create an order from the current cart (authenticated) or from items in the request (guest).
    /// When <paramref name="userId"/> is null, <paramref name="request"/>.Items and .Email must be provided.
    /// </summary>
    Task<OrderResponse> CheckoutAsync(Guid? userId, CheckoutRequest request, CancellationToken ct = default);
    Task<OrderListResponse> GetOrdersAsync(Guid userId, CancellationToken ct = default);
    Task<OrderResponse> GetOrderAsync(Guid userId, Guid orderId, CancellationToken ct = default);

    /// <summary>Initialize a hosted-checkout payment for a pending order. Owner-scoped.</summary>
    Task<PaymentInitiationResponse> InitiatePaymentAsync(Guid? userId, Guid orderId, string? callbackUrl, CancellationToken ct = default);

    /// <summary>Verify a pending order's payment with the provider and mark it paid on success. Owner-scoped.</summary>
    Task<OrderResponse> VerifyPaymentAsync(Guid? userId, Guid orderId, CancellationToken ct = default);

    /// <summary>Confirm payment from a provider webhook, located by reference. Idempotent, not owner-scoped.</summary>
    Task ConfirmPaymentByReferenceAsync(string reference, CancellationToken ct = default);

    /// <summary>Cancel a pending/paid order and restock inventory. Owner-scoped.</summary>
    Task<OrderResponse> CancelAsync(Guid userId, Guid orderId, CancellationToken ct = default);

    /// <summary>Admin: list every order.</summary>
    Task<OrderListResponse> GetAllAsync(CancellationToken ct = default);

    /// <summary>Admin: transition an order to a new status.</summary>
    Task<OrderResponse> UpdateStatusAsync(Guid orderId, OrderStatus status, CancellationToken ct = default);
}
