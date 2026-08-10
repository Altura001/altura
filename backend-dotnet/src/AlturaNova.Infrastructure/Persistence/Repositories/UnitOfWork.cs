using AlturaNova.Domain.Interfaces;
using AlturaNova.Infrastructure.Persistence;

namespace AlturaNova.Infrastructure.Persistence.Repositories;

/// <summary>Commits tracked changes on the shared <see cref="AppDbContext"/>.</summary>
public sealed class UnitOfWork(AppDbContext db) : IUnitOfWork
{
    public Task<int> SaveChangesAsync(CancellationToken cancellationToken = default) =>
        db.SaveChangesAsync(cancellationToken);
}
