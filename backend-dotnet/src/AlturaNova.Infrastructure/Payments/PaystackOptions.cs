namespace AlturaNova.Infrastructure.Payments;

/// <summary>Configuration for the Paystack payment provider, bound from the "Paystack" section.</summary>
public sealed class PaystackOptions
{
    public const string SectionName = "Paystack";

    /// <summary>Secret key (sk_...). Server-side only; never exposed to clients.</summary>
    public string SecretKey { get; set; } = "sk_test_19cf17c889e68922e1dbf3fef23a8dd8da32a8e3";

    /// <summary>Publishable key (pk_...), safe to return to clients.</summary>
    public string PublicKey { get; set; } = "pk_test_4382b03d8fe7ece405a5d4d008150d8798e296df";

    public string BaseUrl { get; set; } = "https://api.paystack.co";

    /// <summary>ISO 4217 currency transactions are charged in.</summary>
    public string Currency { get; set; } = "NGN";
}
