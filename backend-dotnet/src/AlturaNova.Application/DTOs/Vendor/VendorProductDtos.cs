using System.ComponentModel.DataAnnotations;

namespace AlturaNova.Application.DTOs.Vendor;

/// <summary>A product variant supplied when creating or updating a product.</summary>
public sealed record VariantInput
{
    /// <summary>Existing variant id when updating; omit/empty to create a new variant.</summary>
    public Guid? Id { get; init; }

    [Required, MaxLength(200)]
    public required string Title { get; init; }

    [MaxLength(100)]
    public string? Sku { get; init; }

    [Range(0.0, 9_999_999.99)]
    public required decimal Price { get; init; }

    [Range(0, int.MaxValue)]
    public int InventoryQuantity { get; init; }
}

/// <summary>Payload for creating a new product owned by the authenticated vendor.</summary>
public sealed record CreateProductRequest
{
    [Required, MaxLength(300)]
    public required string Name { get; init; }

    [MaxLength(300)]
    public string? Handle { get; init; }

    [MaxLength(4000)]
    public string? Description { get; init; }

    [MaxLength(1000)]
    public string? ThumbnailUrl { get; init; }

    [MaxLength(3), MinLength(3)]
    public string Currency { get; init; } = "EUR";

    public Guid? CategoryId { get; init; }

    public bool IsPublished { get; init; } = true;

    public IReadOnlyList<string> Images { get; init; } = [];

    [MinLength(1)]
    public required IReadOnlyList<VariantInput> Variants { get; init; }
}

/// <summary>Payload for updating an existing product. Variants are reconciled by id.</summary>
public sealed record UpdateProductRequest
{
    [Required, MaxLength(300)]
    public required string Name { get; init; }

    [MaxLength(4000)]
    public string? Description { get; init; }

    [MaxLength(1000)]
    public string? ThumbnailUrl { get; init; }

    [MaxLength(3), MinLength(3)]
    public string Currency { get; init; } = "EUR";

    public Guid? CategoryId { get; init; }

    public bool IsPublished { get; init; } = true;

    public IReadOnlyList<string> Images { get; init; } = [];

    [MinLength(1)]
    public required IReadOnlyList<VariantInput> Variants { get; init; }
}

/// <summary>Payload for publishing or unpublishing a product.</summary>
public sealed record SetPublishRequest
{
    public required bool IsPublished { get; init; }
}
