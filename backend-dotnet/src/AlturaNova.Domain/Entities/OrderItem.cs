namespace AlturaNova.Domain.Entities;

/// <summary>A snapshot line item captured at the time an order was placed.</summary>
public class OrderItem
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public Guid OrderId { get; set; }
    public Order Order { get; set; } = null!;

    public Guid ProductId { get; set; }
    public Guid VariantId { get; set; }

    /// <summary>Vendor that owns the purchased product, snapshotted for marketplace reporting.</summary>
    public Guid VendorId { get; set; }

    public string ProductName { get; set; } = string.Empty;
    public string? Sku { get; set; }
    public string? ThumbnailUrl { get; set; }

    public decimal UnitPrice { get; set; }
    public int Quantity { get; set; }
    public decimal LineTotal { get; set; }
    public string Currency { get; set; } = "EUR";
}
