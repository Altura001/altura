using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace AlturaNova.Infrastructure.Persistence.Repositories;

/// <summary>EF Core implementation of <see cref="IVendorRepository"/>.</summary>
public sealed class VendorRepository(AppDbContext db) : IVendorRepository
{
    public async Task<IReadOnlyList<Vendor>> GetAllActiveAsync(CancellationToken ct = default) =>
        await db.Vendors.AsNoTracking()
            .Where(v => v.IsActive)
            .OrderBy(v => v.Name)
            .ToListAsync(ct);

    public Task<Vendor?> GetByIdAsync(Guid id, CancellationToken ct = default) =>
        db.Vendors.AsNoTracking().FirstOrDefaultAsync(v => v.Id == id, ct);

    public Task<Vendor?> GetTrackedByIdAsync(Guid id, CancellationToken ct = default) =>
        db.Vendors.FirstOrDefaultAsync(v => v.Id == id, ct);

    public Task<Vendor?> GetByHandleAsync(string handle, CancellationToken ct = default) =>
        db.Vendors.AsNoTracking().FirstOrDefaultAsync(v => v.Handle == handle, ct);

    public Task<bool> HandleExistsAsync(string handle, CancellationToken ct = default) =>
        db.Vendors.AnyAsync(v => v.Handle == handle, ct);

    public async Task AddAsync(Vendor vendor, CancellationToken ct = default) =>
        await db.Vendors.AddAsync(vendor, ct);

    public void Update(Vendor vendor) => db.Vendors.Update(vendor);
}
