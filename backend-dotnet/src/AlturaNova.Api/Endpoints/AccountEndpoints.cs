using AlturaNova.Api.Filters;
using AlturaNova.Application.Common.Security;
using AlturaNova.Application.DTOs.Account;
using AlturaNova.Application.DTOs.Auth;
using AlturaNova.Application.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;

namespace AlturaNova.Api.Endpoints;

/// <summary>Self-service account endpoints for the authenticated user.</summary>
public static class AccountEndpoints
{
    public static void MapAccountEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/account")
            .WithTags("Account")
            .RequireAuthorization()
            .AddEndpointFilter<DataAnnotationsValidationFilter>();

        group.MapPut("/profile", async Task<Ok<UserResponse>> (
                UpdateProfileRequest request, IAccountService account, ICurrentUser user, CancellationToken ct) =>
                TypedResults.Ok(await account.UpdateProfileAsync(user.RequireUserId(), request, ct)))
            .WithName("UpdateProfile")
            .WithSummary("Update the authenticated user's profile")
            .Produces<UserResponse>(StatusCodes.Status200OK);

        group.MapPost("/change-password", async Task<NoContent> (
                ChangePasswordRequest request, IAccountService account, ICurrentUser user, CancellationToken ct) =>
            {
                await account.ChangePasswordAsync(user.RequireUserId(), request, ct);
                return TypedResults.NoContent();
            })
            .WithName("ChangePassword")
            .WithSummary("Change the authenticated user's password")
            .Produces(StatusCodes.Status204NoContent)
            .ProducesProblem(StatusCodes.Status401Unauthorized);
    }
}
