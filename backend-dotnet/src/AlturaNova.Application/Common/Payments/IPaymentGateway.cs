namespace AlturaNova.Application.Common.Payments;

/// <summary>The outcome of a payment as reported by the provider.</summary>
public enum PaymentStatus
{
    Pending = 0,
    Success = 1,
    Failed = 2
}

/// <summary>Request to initialize a hosted-checkout transaction.</summary>
public sealed record PaymentInitializationRequest(
    long AmountSubunits,
    string Currency,
    string Email,
    string Reference,
    string? CallbackUrl);

/// <summary>Provider response describing where to send the payer.</summary>
public sealed record PaymentInitialization(
    string AuthorizationUrl,
    string AccessCode,
    string Reference);

/// <summary>Provider response describing the final state of a transaction.</summary>
public sealed record PaymentVerification(
    PaymentStatus Status,
    long AmountSubunits,
    string Currency,
    string Reference);

/// <summary>Abstraction over a hosted-checkout payment provider (implemented by Paystack).</summary>
public interface IPaymentGateway
{
    /// <summary>Short provider identifier, e.g. "paystack".</summary>
    string ProviderName { get; }

    /// <summary>The currency transactions are charged in (ISO 4217, e.g. "NGN").</summary>
    string DefaultCurrency { get; }

    /// <summary>The provider's publishable key, safe to expose to clients.</summary>
    string PublicKey { get; }

    Task<PaymentInitialization> InitializeAsync(PaymentInitializationRequest request, CancellationToken ct = default);

    Task<PaymentVerification> VerifyAsync(string reference, CancellationToken ct = default);

    /// <summary>Validates a provider webhook signature against the raw request body.</summary>
    bool VerifyWebhookSignature(string rawBody, string? signature);
}
