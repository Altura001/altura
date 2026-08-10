using AlturaNova.Domain.Entities;

namespace AlturaNova.Domain.Interfaces;

/// <summary>Filter/paging criteria for product search.</summary>
public sealed record ProductSearchCriteria
{
    public string? SearchTerm { get; init; }
    public Guid? VendorId { get; init; }
    public Guid? CategoryId { get; init; }
    public int Page { get; init; } = 1;
    public int PageSize { get; init; } = 50;
}

/// <summary>Data access for <see cref="Product"/> aggregates (includes variants and images).</summary>
public interface IProductRepository
{
    Task<(IReadOnlyList<Product> Items, int Total)> SearchAsync(
        ProductSearchCriteria criteria, CancellationToken ct = default);

    Task<Product?> GetByIdAsync(Guid id, CancellationToken ct = default);
    Task<Product?> GetByHandleAsync(string handle, CancellationToken ct = default);

    /// <summary>Products owned by a vendor. Set <paramref name="includeUnpublished"/> for the vendor console.</summary>
    Task<IReadOnlyList<Product>> GetByVendorAsync(Guid vendorId, bool includeUnpublished, CancellationToken ct = default);

    /// <summary>Change-tracked product (with variants and images) for mutation.</summary>
    Task<Product?> GetTrackedByIdAsync(Guid id, CancellationToken ct = default);

    Task<bool> HandleExistsAsync(string handle, CancellationToken ct = default);

    Task<ProductVariant?> GetVariantAsync(Guid variantId, CancellationToken ct = default);
    Task AddAsync(Product product, CancellationToken ct = default);
    void Update(Product product);
    void Remove(Product product);
}
