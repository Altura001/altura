namespace AlturaNova.Application.DTOs.Orders;

/// <summary>A shipping address returned on an order.</summary>
public sealed record AddressResponse(
    string FirstName,
    string LastName,
    string Line1,
    string? Line2,
    string City,
    string? State,
    string PostalCode,
    string Country,
    string? Phone);

/// <summary>A single line item captured on an order.</summary>
public sealed record OrderItemResponse(
    Guid Id,
    Guid ProductId,
    Guid VariantId,
    string ProductName,
    string? Sku,
    string? ThumbnailUrl,
    decimal UnitPrice,
    int Quantity,
    decimal LineTotal,
    string Currency);

/// <summary>Represents a placed order.</summary>
public sealed record OrderResponse(
    Guid Id,
    string Status,
    string DeliveryMethod,
    string? PickupStationName,
    decimal Subtotal,
    decimal ShippingFee,
    decimal Total,
    string Currency,
    AddressResponse? ShippingAddress,
    IReadOnlyList<OrderItemResponse> Items,
    DateTimeOffset CreatedAt);

/// <summary>A list of the user's orders.</summary>
public sealed record OrderListResponse(
    IReadOnlyList<OrderResponse> Items,
    int Total);
