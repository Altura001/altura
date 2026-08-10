using System.ComponentModel.DataAnnotations;
using AlturaNova.Domain.Enums;

namespace AlturaNova.Application.DTOs.Orders;

/// <summary>Payload for starting payment on a pending order via the hosted checkout.</summary>
public sealed record InitiatePaymentRequest
{
    /// <summary>Optional URL the provider redirects to after payment (e.g. an app deep link).</summary>
    [MaxLength(500)]
    public string? CallbackUrl { get; init; }
}

/// <summary>Payload for an admin updating an order's status.</summary>
public sealed record UpdateOrderStatusRequest
{
    public required OrderStatus Status { get; init; }
}

/// <summary>A vendor-scoped view of an order, exposing only the vendor's own line items.</summary>
public sealed record VendorOrderResponse(
    Guid OrderId,
    string Status,
    string Currency,
    decimal VendorSubtotal,
    IReadOnlyList<OrderItemResponse> Items,
    DateTimeOffset CreatedAt);

/// <summary>A list of vendor-scoped orders.</summary>
public sealed record VendorOrderListResponse(
    IReadOnlyList<VendorOrderResponse> Items,
    int Total);
