using AlturaNova.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace AlturaNova.Infrastructure.Persistence.Configurations;

/// <summary>EF configuration for products, variants, and images.</summary>
public sealed class ProductConfiguration : IEntityTypeConfiguration<Product>
{
    public void Configure(EntityTypeBuilder<Product> b)
    {
        b.ToTable("products");
        b.HasKey(p => p.Id);
        b.Property(p => p.Id).ValueGeneratedNever();
        b.Property(p => p.Name).HasMaxLength(300).IsRequired();
        b.Property(p => p.Handle).HasMaxLength(300).IsRequired();
        b.HasIndex(p => p.Handle).IsUnique();
        b.Property(p => p.Description).HasMaxLength(4000);
        b.Property(p => p.ThumbnailUrl).HasMaxLength(1000);
        b.Property(p => p.Currency).HasMaxLength(3).IsRequired();
        b.HasIndex(p => new { p.VendorId, p.IsPublished });

        b.HasOne(p => p.Category)
            .WithMany(c => c.Products)
            .HasForeignKey(p => p.CategoryId)
            .OnDelete(DeleteBehavior.SetNull);

        b.HasMany(p => p.Variants)
            .WithOne(v => v.Product)
            .HasForeignKey(v => v.ProductId)
            .OnDelete(DeleteBehavior.Cascade);

        b.HasMany(p => p.Images)
            .WithOne(i => i.Product)
            .HasForeignKey(i => i.ProductId)
            .OnDelete(DeleteBehavior.Cascade);
    }
}

/// <summary>EF configuration for product variants.</summary>
public sealed class ProductVariantConfiguration : IEntityTypeConfiguration<ProductVariant>
{
    public void Configure(EntityTypeBuilder<ProductVariant> b)
    {
        b.ToTable("product_variants");
        b.HasKey(v => v.Id);
        b.Property(v => v.Id).ValueGeneratedNever();
        b.Property(v => v.Title).HasMaxLength(200).IsRequired();
        b.Property(v => v.Sku).HasMaxLength(100);
        b.Property(v => v.Price).HasPrecision(18, 2);
        b.Property(v => v.Currency).HasMaxLength(3).IsRequired();
        b.HasIndex(v => v.Sku);
        b.Ignore(v => v.IsInStock);

        // Optimistic concurrency on stock changes to prevent overselling under races.
        // Maps PostgreSQL's system "xmin" column as a shadow row-version concurrency token.
        b.Property<uint>("xmin")
            .HasColumnName("xmin")
            .HasColumnType("xid")
            .ValueGeneratedOnAddOrUpdate()
            .IsConcurrencyToken();
    }
}

/// <summary>EF configuration for product images.</summary>
public sealed class ProductImageConfiguration : IEntityTypeConfiguration<ProductImage>
{
    public void Configure(EntityTypeBuilder<ProductImage> b)
    {
        b.ToTable("product_images");
        b.HasKey(i => i.Id);
        b.Property(i => i.Id).ValueGeneratedNever();
        b.Property(i => i.Url).HasMaxLength(1000).IsRequired();
    }
}
