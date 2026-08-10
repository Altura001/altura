using AlturaNova.Api.Filters;
using AlturaNova.Application.Common.Security;
using AlturaNova.Application.DTOs.Auth;
using AlturaNova.Application.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;

namespace AlturaNova.Api.Endpoints;

/// <summary>Authentication and account endpoints.</summary>
public static class AuthEndpoints
{
    public static void MapAuthEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/auth")
            .WithTags("Auth")
            .AddEndpointFilter<DataAnnotationsValidationFilter>();

        group.MapPost("/register/customer", async Task<Ok<AuthResponse>> (
                RegisterCustomerRequest request, IAuthService auth, CancellationToken ct) =>
                TypedResults.Ok(await auth.RegisterCustomerAsync(request, ct)))
            .WithName("RegisterCustomer")
            .WithSummary("Register a new customer account")
            .WithDescription("Creates a customer account and returns access and refresh tokens.")
            .Produces<AuthResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status409Conflict)
            .AllowAnonymous();

        group.MapPost("/register/vendor", async Task<Ok<AuthResponse>> (
                RegisterVendorRequest request, IAuthService auth, CancellationToken ct) =>
                TypedResults.Ok(await auth.RegisterVendorAsync(request, ct)))
            .WithName("RegisterVendor")
            .WithSummary("Register a new vendor account")
            .WithDescription("Creates a vendor store and its owner account, returning tokens.")
            .Produces<AuthResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status409Conflict)
            .AllowAnonymous();

        group.MapPost("/login", async Task<Ok<AuthResponse>> (
                LoginRequest request, IAuthService auth, CancellationToken ct) =>
                TypedResults.Ok(await auth.LoginAsync(request, ct)))
            .WithName("Login")
            .WithSummary("Authenticate with email and password")
            .Produces<AuthResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status401Unauthorized)
            .AllowAnonymous();

        group.MapPost("/refresh", async Task<Ok<AuthResponse>> (
                RefreshTokenRequest request, IAuthService auth, CancellationToken ct) =>
                TypedResults.Ok(await auth.RefreshAsync(request, ct)))
            .WithName("RefreshToken")
            .WithSummary("Exchange a refresh token for a new token pair")
            .Produces<AuthResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status401Unauthorized)
            .AllowAnonymous();

        group.MapPost("/logout", async Task<NoContent> (
                RefreshTokenRequest request, IAuthService auth, CancellationToken ct) =>
            {
                await auth.LogoutAsync(request.RefreshToken, ct);
                return TypedResults.NoContent();
            })
            .WithName("Logout")
            .WithSummary("Revoke a refresh token")
            .Produces(StatusCodes.Status204NoContent)
            .RequireAuthorization();

        group.MapGet("/me", async Task<Ok<UserResponse>> (
                IAuthService auth, ICurrentUser currentUser, CancellationToken ct) =>
                TypedResults.Ok(await auth.GetCurrentUserAsync(currentUser.RequireUserId(), ct)))
            .WithName("GetCurrentUser")
            .WithSummary("Get the authenticated user's profile")
            .Produces<UserResponse>(StatusCodes.Status200OK)
            .ProducesProblem(StatusCodes.Status401Unauthorized)
            .RequireAuthorization();
    }
}
