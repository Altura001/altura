using AlturaNova.Application.DTOs;
using AlturaNova.Domain.Interfaces;
using Microsoft.AspNetCore.Http.HttpResults;

namespace AlturaNova.Api.Endpoints;

/// <summary>Public pickup station endpoints.</summary>
public static class PickupStationEndpoints
{
    public static void MapPickupStationEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/pickup-stations")
            .WithTags("Pickup Stations");

        group.MapGet("/", async Task<Ok<List<PickupStationResponse>>> (
                IPickupStationRepository repository, CancellationToken ct) =>
            {
                var stations = await repository.GetActiveAsync(ct);
                var response = stations.Select(s => new PickupStationResponse(
                    s.Id, s.Name, s.Address, s.City, s.Phone, s.OperatingHours)).ToList();
                return TypedResults.Ok(response);
            })
            .WithName("GetPickupStations")
            .WithSummary("List active pickup stations")
            .Produces<List<PickupStationResponse>>(StatusCodes.Status200OK);
    }
}
