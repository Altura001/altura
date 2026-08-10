using AlturaNova.Application.Common.Payments;
using AlturaNova.Application.Interfaces;
using System.Text.Json;

namespace AlturaNova.Api.Endpoints;

/// <summary>Provider webhook endpoints (public, signature-verified).</summary>
public static class WebhookEndpoints
{
    public static void MapWebhookEndpoints(this IEndpointRouteBuilder app)
    {
        // Paystack posts events here. The raw body is required for signature verification.
        app.MapPost("/webhooks/paystack", async (
                HttpRequest request,
                IPaymentGateway gateway,
                IOrderService orders,
                ILoggerFactory loggerFactory,
                CancellationToken ct) =>
            {
                var logger = loggerFactory.CreateLogger("PaystackWebhook");

                using var reader = new StreamReader(request.Body);
                var rawBody = await reader.ReadToEndAsync(ct);

                var signature = request.Headers["x-paystack-signature"].ToString();
                if (!gateway.VerifyWebhookSignature(rawBody, signature))
                {
                    logger.LogWarning("Rejected Paystack webhook with an invalid signature.");
                    return Results.Unauthorized();
                }

                // Confirm authoritatively by re-verifying the reference with Paystack.
                var reference = ExtractReference(rawBody);
                if (!string.IsNullOrWhiteSpace(reference))
                    await orders.ConfirmPaymentByReferenceAsync(reference, ct);

                // Always 200 so the provider stops retrying a handled event.
                return Results.Ok();
            })
            .WithTags("Webhooks")
            .WithName("PaystackWebhook")
            .WithSummary("Receive Paystack payment events")
            .AllowAnonymous();
    }

    private static string? ExtractReference(string rawBody)
    {
        try
        {
            using var doc = JsonDocument.Parse(rawBody);
            return doc.RootElement.TryGetProperty("data", out var data)
                   && data.TryGetProperty("reference", out var reference)
                ? reference.GetString()
                : null;
        }
        catch (JsonException)
        {
            return null;
        }
    }
}
