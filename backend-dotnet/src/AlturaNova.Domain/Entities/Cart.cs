namespace AlturaNova.Domain.Entities;

/// <summary>A user's active shopping cart. One active cart per user.</summary>
public class Cart
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public Guid UserId { get; set; }
    public User User { get; set; } = null!;

    public string Currency { get; set; } = "EUR";

    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;
    public DateTimeOffset UpdatedAt { get; set; } = DateTimeOffset.UtcNow;

    public ICollection<CartItem> Items { get; set; } = new List<CartItem>();

    public decimal Subtotal => Items.Sum(i => i.UnitPrice * i.Quantity);
    public int ItemCount => Items.Sum(i => i.Quantity);
}
