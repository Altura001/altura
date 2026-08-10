using AlturaNova.Domain.Entities;

namespace AlturaNova.Domain.Interfaces;

/// <summary>Data access for <see cref="Cart"/> aggregates (includes items).</summary>
public interface ICartRepository
{
    /// <summary>Returns the user's active cart (with items), or null if none exists.</summary>
    Task<Cart?> GetActiveByUserAsync(Guid userId, CancellationToken ct = default);
    Task AddAsync(Cart cart, CancellationToken ct = default);
    void Remove(Cart cart);
}
