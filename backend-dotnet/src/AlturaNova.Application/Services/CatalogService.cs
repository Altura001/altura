using AlturaNova.Application.Common.Mapping;
using AlturaNova.Application.DTOs.Catalog;
using AlturaNova.Application.Interfaces;
using AlturaNova.Domain.Exceptions;
using AlturaNova.Domain.Interfaces;

namespace AlturaNova.Application.Services;

/// <summary>Implements read access to vendors, categories, and products.</summary>
public sealed class CatalogService(
    IVendorRepository vendors,
    ICategoryRepository categories,
    IProductRepository products) : ICatalogService
{
    private const int MaxPageSize = 200;

    public async Task<IReadOnlyList<VendorResponse>> GetVendorsAsync(CancellationToken ct = default)
    {
        var list = await vendors.GetAllActiveAsync(ct);
        return list.Select(v => v.ToResponse()).ToList();
    }

    public async Task<VendorResponse> GetVendorAsync(Guid id, CancellationToken ct = default)
    {
        var vendor = await vendors.GetByIdAsync(id, ct)
            ?? throw new NotFoundException("Vendor not found.");
        return vendor.ToResponse();
    }

    public async Task<IReadOnlyList<CategoryResponse>> GetCategoriesAsync(CancellationToken ct = default)
    {
        var list = await categories.GetAllAsync(ct);
        return list.Select(c => c.ToResponse()).ToList();
    }

    public async Task<ProductListResponse> GetProductsAsync(ProductQuery query, CancellationToken ct = default)
    {
        var page = query.Page < 1 ? 1 : query.Page;
        var pageSize = Math.Clamp(query.PageSize, 1, MaxPageSize);

        var criteria = new ProductSearchCriteria
        {
            SearchTerm = string.IsNullOrWhiteSpace(query.Search) ? null : query.Search.Trim(),
            VendorId = query.VendorId,
            CategoryId = query.CategoryId,
            Page = page,
            PageSize = pageSize
        };

        var (items, total) = await products.SearchAsync(criteria, ct);
        return new ProductListResponse(
            items.Select(p => p.ToResponse()).ToList(),
            total,
            page,
            pageSize);
    }

    public async Task<ProductResponse> GetProductByIdAsync(Guid id, CancellationToken ct = default)
    {
        var product = await products.GetByIdAsync(id, ct)
            ?? throw new NotFoundException("Product not found.");
        return product.ToResponse();
    }

    public async Task<ProductResponse> GetProductByHandleAsync(string handle, CancellationToken ct = default)
    {
        var product = await products.GetByHandleAsync(handle, ct)
            ?? throw new NotFoundException("Product not found.");
        return product.ToResponse();
    }
}
