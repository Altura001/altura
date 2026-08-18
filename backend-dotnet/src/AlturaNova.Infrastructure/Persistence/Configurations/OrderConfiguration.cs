using AlturaNova.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace AlturaNova.Infrastructure.Persistence.Configurations;

/// <summary>EF configuration for orders.</summary>
public sealed class OrderConfiguration : IEntityTypeConfiguration<Order>
{
    public void Configure(EntityTypeBuilder<Order> b)
    {
        b.ToTable("orders");
        b.HasKey(o => o.Id);
        b.Property(o => o.Id).ValueGeneratedNever();
        b.Property(o => o.Status).HasConversion<string>().HasMaxLength(20);
        b.Property(o => o.DeliveryMethod).HasConversion<string>().HasMaxLength(20);
        b.Property(o => o.ShippingFee).HasPrecision(18, 2);
        b.Property(o => o.Subtotal).HasPrecision(18, 2);
        b.Property(o => o.Total).HasPrecision(18, 2);
        b.Property(o => o.Currency).HasMaxLength(3).IsRequired();
        b.Property(o => o.PaymentReference).HasMaxLength(100);
        b.HasIndex(o => o.PaymentReference);
        b.HasIndex(o => new { o.UserId, o.CreatedAt });

        b.HasOne(o => o.User)
            .WithMany()
            .HasForeignKey(o => o.UserId)
            .OnDelete(DeleteBehavior.Cascade);

        b.HasOne(o => o.PickupStation)
            .WithMany()
            .HasForeignKey(o => o.PickupStationId)
            .OnDelete(DeleteBehavior.SetNull);

        b.HasMany(o => o.Items)
            .WithOne(i => i.Order)
            .HasForeignKey(i => i.OrderId)
            .OnDelete(DeleteBehavior.Cascade);

        b.HasOne(o => o.ShippingAddress)
            .WithOne(a => a.Order)
            .HasForeignKey<OrderAddress>(a => a.OrderId)
            .OnDelete(DeleteBehavior.Cascade);
    }
}

/// <summary>EF configuration for order line items.</summary>
public sealed class OrderItemConfiguration : IEntityTypeConfiguration<OrderItem>
{
    public void Configure(EntityTypeBuilder<OrderItem> b)
    {
        b.ToTable("order_items");
        b.HasKey(i => i.Id);
        b.Property(i => i.Id).ValueGeneratedNever();
        b.Property(i => i.ProductName).HasMaxLength(400).IsRequired();
        b.HasIndex(i => i.VendorId);
        b.Property(i => i.Sku).HasMaxLength(100);
        b.Property(i => i.ThumbnailUrl).HasMaxLength(1000);
        b.Property(i => i.UnitPrice).HasPrecision(18, 2);
        b.Property(i => i.LineTotal).HasPrecision(18, 2);
        b.Property(i => i.Currency).HasMaxLength(3).IsRequired();
    }
}

/// <summary>EF configuration for order shipping addresses.</summary>
public sealed class OrderAddressConfiguration : IEntityTypeConfiguration<OrderAddress>
{
    public void Configure(EntityTypeBuilder<OrderAddress> b)
    {
        b.ToTable("order_addresses");
        b.HasKey(a => a.Id);
        b.Property(a => a.Id).ValueGeneratedNever();
        b.Property(a => a.FirstName).HasMaxLength(100).IsRequired();
        b.Property(a => a.LastName).HasMaxLength(100).IsRequired();
        b.Property(a => a.Line1).HasMaxLength(200).IsRequired();
        b.Property(a => a.Line2).HasMaxLength(200);
        b.Property(a => a.City).HasMaxLength(100).IsRequired();
        b.Property(a => a.State).HasMaxLength(100);
        b.Property(a => a.PostalCode).HasMaxLength(20).IsRequired();
        b.Property(a => a.Country).HasMaxLength(2).IsRequired();
        b.Property(a => a.Phone).HasMaxLength(40);
    }
}
