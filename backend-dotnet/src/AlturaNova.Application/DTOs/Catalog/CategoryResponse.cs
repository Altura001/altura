namespace AlturaNova.Application.DTOs.Catalog;

/// <summary>Represents a product category.</summary>
public sealed record CategoryResponse(
    Guid Id,
    string Name,
    string Handle);
