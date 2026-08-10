using AlturaNova.Api.Filters;
using AlturaNova.Application.DTOs.Orders;
using AlturaNova.Application.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;

namespace AlturaNova.Api.Endpoints;

/// <summary>Admin-only endpoints for order oversight.</summary>
public static class AdminEndpoints
{
    public static void MapAdminEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/admin")
            .WithTags("Admin")
            .RequireAuthorization(policy => policy.RequireRole("Admin"))
            .AddEndpointFilter<DataAnnotationsValidationFilter>();

        group.MapGet("/orders", async Task<Ok<OrderListResponse>> (
                IOrderService orders, CancellationToken ct) =>
                TypedResults.Ok(await orders.GetAllAsync(ct)))
            .WithName("AdminGetOrders")
            .WithSummary("List every order")
            .Produces<OrderListResponse>(StatusCodes.Status200OK);

        group.MapPatch("/orders/{id:guid}/status", async Task<Ok<OrderResponse>> (
                Guid id, UpdateOrderStatusRequest request, IOrderService orders, CancellationToken ct) =>
                TypedResults.Ok(await orders.UpdateStatusAsync(id, request.Status, ct)))
            .WithName("AdminUpdateOrderStatus")
            .WithSummary("Transition an order to a new status (restocks on cancellation)")
            .Produces<OrderResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status404NotFound)
            .ProducesProblem(StatusCodes.Status409Conflict);
    }
}
