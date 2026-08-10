using System.ComponentModel.DataAnnotations;

namespace AlturaNova.Application.DTOs.Auth;

/// <summary>Payload for registering a new customer account.</summary>
public sealed record RegisterCustomerRequest
{
    [Required, EmailAddress, MaxLength(256)]
    public required string Email { get; init; }

    [Required, MinLength(6), MaxLength(128)]
    public required string Password { get; init; }

    [Required, MaxLength(100)]
    public required string FirstName { get; init; }

    [Required, MaxLength(100)]
    public required string LastName { get; init; }

    [Phone, MaxLength(40)]
    public string? Phone { get; init; }
}

/// <summary>Payload for registering a new vendor account (creates a store and owner user).</summary>
public sealed record RegisterVendorRequest
{
    [Required, EmailAddress, MaxLength(256)]
    public required string Email { get; init; }

    [Required, MinLength(6), MaxLength(128)]
    public required string Password { get; init; }

    [Required, MaxLength(100)]
    public required string FirstName { get; init; }

    [Required, MaxLength(100)]
    public required string LastName { get; init; }

    [Required, MaxLength(200)]
    public required string StoreName { get; init; }

    [Phone, MaxLength(40)]
    public string? Phone { get; init; }
}

/// <summary>Payload for authenticating with email and password.</summary>
public sealed record LoginRequest
{
    [Required, EmailAddress, MaxLength(256)]
    public required string Email { get; init; }

    [Required, MaxLength(128)]
    public required string Password { get; init; }
}

/// <summary>Payload for exchanging a refresh token for a new token pair.</summary>
public sealed record RefreshTokenRequest
{
    [Required]
    public required string RefreshToken { get; init; }
}
