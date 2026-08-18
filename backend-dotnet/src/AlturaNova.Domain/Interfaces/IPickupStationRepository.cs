using AlturaNova.Domain.Entities;

namespace AlturaNova.Domain.Interfaces;

/// <summary>Data access for <see cref="PickupStation"/> entities.</summary>
public interface IPickupStationRepository
{
    Task<List<PickupStation>> GetActiveAsync(CancellationToken ct = default);
    Task<PickupStation?> GetByIdAsync(Guid id, CancellationToken ct = default);
}
