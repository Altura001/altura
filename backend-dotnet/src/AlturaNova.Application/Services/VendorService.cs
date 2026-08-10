using AlturaNova.Application.Common;
using AlturaNova.Application.Common.Mapping;
using AlturaNova.Application.DTOs.Catalog;
using AlturaNova.Application.DTOs.Orders;
using AlturaNova.Application.DTOs.Vendor;
using AlturaNova.Application.Interfaces;
using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Exceptions;
using AlturaNova.Domain.Interfaces;

namespace AlturaNova.Application.Services;

/// <summary>Implements the vendor console: store profile, product management, and order views.</summary>
public sealed class VendorService(
    IVendorRepository vendors,
    IProductRepository products,
    ICategoryRepository categories,
    IOrderRepository orders,
    IUnitOfWork unitOfWork) : IVendorService
{
    // ----- Store profile -----------------------------------------------------

    public async Task<VendorStoreResponse> GetMyStoreAsync(Guid vendorId, CancellationToken ct = default)
    {
        var vendor = await vendors.GetByIdAsync(vendorId, ct)
            ?? throw new NotFoundException("Store not found.");
        return vendor.ToStoreResponse();
    }

    public async Task<VendorStoreResponse> UpdateMyStoreAsync(Guid vendorId, UpdateVendorStoreRequest request, CancellationToken ct = default)
    {
        var vendor = await vendors.GetTrackedByIdAsync(vendorId, ct)
            ?? throw new NotFoundException("Store not found.");

        vendor.Name = request.Name.Trim();
        vendor.Description = request.Description?.Trim() ?? string.Empty;
        vendor.LogoUrl = request.LogoUrl;
        vendor.BannerUrl = request.BannerUrl;
        vendors.Update(vendor);

        await unitOfWork.SaveChangesAsync(ct);
        return vendor.ToStoreResponse();
    }

    // ----- Products ----------------------------------------------------------

    public async Task<IReadOnlyList<ProductResponse>> GetMyProductsAsync(Guid vendorId, CancellationToken ct = default)
    {
        var list = await products.GetByVendorAsync(vendorId, includeUnpublished: true, ct);
        return list.Select(p => p.ToResponse()).ToList();
    }

    public async Task<ProductResponse> GetMyProductAsync(Guid vendorId, Guid productId, CancellationToken ct = default)
    {
        var product = await products.GetByIdAsync(productId, ct);
        if (product is null || product.VendorId != vendorId)
            throw new NotFoundException("Product not found.");
        return product.ToResponse();
    }

    public async Task<ProductResponse> CreateProductAsync(Guid vendorId, CreateProductRequest request, CancellationToken ct = default)
    {
        await ValidateCategoryAsync(request.CategoryId, ct);

        var currency = NormalizeCurrency(request.Currency);
        var handleSeed = string.IsNullOrWhiteSpace(request.Handle) ? request.Name : request.Handle!;
        var handle = await GenerateUniqueHandleAsync(handleSeed, ct);

        var product = new Product
        {
            VendorId = vendorId,
            CategoryId = request.CategoryId,
            Name = request.Name.Trim(),
            Handle = handle,
            Description = request.Description?.Trim() ?? string.Empty,
            ThumbnailUrl = request.ThumbnailUrl,
            Currency = currency,
            IsPublished = request.IsPublished
        };

        foreach (var v in request.Variants)
            product.Variants.Add(NewVariant(v, currency));

        AddImages(product, request.Images);

        await products.AddAsync(product, ct);
        await unitOfWork.SaveChangesAsync(ct);

        return await ReloadAsync(product.Id, ct);
    }

    public async Task<ProductResponse> UpdateProductAsync(Guid vendorId, Guid productId, UpdateProductRequest request, CancellationToken ct = default)
    {
        var product = await products.GetTrackedByIdAsync(productId, ct);
        if (product is null || product.VendorId != vendorId)
            throw new NotFoundException("Product not found.");

        await ValidateCategoryAsync(request.CategoryId, ct);

        var currency = NormalizeCurrency(request.Currency);
        product.Name = request.Name.Trim();
        product.Description = request.Description?.Trim() ?? string.Empty;
        product.ThumbnailUrl = request.ThumbnailUrl;
        product.Currency = currency;
        product.CategoryId = request.CategoryId;
        product.IsPublished = request.IsPublished;
        product.UpdatedAt = DateTimeOffset.UtcNow;

        ReconcileVariants(product, request.Variants, currency);

        // Replace images wholesale (they carry no external references).
        product.Images.Clear();
        AddImages(product, request.Images);

        products.Update(product);
        await unitOfWork.SaveChangesAsync(ct);

        return await ReloadAsync(product.Id, ct);
    }

    public async Task DeleteProductAsync(Guid vendorId, Guid productId, CancellationToken ct = default)
    {
        var product = await products.GetTrackedByIdAsync(productId, ct);
        if (product is null || product.VendorId != vendorId)
            throw new NotFoundException("Product not found.");

        products.Remove(product);
        await unitOfWork.SaveChangesAsync(ct);
    }

    public async Task<ProductResponse> SetPublishAsync(Guid vendorId, Guid productId, bool isPublished, CancellationToken ct = default)
    {
        var product = await products.GetTrackedByIdAsync(productId, ct);
        if (product is null || product.VendorId != vendorId)
            throw new NotFoundException("Product not found.");

        product.IsPublished = isPublished;
        product.UpdatedAt = DateTimeOffset.UtcNow;
        products.Update(product);
        await unitOfWork.SaveChangesAsync(ct);

        return await ReloadAsync(product.Id, ct);
    }

    // ----- Orders ------------------------------------------------------------

    public async Task<VendorOrderListResponse> GetMyOrdersAsync(Guid vendorId, CancellationToken ct = default)
    {
        var list = await orders.GetForVendorAsync(vendorId, ct);

        var result = list.Select(order =>
        {
            var vendorItems = order.Items
                .Where(i => i.VendorId == vendorId)
                .Select(i => i.ToResponse())
                .ToList();

            return new VendorOrderResponse(
                order.Id,
                order.Status.ToString(),
                order.Currency,
                vendorItems.Sum(i => i.LineTotal),
                vendorItems,
                order.CreatedAt);
        }).ToList();

        return new VendorOrderListResponse(result, result.Count);
    }

    // ----- Helpers -----------------------------------------------------------

    private async Task<ProductResponse> ReloadAsync(Guid productId, CancellationToken ct)
    {
        var product = await products.GetByIdAsync(productId, ct)
            ?? throw new NotFoundException("Product not found.");
        return product.ToResponse();
    }

    private static ProductVariant NewVariant(VariantInput v, string currency) => new()
    {
        Title = v.Title.Trim(),
        Sku = string.IsNullOrWhiteSpace(v.Sku) ? null : v.Sku.Trim(),
        Price = v.Price,
        Currency = currency,
        InventoryQuantity = v.InventoryQuantity
    };

    private static void ReconcileVariants(Product product, IReadOnlyList<VariantInput> inputs, string currency)
    {
        var incomingIds = inputs.Where(i => i.Id is not null).Select(i => i.Id!.Value).ToHashSet();

        // Remove variants that are no longer present.
        foreach (var existing in product.Variants.Where(v => !incomingIds.Contains(v.Id)).ToList())
            product.Variants.Remove(existing);

        foreach (var input in inputs)
        {
            var existing = input.Id is { } id ? product.Variants.FirstOrDefault(v => v.Id == id) : null;
            if (existing is not null)
            {
                existing.Title = input.Title.Trim();
                existing.Sku = string.IsNullOrWhiteSpace(input.Sku) ? null : input.Sku.Trim();
                existing.Price = input.Price;
                existing.Currency = currency;
                existing.InventoryQuantity = input.InventoryQuantity;
            }
            else
            {
                product.Variants.Add(NewVariant(input, currency));
            }
        }
    }

    private static void AddImages(Product product, IReadOnlyList<string> urls)
    {
        var order = 0;
        foreach (var url in urls.Where(u => !string.IsNullOrWhiteSpace(u)))
            product.Images.Add(new ProductImage { Url = url.Trim(), SortOrder = order++ });
    }

    private async Task ValidateCategoryAsync(Guid? categoryId, CancellationToken ct)
    {
        if (categoryId is { } id && !await categories.ExistsAsync(id, ct))
            throw new DomainValidationException("The specified category does not exist.");
    }

    private async Task<string> GenerateUniqueHandleAsync(string seed, CancellationToken ct)
    {
        var baseHandle = Slug.From(seed);
        var handle = baseHandle;
        var suffix = 1;
        while (await products.HandleExistsAsync(handle, ct))
            handle = $"{baseHandle}-{++suffix}";
        return handle;
    }

    private static string NormalizeCurrency(string currency) => currency.Trim().ToUpperInvariant();
}
