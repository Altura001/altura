using AlturaNova.Api.Filters;
using AlturaNova.Application.Common.Security;
using AlturaNova.Application.DTOs.Cart;
using AlturaNova.Application.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;

namespace AlturaNova.Api.Endpoints;

/// <summary>Cart endpoints for the authenticated user.</summary>
public static class CartEndpoints
{
    public static void MapCartEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/cart")
            .WithTags("Cart")
            .RequireAuthorization()
            .AddEndpointFilter<DataAnnotationsValidationFilter>();

        group.MapGet("/", async Task<Ok<CartResponse>> (
                ICartService cart, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await cart.GetCartAsync(user.RequireUserId(), ct)))
            .WithName("GetCart")
            .WithSummary("Get the current user's cart")
            .Produces<CartResponse>(StatusCodes.Status200OK);

        group.MapPost("/items", async Task<Ok<CartResponse>> (
                AddCartItemRequest request, ICartService cart, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await cart.AddItemAsync(user.RequireUserId(), request, ct)))
            .WithName("AddCartItem")
            .WithSummary("Add a product variant to the cart")
            .Produces<CartResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound)
            .ProducesProblem(StatusCodes.Status409Conflict);

        group.MapPatch("/items/{itemId:guid}", async Task<Ok<CartResponse>> (
                Guid itemId, UpdateCartItemRequest request,
                ICartService cart, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await cart.UpdateItemAsync(user.RequireUserId(), itemId, request, ct)))
            .WithName("UpdateCartItem")
            .WithSummary("Change the quantity of a cart item (0 removes it)")
            .Produces<CartResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound)
            .ProducesProblem(StatusCodes.Status409Conflict);

        group.MapDelete("/items/{itemId:guid}", async Task<Ok<CartResponse>> (
                Guid itemId, ICartService cart, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await cart.RemoveItemAsync(user.RequireUserId(), itemId, ct)))
            .WithName("RemoveCartItem")
            .WithSummary("Remove an item from the cart")
            .Produces<CartResponse>(StatusCodes.Status200OK);

        group.MapDelete("/", async Task<Ok<CartResponse>> (
                ICartService cart, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await cart.ClearAsync(user.RequireUserId(), ct)))
            .WithName("ClearCart")
            .WithSummary("Remove all items from the cart")
            .Produces<CartResponse>(StatusCodes.Status200OK);
    }
}
