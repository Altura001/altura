using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace AlturaNova.Infrastructure.Persistence.Repositories;

/// <summary>EF Core implementation of <see cref="IPickupStationRepository"/>.</summary>
public sealed class PickupStationRepository(AppDbContext db) : IPickupStationRepository
{
    public Task<List<PickupStation>> GetActiveAsync(CancellationToken ct = default) =>
        db.PickupStations
            .Where(s => s.IsActive)
            .OrderBy(s => s.Name)
            .ToListAsync(ct);

    public Task<PickupStation?> GetByIdAsync(Guid id, CancellationToken ct = default) =>
        db.PickupStations.FirstOrDefaultAsync(s => s.Id == id, ct);
}
