using AlturaNova.Domain.Entities;

namespace AlturaNova.Domain.Interfaces;

/// <summary>Data access for <see cref="Vendor"/> aggregates.</summary>
public interface IVendorRepository
{
    Task<IReadOnlyList<Vendor>> GetAllActiveAsync(CancellationToken ct = default);
    Task<Vendor?> GetByIdAsync(Guid id, CancellationToken ct = default);

    /// <summary>Returns a change-tracked vendor for mutation (store profile updates).</summary>
    Task<Vendor?> GetTrackedByIdAsync(Guid id, CancellationToken ct = default);

    Task<Vendor?> GetByHandleAsync(string handle, CancellationToken ct = default);
    Task<bool> HandleExistsAsync(string handle, CancellationToken ct = default);
    Task AddAsync(Vendor vendor, CancellationToken ct = default);
    void Update(Vendor vendor);
}
