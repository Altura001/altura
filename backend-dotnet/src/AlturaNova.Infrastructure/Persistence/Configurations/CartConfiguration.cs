using AlturaNova.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace AlturaNova.Infrastructure.Persistence.Configurations;

/// <summary>EF configuration for carts.</summary>
public sealed class CartConfiguration : IEntityTypeConfiguration<Cart>
{
    public void Configure(EntityTypeBuilder<Cart> b)
    {
        b.ToTable("carts");
        b.HasKey(c => c.Id);
        b.Property(c => c.Id).ValueGeneratedNever();
        b.Property(c => c.Currency).HasMaxLength(3).IsRequired();

        // One active cart per user.
        b.HasIndex(c => c.UserId).IsUnique();

        b.HasOne(c => c.User)
            .WithMany()
            .HasForeignKey(c => c.UserId)
            .OnDelete(DeleteBehavior.Cascade);

        b.HasMany(c => c.Items)
            .WithOne(i => i.Cart)
            .HasForeignKey(i => i.CartId)
            .OnDelete(DeleteBehavior.Cascade);

        b.Ignore(c => c.Subtotal);
        b.Ignore(c => c.ItemCount);
    }
}

/// <summary>EF configuration for cart items.</summary>
public sealed class CartItemConfiguration : IEntityTypeConfiguration<CartItem>
{
    public void Configure(EntityTypeBuilder<CartItem> b)
    {
        b.ToTable("cart_items");
        b.HasKey(i => i.Id);
        b.Property(i => i.Id).ValueGeneratedNever();
        b.Property(i => i.Title).HasMaxLength(400).IsRequired();
        b.Property(i => i.UnitPrice).HasPrecision(18, 2);
        b.Property(i => i.Currency).HasMaxLength(3).IsRequired();
        b.Property(i => i.ThumbnailUrl).HasMaxLength(1000);
        b.HasIndex(i => new { i.CartId, i.VariantId }).IsUnique();

        b.HasOne(i => i.Product)
            .WithMany()
            .HasForeignKey(i => i.ProductId)
            .OnDelete(DeleteBehavior.Restrict);

        b.HasOne(i => i.Variant)
            .WithMany()
            .HasForeignKey(i => i.VariantId)
            .OnDelete(DeleteBehavior.Restrict);

        b.Ignore(i => i.LineTotal);
    }
}
