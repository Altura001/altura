using AlturaNova.Api.Filters;
using AlturaNova.Application.Common.Security;
using AlturaNova.Application.DTOs.Wishlist;
using AlturaNova.Application.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;

namespace AlturaNova.Api.Endpoints;

/// <summary>Wishlist endpoints for the authenticated user.</summary>
public static class WishlistEndpoints
{
    public static void MapWishlistEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/wishlist")
            .WithTags("Wishlist")
            .RequireAuthorization()
            .AddEndpointFilter<DataAnnotationsValidationFilter>();

        group.MapGet("/", async Task<Ok<WishlistResponse>> (
                IWishlistService wishlist, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await wishlist.GetWishlistAsync(user.RequireUserId(), ct)))
            .WithName("GetWishlist")
            .WithSummary("Get the current user's wishlist")
            .Produces<WishlistResponse>(StatusCodes.Status200OK);

        group.MapGet("/check/{productId:guid}", async Task<Ok<bool>> (
                Guid productId, IWishlistService wishlist, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await wishlist.IsProductWishlistedAsync(user.RequireUserId(), productId, ct)))
            .WithName("CheckWishlist")
            .WithSummary("Check if a product is in the user's wishlist")
            .Produces<bool>(StatusCodes.Status200OK);

        group.MapPost("/", async Task<Results<Ok<WishlistResponse>, Conflict>> (
                AddWishlistItemRequest request, IWishlistService wishlist, ICurrentUser user, CancellationToken ct) =>
            {
                try
                {
                    var result = await wishlist.AddItemAsync(user.RequireUserId(), request, ct);
                    return TypedResults.Ok(result);
                }
                catch (Domain.Exceptions.ConflictException)
                {
                    return TypedResults.Conflict();
                }
            })
            .WithName("AddToWishlist")
            .WithSummary("Add a product to the wishlist")
            .Produces<WishlistResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound);

        group.MapDelete("/{productId:guid}", async Task<Ok<WishlistResponse>> (
                Guid productId, IWishlistService wishlist, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await wishlist.RemoveItemAsync(user.RequireUserId(), productId, ct)))
            .WithName("RemoveFromWishlist")
            .WithSummary("Remove a product from the wishlist")
            .Produces<WishlistResponse>(StatusCodes.Status200OK);

        group.MapPut("/toggle/{productId:guid}", async Task<Ok<WishlistResponse>> (
                Guid productId, IWishlistService wishlist, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await wishlist.ToggleItemAsync(user.RequireUserId(), productId, ct)))
            .WithName("ToggleWishlist")
            .WithSummary("Toggle a product in the wishlist (add if absent, remove if present)")
            .Produces<WishlistResponse>(StatusCodes.Status200OK);
    }
}
