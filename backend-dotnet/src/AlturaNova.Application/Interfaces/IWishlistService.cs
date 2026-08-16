using AlturaNova.Application.DTOs.Wishlist;

namespace AlturaNova.Application.Interfaces;

/// <summary>Wishlist operations scoped to a single authenticated user.</summary>
public interface IWishlistService
{
    Task<WishlistResponse> GetWishlistAsync(Guid userId, CancellationToken ct = default);
    Task<WishlistResponse> AddItemAsync(Guid userId, AddWishlistItemRequest request, CancellationToken ct = default);
    Task<WishlistResponse> RemoveItemAsync(Guid userId, Guid productId, CancellationToken ct = default);
    Task<WishlistResponse> ToggleItemAsync(Guid userId, Guid productId, CancellationToken ct = default);
    Task<bool> IsProductWishlistedAsync(Guid userId, Guid productId, CancellationToken ct = default);
}
