using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace AlturaNova.Infrastructure.Persistence.Repositories;

/// <summary>EF Core implementation of <see cref="IWishlistRepository"/>.</summary>
public sealed class WishlistRepository(AppDbContext db) : IWishlistRepository
{
    public Task<List<WishlistItem>> GetAllByUserAsync(Guid userId, CancellationToken ct = default) =>
        db.WishlistItems
            .Include(w => w.Product)
            .ThenInclude(p => p!.Variants)
            .Where(w => w.UserId == userId)
            .OrderByDescending(w => w.CreatedAt)
            .ToListAsync(ct);

    public Task<WishlistItem?> FindAsync(Guid userId, Guid productId, CancellationToken ct = default) =>
        db.WishlistItems
            .FirstOrDefaultAsync(w => w.UserId == userId && w.ProductId == productId, ct);

    public async Task AddAsync(WishlistItem item, CancellationToken ct = default) =>
        await db.WishlistItems.AddAsync(item, ct);

    public void Remove(WishlistItem item) => db.WishlistItems.Remove(item);
}
