namespace AlturaNova.Application.DTOs;

/// <summary>A pickup station available for order collection.</summary>
public sealed record PickupStationResponse(
    Guid Id,
    string Name,
    string Address,
    string City,
    string? Phone,
    string? OperatingHours);
