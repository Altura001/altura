namespace AlturaNova.Application.DTOs.Orders;

/// <summary>
/// Details returned after initializing a hosted-checkout payment. The client opens
/// <see cref="AuthorizationUrl"/> to let the customer pay, then verifies the order.
/// </summary>
public sealed record PaymentInitiationResponse(
    Guid OrderId,
    string Provider,
    string AuthorizationUrl,
    string AccessCode,
    string Reference,
    string PublicKey,
    long AmountSubunits,
    string Currency);
