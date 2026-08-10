namespace AlturaNova.Domain.Entities;

/// <summary>A purchasable variant (size/color/etc.) of a product with its own price and stock.</summary>
public class ProductVariant
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public Guid ProductId { get; set; }
    public Product Product { get; set; } = null!;

    public string Title { get; set; } = string.Empty;
    public string? Sku { get; set; }
    public decimal Price { get; set; }
    public string Currency { get; set; } = "EUR";
    public int InventoryQuantity { get; set; }

    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;

    public bool IsInStock => InventoryQuantity > 0;
}
