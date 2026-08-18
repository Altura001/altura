using AlturaNova.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace AlturaNova.Infrastructure.Persistence.Configurations;

/// <summary>EF configuration for pickup stations.</summary>
public sealed class PickupStationConfiguration : IEntityTypeConfiguration<PickupStation>
{
    public void Configure(EntityTypeBuilder<PickupStation> b)
    {
        b.ToTable("pickup_stations");
        b.HasKey(s => s.Id);
        b.Property(s => s.Id).ValueGeneratedNever();
        b.Property(s => s.Name).HasMaxLength(200).IsRequired();
        b.Property(s => s.Address).HasMaxLength(500).IsRequired();
        b.Property(s => s.City).HasMaxLength(100).IsRequired();
        b.Property(s => s.Phone).HasMaxLength(40);
        b.Property(s => s.OperatingHours).HasMaxLength(200);
        b.HasIndex(s => s.IsActive);
    }
}
