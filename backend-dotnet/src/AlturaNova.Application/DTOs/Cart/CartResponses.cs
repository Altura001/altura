namespace AlturaNova.Application.DTOs.Cart;

/// <summary>Represents a single line item in the cart.</summary>
public sealed record CartItemResponse(
    Guid Id,
    Guid ProductId,
    Guid VariantId,
    string Title,
    int Quantity,
    decimal UnitPrice,
    decimal LineTotal,
    string Currency,
    string? ThumbnailUrl);

/// <summary>Represents the user's cart with its items and computed totals.</summary>
public sealed record CartResponse(
    Guid Id,
    IReadOnlyList<CartItemResponse> Items,
    decimal Subtotal,
    decimal Total,
    string Currency,
    int ItemCount);
