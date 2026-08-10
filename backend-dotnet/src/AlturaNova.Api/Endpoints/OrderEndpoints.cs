using AlturaNova.Api.Filters;
using AlturaNova.Application.Common.Security;
using AlturaNova.Application.DTOs.Orders;
using AlturaNova.Application.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;

namespace AlturaNova.Api.Endpoints;

/// <summary>Order endpoints for the authenticated user.</summary>
public static class OrderEndpoints
{
    public static void MapOrderEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/orders")
            .WithTags("Orders")
            .RequireAuthorization()
            .AddEndpointFilter<DataAnnotationsValidationFilter>();

        group.MapPost("/checkout", async Task<Created<OrderResponse>> (
                CheckoutRequest request, IOrderService orders, ICurrentUser user, CancellationToken ct) =>
            {
                var order = await orders.CheckoutAsync(user.RequireUserId(), request, ct);
                return TypedResults.Created($"/api/orders/{order.Id}", order);
            })
            .WithName("Checkout")
            .WithSummary("Convert the current cart into an order")
            .WithDescription("Validates stock, snapshots line items, decrements inventory, and clears the cart.")
            .Produces<OrderResponse>(StatusCodes.Status201Created)
            .ProducesProblem(StatusCodes.Status409Conflict);

        group.MapGet("/", async Task<Ok<OrderListResponse>> (
                IOrderService orders, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await orders.GetOrdersAsync(user.RequireUserId(), ct)))
            .WithName("GetOrders")
            .WithSummary("List the current user's orders")
            .Produces<OrderListResponse>(StatusCodes.Status200OK);

        group.MapGet("/{id:guid}", async Task<Ok<OrderResponse>> (
                Guid id, IOrderService orders, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await orders.GetOrderAsync(user.RequireUserId(), id, ct)))
            .WithName("GetOrder")
            .WithSummary("Get one of the current user's orders by id")
            .Produces<OrderResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound);

        group.MapPost("/{id:guid}/pay", async Task<Ok<PaymentInitiationResponse>> (
                Guid id, InitiatePaymentRequest? request, IOrderService orders, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await orders.InitiatePaymentAsync(user.RequireUserId(), id, request?.CallbackUrl, ct)))
            .WithName("InitiatePayment")
            .WithSummary("Start a Paystack hosted-checkout payment for a pending order")
            .WithDescription("Returns an authorization URL the client opens so the customer can pay.")
            .Produces<PaymentInitiationResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound)
            .ProducesProblem(StatusCodes.Status409Conflict);

        group.MapPost("/{id:guid}/verify", async Task<Ok<OrderResponse>> (
                Guid id, IOrderService orders, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await orders.VerifyPaymentAsync(user.RequireUserId(), id, ct)))
            .WithName("VerifyPayment")
            .WithSummary("Verify a payment with the provider and mark the order paid on success")
            .Produces<OrderResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound)
            .ProducesProblem(StatusCodes.Status409Conflict);

        group.MapPost("/{id:guid}/cancel", async Task<Ok<OrderResponse>> (
                Guid id, IOrderService orders, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await orders.CancelAsync(user.RequireUserId(), id, ct)))
            .WithName("CancelOrder")
            .WithSummary("Cancel a pending/paid order and restock inventory")
            .Produces<OrderResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound)
            .ProducesProblem(StatusCodes.Status409Conflict);
    }
}
