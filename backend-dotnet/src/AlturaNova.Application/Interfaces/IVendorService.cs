using AlturaNova.Application.DTOs.Catalog;
using AlturaNova.Application.DTOs.Orders;
using AlturaNova.Application.DTOs.Vendor;

namespace AlturaNova.Application.Interfaces;

/// <summary>Vendor console operations, scoped to the authenticated vendor's own store.</summary>
public interface IVendorService
{
    Task<VendorStoreResponse> GetMyStoreAsync(Guid vendorId, CancellationToken ct = default);
    Task<VendorStoreResponse> UpdateMyStoreAsync(Guid vendorId, UpdateVendorStoreRequest request, CancellationToken ct = default);

    Task<IReadOnlyList<ProductResponse>> GetMyProductsAsync(Guid vendorId, CancellationToken ct = default);
    Task<ProductResponse> GetMyProductAsync(Guid vendorId, Guid productId, CancellationToken ct = default);
    Task<ProductResponse> CreateProductAsync(Guid vendorId, CreateProductRequest request, CancellationToken ct = default);
    Task<ProductResponse> UpdateProductAsync(Guid vendorId, Guid productId, UpdateProductRequest request, CancellationToken ct = default);
    Task DeleteProductAsync(Guid vendorId, Guid productId, CancellationToken ct = default);
    Task<ProductResponse> SetPublishAsync(Guid vendorId, Guid productId, bool isPublished, CancellationToken ct = default);

    Task<VendorOrderListResponse> GetMyOrdersAsync(Guid vendorId, CancellationToken ct = default);
}
