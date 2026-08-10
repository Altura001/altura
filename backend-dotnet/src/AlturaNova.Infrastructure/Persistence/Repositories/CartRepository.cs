using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace AlturaNova.Infrastructure.Persistence.Repositories;

/// <summary>EF Core implementation of <see cref="ICartRepository"/>. Returns tracked entities for mutation.</summary>
public sealed class CartRepository(AppDbContext db) : ICartRepository
{
    public Task<Cart?> GetActiveByUserAsync(Guid userId, CancellationToken ct = default) =>
        db.Carts
            .Include(c => c.Items)
            .FirstOrDefaultAsync(c => c.UserId == userId, ct);

    public async Task AddAsync(Cart cart, CancellationToken ct = default) =>
        await db.Carts.AddAsync(cart, ct);

    public void Remove(Cart cart) => db.Carts.Remove(cart);
}
