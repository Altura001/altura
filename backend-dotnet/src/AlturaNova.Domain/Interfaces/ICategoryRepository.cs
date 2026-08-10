using AlturaNova.Domain.Entities;

namespace AlturaNova.Domain.Interfaces;

/// <summary>Data access for <see cref="Category"/> records.</summary>
public interface ICategoryRepository
{
    Task<IReadOnlyList<Category>> GetAllAsync(CancellationToken ct = default);
    Task<Category?> GetByIdAsync(Guid id, CancellationToken ct = default);
    Task<bool> ExistsAsync(Guid id, CancellationToken ct = default);
}
