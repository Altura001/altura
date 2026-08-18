using AlturaNova.Domain.Enums;

namespace AlturaNova.Domain.Entities;

/// <summary>A placed order with a snapshot of purchased items and totals.</summary>
public class Order
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public Guid UserId { get; set; }
    public User User { get; set; } = null!;

    public OrderStatus Status { get; set; } = OrderStatus.Pending;

    public DeliveryMethod DeliveryMethod { get; set; } = DeliveryMethod.Shipping;
    public Guid? PickupStationId { get; set; }
    public PickupStation? PickupStation { get; set; }

    public decimal Subtotal { get; set; }
    public decimal ShippingFee { get; set; }
    public decimal Total { get; set; }
    public string Currency { get; set; } = "EUR";

    /// <summary>Provider transaction reference (Paystack), set when payment is initialized.</summary>
    public string? PaymentReference { get; set; }

    /// <summary>When the order was successfully paid.</summary>
    public DateTimeOffset? PaidAt { get; set; }

    public OrderAddress? ShippingAddress { get; set; }

    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;
    public DateTimeOffset UpdatedAt { get; set; } = DateTimeOffset.UtcNow;

    public ICollection<OrderItem> Items { get; set; } = new List<OrderItem>();
}
