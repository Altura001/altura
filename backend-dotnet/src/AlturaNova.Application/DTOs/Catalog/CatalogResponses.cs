namespace AlturaNova.Application.DTOs.Catalog;

/// <summary>Represents a vendor/store returned by the API.</summary>
public sealed record VendorResponse(
    Guid Id,
    string Name,
    string Handle,
    string Description,
    string? LogoUrl,
    string? BannerUrl);

/// <summary>Represents a single purchasable variant of a product.</summary>
public sealed record ProductVariantResponse(
    Guid Id,
    string Title,
    string? Sku,
    decimal Price,
    string Currency,
    int InventoryQuantity,
    bool IsInStock);

/// <summary>Represents a product returned by the API, including its variants and images.</summary>
public sealed record ProductResponse(
    Guid Id,
    Guid VendorId,
    string VendorName,
    string Name,
    string Handle,
    string Description,
    decimal Price,
    string Currency,
    string? ThumbnailUrl,
    IReadOnlyList<string> Images,
    string? Category,
    bool InStock,
    IReadOnlyList<ProductVariantResponse> Variants,
    DateTimeOffset CreatedAt);

/// <summary>A paged list of products.</summary>
public sealed record ProductListResponse(
    IReadOnlyList<ProductResponse> Items,
    int Total,
    int Page,
    int PageSize);
