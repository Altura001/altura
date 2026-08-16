using AlturaNova.Domain.Entities;

namespace AlturaNova.Domain.Interfaces;

/// <summary>Data access for <see cref="WishlistItem"/> entities.</summary>
public interface IWishlistRepository
{
    Task<List<WishlistItem>> GetAllByUserAsync(Guid userId, CancellationToken ct = default);
    Task<WishlistItem?> FindAsync(Guid userId, Guid productId, CancellationToken ct = default);
    Task AddAsync(WishlistItem item, CancellationToken ct = default);
    void Remove(WishlistItem item);
}
