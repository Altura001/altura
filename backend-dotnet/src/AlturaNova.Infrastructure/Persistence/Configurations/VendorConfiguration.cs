using AlturaNova.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace AlturaNova.Infrastructure.Persistence.Configurations;

/// <summary>EF configuration for vendors.</summary>
public sealed class VendorConfiguration : IEntityTypeConfiguration<Vendor>
{
    public void Configure(EntityTypeBuilder<Vendor> b)
    {
        b.ToTable("vendors");
        b.HasKey(v => v.Id);
        b.Property(v => v.Id).ValueGeneratedNever();
        b.Property(v => v.Name).HasMaxLength(200).IsRequired();
        b.Property(v => v.Handle).HasMaxLength(200).IsRequired();
        b.HasIndex(v => v.Handle).IsUnique();
        b.Property(v => v.Description).HasMaxLength(2000);
        b.Property(v => v.LogoUrl).HasMaxLength(1000);
        b.Property(v => v.BannerUrl).HasMaxLength(1000);

        b.HasMany(v => v.Products)
            .WithOne(p => p.Vendor)
            .HasForeignKey(p => p.VendorId)
            .OnDelete(DeleteBehavior.Cascade);
    }
}

/// <summary>EF configuration for categories.</summary>
public sealed class CategoryConfiguration : IEntityTypeConfiguration<Category>
{
    public void Configure(EntityTypeBuilder<Category> b)
    {
        b.ToTable("categories");
        b.HasKey(c => c.Id);
        b.Property(c => c.Id).ValueGeneratedNever();
        b.Property(c => c.Name).HasMaxLength(200).IsRequired();
        b.Property(c => c.Handle).HasMaxLength(200).IsRequired();
        b.HasIndex(c => c.Handle).IsUnique();
    }
}
