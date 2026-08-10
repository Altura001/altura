namespace AlturaNova.Domain.Entities;

/// <summary>A line item in a cart, referencing a specific product variant.</summary>
public class CartItem
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public Guid CartId { get; set; }
    public Cart Cart { get; set; } = null!;

    public Guid ProductId { get; set; }
    public Product? Product { get; set; }

    public Guid VariantId { get; set; }
    public ProductVariant? Variant { get; set; }

    public string Title { get; set; } = string.Empty;
    public int Quantity { get; set; }
    public decimal UnitPrice { get; set; }
    public string Currency { get; set; } = "EUR";
    public string? ThumbnailUrl { get; set; }

    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;

    public decimal LineTotal => UnitPrice * Quantity;
}
