using System.ComponentModel.DataAnnotations;

namespace AlturaNova.Application.DTOs.Account;

/// <summary>Payload for updating the authenticated user's profile.</summary>
public sealed record UpdateProfileRequest
{
    [Required, MaxLength(100)]
    public required string FirstName { get; init; }

    [Required, MaxLength(100)]
    public required string LastName { get; init; }

    [Phone, MaxLength(40)]
    public string? Phone { get; init; }
}

/// <summary>Payload for changing the authenticated user's password.</summary>
public sealed record ChangePasswordRequest
{
    [Required, MaxLength(128)]
    public required string CurrentPassword { get; init; }

    [Required, MinLength(6), MaxLength(128)]
    public required string NewPassword { get; init; }
}
