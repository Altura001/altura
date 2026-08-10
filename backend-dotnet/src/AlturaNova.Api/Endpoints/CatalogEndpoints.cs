using AlturaNova.Application.DTOs.Catalog;
using AlturaNova.Application.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;

namespace AlturaNova.Api.Endpoints;

/// <summary>Public catalog endpoints for vendors and products.</summary>
public static class CatalogEndpoints
{
    public static void MapCatalogEndpoints(this IEndpointRouteBuilder app)
    {
        var vendors = app.MapGroup("/vendors").WithTags("Catalog");

        vendors.MapGet("/", async Task<Ok<IReadOnlyList<VendorResponse>>> (
                ICatalogService catalog, CancellationToken ct) =>
                TypedResults.Ok(await catalog.GetVendorsAsync(ct)))
            .WithName("GetVendors")
            .WithSummary("List all active vendors")
            .Produces<IReadOnlyList<VendorResponse>>(StatusCodes.Status200OK);

        vendors.MapGet("/{id:guid}", async Task<Ok<VendorResponse>> (
                Guid id, ICatalogService catalog, CancellationToken ct) =>
                TypedResults.Ok(await catalog.GetVendorAsync(id, ct)))
            .WithName("GetVendor")
            .WithSummary("Get a vendor by id")
            .Produces<VendorResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound);

        vendors.MapGet("/{id:guid}/products", async Task<Ok<ProductListResponse>> (
                Guid id, string? search, int? page, int? pageSize,
                ICatalogService catalog, CancellationToken ct) =>
                TypedResults.Ok(await catalog.GetProductsAsync(new ProductQuery
                {
                    VendorId = id,
                    Search = search,
                    Page = page is > 0 ? page.Value : 1,
                    PageSize = pageSize is > 0 ? pageSize.Value : 50
                }, ct)))
            .WithName("GetVendorProducts")
            .WithSummary("List products for a vendor")
            .Produces<ProductListResponse>(StatusCodes.Status200OK);

        app.MapGet("/categories", async Task<Ok<IReadOnlyList<CategoryResponse>>> (
                ICatalogService catalog, CancellationToken ct) =>
                TypedResults.Ok(await catalog.GetCategoriesAsync(ct)))
            .WithTags("Catalog")
            .WithName("GetCategories")
            .WithSummary("List all product categories")
            .Produces<IReadOnlyList<CategoryResponse>>(StatusCodes.Status200OK);

        var products = app.MapGroup("/products").WithTags("Catalog");

        products.MapGet("/", async Task<Ok<ProductListResponse>> (
                string? search, Guid? vendorId, Guid? categoryId, int? page, int? pageSize,
                ICatalogService catalog, CancellationToken ct) =>
                TypedResults.Ok(await catalog.GetProductsAsync(new ProductQuery
                {
                    Search = search,
                    VendorId = vendorId,
                    CategoryId = categoryId,
                    Page = page is > 0 ? page.Value : 1,
                    PageSize = pageSize is > 0 ? pageSize.Value : 50
                }, ct)))
            .WithName("GetProducts")
            .WithSummary("List and filter products")
            .WithDescription("Supports search term, vendor/category filters, and pagination.")
            .Produces<ProductListResponse>(StatusCodes.Status200OK);

        products.MapGet("/{id:guid}", async Task<Ok<ProductResponse>> (
                Guid id, ICatalogService catalog, CancellationToken ct) =>
                TypedResults.Ok(await catalog.GetProductByIdAsync(id, ct)))
            .WithName("GetProductById")
            .WithSummary("Get a product by id")
            .Produces<ProductResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound);

        products.MapGet("/handle/{handle}", async Task<Ok<ProductResponse>> (
                string handle, ICatalogService catalog, CancellationToken ct) =>
                TypedResults.Ok(await catalog.GetProductByHandleAsync(handle, ct)))
            .WithName("GetProductByHandle")
            .WithSummary("Get a product by its handle/slug")
            .Produces<ProductResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound);
    }
}
