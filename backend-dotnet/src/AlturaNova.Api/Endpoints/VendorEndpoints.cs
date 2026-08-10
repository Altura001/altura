using AlturaNova.Api.Filters;
using AlturaNova.Application.Common.Security;
using AlturaNova.Application.DTOs.Catalog;
using AlturaNova.Application.DTOs.Orders;
using AlturaNova.Application.DTOs.Vendor;
using AlturaNova.Application.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;

namespace AlturaNova.Api.Endpoints;

/// <summary>Vendor console endpoints (store profile, product management, order views).</summary>
public static class VendorEndpoints
{
    public static void MapVendorEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/vendor")
            .WithTags("Vendor")
            .RequireAuthorization(policy => policy.RequireRole("Vendor"))
            .AddEndpointFilter<DataAnnotationsValidationFilter>();

        // ----- Store profile -------------------------------------------------

        group.MapGet("/store", async Task<Ok<VendorStoreResponse>> (
                IVendorService vendor, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await vendor.GetMyStoreAsync(user.RequireVendorId(), ct)))
            .WithName("GetMyStore")
            .WithSummary("Get the authenticated vendor's store profile")
            .Produces<VendorStoreResponse>(StatusCodes.Status200OK);

        group.MapPut("/store", async Task<Ok<VendorStoreResponse>> (
                UpdateVendorStoreRequest request, IVendorService vendor, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await vendor.UpdateMyStoreAsync(user.RequireVendorId(), request, ct)))
            .WithName("UpdateMyStore")
            .WithSummary("Update the authenticated vendor's store profile")
            .Produces<VendorStoreResponse>(StatusCodes.Status200OK);

        // ----- Products ------------------------------------------------------

        group.MapGet("/products", async Task<Ok<IReadOnlyList<ProductResponse>>> (
                IVendorService vendor, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await vendor.GetMyProductsAsync(user.RequireVendorId(), ct)))
            .WithName("GetMyProducts")
            .WithSummary("List the vendor's products (including unpublished)")
            .Produces<IReadOnlyList<ProductResponse>>(StatusCodes.Status200OK);

        group.MapGet("/products/{id:guid}", async Task<Ok<ProductResponse>> (
                Guid id, IVendorService vendor, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await vendor.GetMyProductAsync(user.RequireVendorId(), id, ct)))
            .WithName("GetMyProduct")
            .WithSummary("Get one of the vendor's products")
            .Produces<ProductResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound);

        group.MapPost("/products", async Task<Created<ProductResponse>> (
                CreateProductRequest request, IVendorService vendor, ICurrentUser user, CancellationToken ct) =>
            {
                var product = await vendor.CreateProductAsync(user.RequireVendorId(), request, ct);
                return TypedResults.Created($"/api/vendor/products/{product.Id}", product);
            })
            .WithName("CreateProduct")
            .WithSummary("Create a new product")
            .Produces<ProductResponse>(StatusCodes.Status201Created)
            .ProducesProblem(StatusCodes.Status400BadRequest);

        group.MapPut("/products/{id:guid}", async Task<Ok<ProductResponse>> (
                Guid id, UpdateProductRequest request, IVendorService vendor, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await vendor.UpdateProductAsync(user.RequireVendorId(), id, request, ct)))
            .WithName("UpdateProduct")
            .WithSummary("Update a product (variants reconciled by id)")
            .Produces<ProductResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status400BadRequest)
            .ProducesProblem(StatusCodes.Status404NotFound);

        group.MapPatch("/products/{id:guid}/publish", async Task<Ok<ProductResponse>> (
                Guid id, SetPublishRequest request, IVendorService vendor, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await vendor.SetPublishAsync(user.RequireVendorId(), id, request.IsPublished, ct)))
            .WithName("SetProductPublish")
            .WithSummary("Publish or unpublish a product")
            .Produces<ProductResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound);

        group.MapDelete("/products/{id:guid}", async Task<NoContent> (
                Guid id, IVendorService vendor, ICurrentUser user, CancellationToken ct) =>
            {
                await vendor.DeleteProductAsync(user.RequireVendorId(), id, ct);
                return TypedResults.NoContent();
            })
            .WithName("DeleteProduct")
            .WithSummary("Delete a product")
            .Produces(StatusCodes.Status204NoContent)
            .ProducesProblem(StatusCodes.Status404NotFound);

        // ----- Orders --------------------------------------------------------

        group.MapGet("/orders", async Task<Ok<VendorOrderListResponse>> (
                IVendorService vendor, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await vendor.GetMyOrdersAsync(user.RequireVendorId(), ct)))
            .WithName("GetVendorOrders")
            .WithSummary("List orders containing the vendor's products (vendor-scoped items only)")
            .Produces<VendorOrderListResponse>(StatusCodes.Status200OK);
    }
}
