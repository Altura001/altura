namespace AlturaNova.Application.DTOs.Auth;

/// <summary>Represents an authenticated user returned by the API.</summary>
public sealed record UserResponse(
    Guid Id,
    string Email,
    string FirstName,
    string LastName,
    string? Phone,
    string Role,
    Guid? VendorId);

/// <summary>The result of a successful authentication, including tokens and user profile.</summary>
public sealed record AuthResponse(
    UserResponse User,
    string AccessToken,
    string RefreshToken,
    DateTimeOffset AccessTokenExpiresAt);
