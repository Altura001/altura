using AlturaNova.Domain.Entities;

namespace AlturaNova.Application.Common.Security;

/// <summary>Strongly-typed JWT configuration bound from the "Jwt" configuration section.</summary>
public sealed class JwtOptions
{
    public const string SectionName = "Jwt";

    public string Issuer { get; set; } = "AlturaNova";
    public string Audience { get; set; } = "AlturaNovaClients";
    public string Secret { get; set; } = string.Empty;
    public int AccessTokenMinutes { get; set; } = 60;
    public int RefreshTokenDays { get; set; } = 30;
}

/// <summary>A freshly generated access token and its absolute expiry.</summary>
public readonly record struct AccessToken(string Value, DateTimeOffset ExpiresAt);

/// <summary>Creates signed JWT access tokens and opaque refresh tokens.</summary>
public interface IJwtTokenService
{
    AccessToken CreateAccessToken(User user);
    string CreateRefreshToken();
    DateTimeOffset GetRefreshTokenExpiry();
}
