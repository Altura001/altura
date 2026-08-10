namespace AlturaNova.Domain.Enums;

/// <summary>Lifecycle status of a customer order.</summary>
public enum OrderStatus
{
    Pending = 0,
    Paid = 1,
    Shipped = 2,
    Delivered = 3,
    Cancelled = 4
}
