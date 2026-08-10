using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace AlturaNova.Infrastructure.Persistence.Repositories;

/// <summary>EF Core implementation of <see cref="IProductRepository"/>.</summary>
public sealed class ProductRepository(AppDbContext db) : IProductRepository
{
    public async Task<(IReadOnlyList<Product> Items, int Total)> SearchAsync(
        ProductSearchCriteria criteria, CancellationToken ct = default)
    {
        var query = db.Products.AsNoTracking().Where(p => p.IsPublished);

        if (!string.IsNullOrWhiteSpace(criteria.SearchTerm))
        {
            var term = $"%{criteria.SearchTerm}%";
            query = query.Where(p => EF.Functions.ILike(p.Name, term));
        }

        if (criteria.VendorId is { } vendorId)
            query = query.Where(p => p.VendorId == vendorId);

        if (criteria.CategoryId is { } categoryId)
            query = query.Where(p => p.CategoryId == categoryId);

        var total = await query.CountAsync(ct);

        var items = await query
            .OrderByDescending(p => p.CreatedAt)
            .Skip((criteria.Page - 1) * criteria.PageSize)
            .Take(criteria.PageSize)
            .Include(p => p.Vendor)
            .Include(p => p.Category)
            .Include(p => p.Variants)
            .Include(p => p.Images)
            .AsSplitQuery()
            .ToListAsync(ct);

        return (items, total);
    }

    public Task<Product?> GetByIdAsync(Guid id, CancellationToken ct = default) =>
        WithGraph(db.Products.AsNoTracking()).FirstOrDefaultAsync(p => p.Id == id, ct);

    public Task<Product?> GetByHandleAsync(string handle, CancellationToken ct = default) =>
        WithGraph(db.Products.AsNoTracking()).FirstOrDefaultAsync(p => p.Handle == handle, ct);

    public async Task<IReadOnlyList<Product>> GetByVendorAsync(Guid vendorId, bool includeUnpublished, CancellationToken ct = default)
    {
        var query = db.Products.AsNoTracking().Where(p => p.VendorId == vendorId);
        if (!includeUnpublished)
            query = query.Where(p => p.IsPublished);

        return await WithGraph(query)
            .OrderByDescending(p => p.CreatedAt)
            .ToListAsync(ct);
    }

    // Tracked graph (variants + images) for mutation.
    public Task<Product?> GetTrackedByIdAsync(Guid id, CancellationToken ct = default) =>
        db.Products
            .Include(p => p.Variants)
            .Include(p => p.Images)
            .AsSplitQuery()
            .FirstOrDefaultAsync(p => p.Id == id, ct);

    public Task<bool> HandleExistsAsync(string handle, CancellationToken ct = default) =>
        db.Products.AnyAsync(p => p.Handle == handle, ct);

    // Tracked (no AsNoTracking): callers may decrement/restock inventory.
    public Task<ProductVariant?> GetVariantAsync(Guid variantId, CancellationToken ct = default) =>
        db.ProductVariants
            .Include(v => v.Product)
            .FirstOrDefaultAsync(v => v.Id == variantId, ct);

    public async Task AddAsync(Product product, CancellationToken ct = default) =>
        await db.Products.AddAsync(product, ct);

    public void Update(Product product) => db.Products.Update(product);

    public void Remove(Product product) => db.Products.Remove(product);

    private static IQueryable<Product> WithGraph(IQueryable<Product> query) =>
        query
            .Include(p => p.Vendor)
            .Include(p => p.Category)
            .Include(p => p.Variants)
            .Include(p => p.Images)
            .AsSplitQuery();
}
