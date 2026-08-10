using System.Net.Http.Json;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using AlturaNova.Application.Common.Payments;
using AlturaNova.Domain.Exceptions;
using Microsoft.Extensions.Options;

namespace AlturaNova.Infrastructure.Payments;

/// <summary>Paystack implementation of <see cref="IPaymentGateway"/> (hosted checkout).</summary>
public sealed class PaystackPaymentGateway(HttpClient httpClient, IOptions<PaystackOptions> options)
    : IPaymentGateway
{
    private readonly PaystackOptions _options = options.Value;

    private static readonly JsonSerializerOptions Json = new(JsonSerializerDefaults.Web);

    public string ProviderName => "paystack";
    public string DefaultCurrency => _options.Currency;
    public string PublicKey => _options.PublicKey;

    public async Task<PaymentInitialization> InitializeAsync(PaymentInitializationRequest request, CancellationToken ct = default)
    {
        EnsureConfigured();

        var payload = new InitializeRequest
        {
            Email = request.Email,
            Amount = request.AmountSubunits,
            Currency = request.Currency,
            Reference = request.Reference,
            CallbackUrl = request.CallbackUrl
        };

        using var response = await httpClient.PostAsJsonAsync("transaction/initialize", payload, Json, ct);
        var body = await ReadAsync<PaystackResponse<InitializeData>>(response, ct);

        if (body is not { Status: true, Data: not null })
            throw new ConflictException(body?.Message ?? "Unable to initialize payment with Paystack.");

        return new PaymentInitialization(
            body.Data.AuthorizationUrl,
            body.Data.AccessCode,
            body.Data.Reference);
    }

    public async Task<PaymentVerification> VerifyAsync(string reference, CancellationToken ct = default)
    {
        EnsureConfigured();

        using var response = await httpClient.GetAsync($"transaction/verify/{Uri.EscapeDataString(reference)}", ct);
        var body = await ReadAsync<PaystackResponse<VerifyData>>(response, ct);

        if (body is not { Status: true, Data: not null })
            throw new ConflictException(body?.Message ?? "Unable to verify payment with Paystack.");

        var status = body.Data.Status?.ToLowerInvariant() switch
        {
            "success" => PaymentStatus.Success,
            "failed" or "abandoned" or "reversed" => PaymentStatus.Failed,
            _ => PaymentStatus.Pending
        };

        return new PaymentVerification(status, body.Data.Amount, body.Data.Currency ?? _options.Currency, body.Data.Reference ?? reference);
    }

    public bool VerifyWebhookSignature(string rawBody, string? signature)
    {
        if (string.IsNullOrEmpty(signature) || string.IsNullOrEmpty(_options.SecretKey))
            return false;

        var key = Encoding.UTF8.GetBytes(_options.SecretKey);
        var hash = HMACSHA512.HashData(key, Encoding.UTF8.GetBytes(rawBody));
        var computed = Convert.ToHexStringLower(hash);

        // Constant-time comparison.
        return CryptographicOperations.FixedTimeEquals(
            Encoding.ASCII.GetBytes(computed),
            Encoding.ASCII.GetBytes(signature.Trim().ToLowerInvariant()));
    }

    private void EnsureConfigured()
    {
        if (string.IsNullOrEmpty(_options.SecretKey))
            throw new ConflictException("Paystack is not configured. Set Paystack:SecretKey.");
    }

    private static async Task<T?> ReadAsync<T>(HttpResponseMessage response, CancellationToken ct)
    {
        // Paystack returns a JSON error body even on non-2xx; parse it for the message.
        var content = await response.Content.ReadFromJsonAsync<T>(Json, ct);
        return content;
    }

    // ----- Paystack wire models ---------------------------------------------

    private sealed class InitializeRequest
    {
        public string Email { get; set; } = string.Empty;
        public long Amount { get; set; }
        public string Currency { get; set; } = "NGN";
        public string Reference { get; set; } = string.Empty;

        [JsonPropertyName("callback_url")]
        public string? CallbackUrl { get; set; }
    }

    private sealed class PaystackResponse<T>
    {
        public bool Status { get; set; }
        public string? Message { get; set; }
        public T? Data { get; set; }
    }

    private sealed class InitializeData
    {
        [JsonPropertyName("authorization_url")]
        public string AuthorizationUrl { get; set; } = string.Empty;

        [JsonPropertyName("access_code")]
        public string AccessCode { get; set; } = string.Empty;

        public string Reference { get; set; } = string.Empty;
    }

    private sealed class VerifyData
    {
        public string? Status { get; set; }
        public long Amount { get; set; }
        public string? Currency { get; set; }
        public string? Reference { get; set; }
    }
}
