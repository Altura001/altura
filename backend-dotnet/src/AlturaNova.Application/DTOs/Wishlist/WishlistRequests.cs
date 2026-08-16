using System.ComponentModel.DataAnnotations;

namespace AlturaNova.Application.DTOs.Wishlist;

/// <summary>Payload for adding a product to the wishlist.</summary>
public sealed record AddWishlistItemRequest
{
    [Required]
    public required Guid ProductId { get; init; }
}
