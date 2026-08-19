using System.ComponentModel.DataAnnotations;

namespace AlturaNova.Application.DTOs.Orders;

/// <summary>A shipping address supplied at checkout.</summary>
public sealed record AddressRequest
{
    [Required, MaxLength(100)]
    public required string FirstName { get; init; }

    [Required, MaxLength(100)]
    public required string LastName { get; init; }

    [Required, MaxLength(200)]
    public required string Line1 { get; init; }

    [MaxLength(200)]
    public string? Line2 { get; init; }

    [Required, MaxLength(100)]
    public required string City { get; init; }

    [MaxLength(100)]
    public string? State { get; init; }

    [Required, MaxLength(20)]
    public required string PostalCode { get; init; }

    [Required, MaxLength(2), MinLength(2)]
    public required string Country { get; init; }

    [Phone, MaxLength(40)]
    public string? Phone { get; init; }
}

/// <summary>A single item in a guest checkout request (sent when no server-side cart exists).</summary>
public sealed record CheckoutItemRequest
{
    [Required]
    public required Guid VariantId { get; init; }

    [Required, Range(1, 9999)]
    public int Quantity { get; init; }
}

/// <summary>Payload for converting the current cart into an order.</summary>
public sealed record CheckoutRequest
{
    [Required]
    public required AddressRequest ShippingAddress { get; init; }

    /// <summary>Delivery method: "Shipping" (door delivery) or "Pickup" (station).</summary>
    [MaxLength(20)]
    public string? DeliveryMethod { get; init; }

    /// <summary>Required when DeliveryMethod is "Pickup".</summary>
    public Guid? PickupStationId { get; init; }

    /// <summary>
    /// Guest checkout: cart items sent by the client when no server-side cart exists.
    /// If null/empty and the user is authenticated, the server-side cart is used.
    /// </summary>
    public List<CheckoutItemRequest>? Items { get; init; }

    /// <summary>
    /// Guest checkout: customer email for payment provider (Paystack).
    /// Required for guests; authenticated users get their email from their account.
    /// </summary>
    [EmailAddress]
    public string? Email { get; init; }
}
