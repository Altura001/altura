using AlturaNova.Application.DTOs.Catalog;

namespace AlturaNova.Application.Interfaces;

/// <summary>Query parameters for listing products.</summary>
public sealed record ProductQuery
{
    public string? Search { get; init; }
    public Guid? VendorId { get; init; }
    public Guid? CategoryId { get; init; }
    public int Page { get; init; } = 1;
    public int PageSize { get; init; } = 50;
}

/// <summary>Read operations for vendors and products.</summary>
public interface ICatalogService
{
    Task<IReadOnlyList<VendorResponse>> GetVendorsAsync(CancellationToken ct = default);
    Task<VendorResponse> GetVendorAsync(Guid id, CancellationToken ct = default);
    Task<IReadOnlyList<CategoryResponse>> GetCategoriesAsync(CancellationToken ct = default);
    Task<ProductListResponse> GetProductsAsync(ProductQuery query, CancellationToken ct = default);
    Task<ProductResponse> GetProductByIdAsync(Guid id, CancellationToken ct = default);
    Task<ProductResponse> GetProductByHandleAsync(string handle, CancellationToken ct = default);
}
