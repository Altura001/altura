using System.ComponentModel.DataAnnotations;

namespace AlturaNova.Application.DTOs.Vendor;

/// <summary>The authenticated vendor's store profile.</summary>
public sealed record VendorStoreResponse(
    Guid Id,
    string Name,
    string Handle,
    string Description,
    string? LogoUrl,
    string? BannerUrl,
    bool IsActive,
    DateTimeOffset CreatedAt);

/// <summary>Payload for updating the authenticated vendor's store profile.</summary>
public sealed record UpdateVendorStoreRequest
{
    [Required, MaxLength(200)]
    public required string Name { get; init; }

    [MaxLength(2000)]
    public string? Description { get; init; }

    [MaxLength(1000)]
    public string? LogoUrl { get; init; }

    [MaxLength(1000)]
    public string? BannerUrl { get; init; }
}
