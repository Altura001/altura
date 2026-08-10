namespace AlturaNova.Infrastructure.Payments;

/// <summary>Configuration for the Paystack payment provider, bound from the "Paystack" section.</summary>
public sealed class PaystackOptions
{
    public const string SectionName = "Paystack";

    /// <summary>Secret key (sk_...). Server-side only; never exposed to clients.</summary>
    public string SecretKey { get; set; } = string.Empty;

    /// <summary>Publishable key (pk_...), safe to return to clients.</summary>
    public string PublicKey { get; set; } = string.Empty;

    public string BaseUrl { get; set; } = "https://api.paystack.co";

    /// <summary>ISO 4217 currency transactions are charged in.</summary>
    public string Currency { get; set; } = "NGN";
}
