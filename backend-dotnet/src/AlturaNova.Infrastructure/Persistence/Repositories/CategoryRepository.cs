using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace AlturaNova.Infrastructure.Persistence.Repositories;

/// <summary>EF Core implementation of <see cref="ICategoryRepository"/>.</summary>
public sealed class CategoryRepository(AppDbContext db) : ICategoryRepository
{
    public async Task<IReadOnlyList<Category>> GetAllAsync(CancellationToken ct = default) =>
        await db.Categories.AsNoTracking().OrderBy(c => c.Name).ToListAsync(ct);

    public Task<Category?> GetByIdAsync(Guid id, CancellationToken ct = default) =>
        db.Categories.AsNoTracking().FirstOrDefaultAsync(c => c.Id == id, ct);

    public Task<bool> ExistsAsync(Guid id, CancellationToken ct = default) =>
        db.Categories.AnyAsync(c => c.Id == id, ct);
}
