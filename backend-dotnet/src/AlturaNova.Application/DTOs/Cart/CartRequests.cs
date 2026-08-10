using System.ComponentModel.DataAnnotations;

namespace AlturaNova.Application.DTOs.Cart;

/// <summary>Payload for adding a product variant to the cart.</summary>
public sealed record AddCartItemRequest
{
    [Required]
    public required Guid VariantId { get; init; }

    [Range(1, 999)]
    public int Quantity { get; init; } = 1;
}

/// <summary>Payload for changing the quantity of a cart line item.</summary>
public sealed record UpdateCartItemRequest
{
    [Range(0, 999)]
    public required int Quantity { get; init; }
}
