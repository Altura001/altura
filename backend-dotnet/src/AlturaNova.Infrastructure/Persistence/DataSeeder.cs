using AlturaNova.Application.Common.Security;
using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Enums;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;

namespace AlturaNova.Infrastructure.Persistence;

/// <summary>Applies pending migrations and seeds baseline catalog data on startup.</summary>
public static class DataSeeder
{
    public static async Task MigrateAndSeedAsync(IServiceProvider services, CancellationToken ct = default)
    {
        using var scope = services.CreateScope();
        var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
        var passwordHasher = scope.ServiceProvider.GetRequiredService<IPasswordHasher>();
        var logger = scope.ServiceProvider.GetRequiredService<ILoggerFactory>().CreateLogger("DataSeeder");

        logger.LogInformation("Applying database migrations...");
        await db.Database.MigrateAsync(ct);

        if (await db.Vendors.AnyAsync(ct))
        {
            logger.LogInformation("Seed data already present; skipping catalog seed.");

            if (!await db.PickupStations.AnyAsync(ct))
            {
                logger.LogInformation("Pickup stations missing — seeding...");
                db.PickupStations.AddRange(
                    new PickupStation
                    {
                        Name = "Altura Hub - Lekki",
                        Address = "15 Admiralty Way, Lekki Phase 1, Lagos",
                        City = "Lagos",
                        Phone = "+234 801 234 5678",
                        OperatingHours = "Mon-Fri: 9AM-6PM, Sat: 10AM-4PM",
                        IsActive = true
                    },
                    new PickupStation
                    {
                        Name = "Altura Hub - Ikeja",
                        Address = "22 Opebi Road, Ikeja, Lagos",
                        City = "Lagos",
                        Phone = "+234 802 345 6789",
                        OperatingHours = "Mon-Fri: 8AM-5PM, Sat: 9AM-3PM",
                        IsActive = true
                    },
                    new PickupStation
                    {
                        Name = "Altura Hub - Victoria Island",
                        Address = "8 Akin Adesola Street, Victoria Island, Lagos",
                        City = "Lagos",
                        Phone = "+234 803 456 7890",
                        OperatingHours = "Mon-Fri: 9AM-6PM",
                        IsActive = true
                    },
                    new PickupStation
                    {
                        Name = "Altura Hub - Abuja Central",
                        Address = "45 Aminu Kano Crescent, Wuse 2, Abuja",
                        City = "Abuja",
                        Phone = "+234 804 567 8901",
                        OperatingHours = "Mon-Fri: 9AM-5PM, Sat: 10AM-2PM",
                        IsActive = true
                    },
                    new PickupStation
                    {
                        Name = "Altura Hub - Port Harcourt",
                        Address = "12 Olu Obasanjo Road, Port Harcourt, Rivers",
                        City = "Port Harcourt",
                        Phone = "+234 805 678 9012",
                        OperatingHours = "Mon-Fri: 9AM-5PM",
                        IsActive = true
                    }
                );
                await db.SaveChangesAsync(ct);
                logger.LogInformation("Pickup stations seeded.");
            }

            return;
        }

        logger.LogInformation("Seeding baseline catalog data...");

        var electronics = new Category { Name = "Electronics", Handle = "electronics" };
        var fashion = new Category { Name = "Fashion", Handle = "fashion" };
        var home = new Category { Name = "Home & Living", Handle = "home-living" };
        db.Categories.AddRange(electronics, fashion, home);

        var techVendor = new Vendor
        {
            Name = "Altura Tech",
            Handle = "altura-tech",
            Description = "Gadgets, audio, and accessories.",
            IsActive = true
        };
        var styleVendor = new Vendor
        {
            Name = "Nova Style",
            Handle = "nova-style",
            Description = "Everyday fashion and apparel.",
            IsActive = true
        };
        var homeVendor = new Vendor
        {
            Name = "Casa Nova",
            Handle = "casa-nova",
            Description = "Home essentials and decor.",
            IsActive = true
        };
        db.Vendors.AddRange(techVendor, styleVendor, homeVendor);

        // Seed baseline accounts (dev credentials): admin, a vendor owner for Altura Tech, and a customer.
        const string demoPassword = "Password123!";
        db.Users.AddRange(
            new User
            {
                Email = "admin@altura.test",
                PasswordHash = passwordHasher.Hash(demoPassword),
                FirstName = "Altura",
                LastName = "Admin",
                Role = UserRole.Admin
            },
            new User
            {
                Email = "vendor@altura.test",
                PasswordHash = passwordHasher.Hash(demoPassword),
                FirstName = "Tech",
                LastName = "Owner",
                Role = UserRole.Vendor,
                VendorId = techVendor.Id
            },
            new User
            {
                Email = "customer@altura.test",
                PasswordHash = passwordHasher.Hash(demoPassword),
                FirstName = "Casey",
                LastName = "Customer",
                Role = UserRole.Customer
            });

        db.Products.AddRange(
            BuildProduct(techVendor, electronics, "Wireless Headphones", "wireless-headphones",
                "Over-ear Bluetooth headphones with active noise cancellation.", 129.99m,
                "https://picsum.photos/seed/headphones/600", stock: 40),
            BuildProduct(techVendor, electronics, "Smart Watch", "smart-watch",
                "Fitness tracking smart watch with heart-rate monitoring.", 89.50m,
                "https://picsum.photos/seed/smartwatch/600", stock: 25),
            BuildProduct(techVendor, electronics, "USB-C Charger", "usb-c-charger",
                "65W fast charger with dual USB-C ports.", 34.00m,
                "https://picsum.photos/seed/charger/600", stock: 120),
            BuildProduct(styleVendor, fashion, "Classic T-Shirt", "classic-t-shirt",
                "100% organic cotton crew-neck t-shirt.", 19.99m,
                "https://picsum.photos/seed/tshirt/600", stock: 200, withSizes: true),
            BuildProduct(styleVendor, fashion, "Denim Jacket", "denim-jacket",
                "Vintage-wash denim jacket with button front.", 59.99m,
                "https://picsum.photos/seed/denim/600", stock: 60, withSizes: true),
            BuildProduct(homeVendor, home, "Ceramic Mug", "ceramic-mug",
                "350ml handcrafted ceramic mug.", 12.50m,
                "https://picsum.photos/seed/mug/600", stock: 150),
            BuildProduct(homeVendor, home, "Scented Candle", "scented-candle",
                "Soy-wax candle with a 40-hour burn time.", 22.00m,
                "https://picsum.photos/seed/candle/600", stock: 80)
        );

        await db.SaveChangesAsync(ct);

        logger.LogInformation("Seeding pickup stations...");

        db.PickupStations.AddRange(
            new PickupStation
            {
                Name = "Altura Hub - Lekki",
                Address = "15 Admiralty Way, Lekki Phase 1, Lagos",
                City = "Lagos",
                Phone = "+234 801 234 5678",
                OperatingHours = "Mon-Fri: 9AM-6PM, Sat: 10AM-4PM",
                IsActive = true
            },
            new PickupStation
            {
                Name = "Altura Hub - Ikeja",
                Address = "22 Opebi Road, Ikeja, Lagos",
                City = "Lagos",
                Phone = "+234 802 345 6789",
                OperatingHours = "Mon-Fri: 8AM-5PM, Sat: 9AM-3PM",
                IsActive = true
            },
            new PickupStation
            {
                Name = "Altura Hub - Victoria Island",
                Address = "8 Akin Adesola Street, Victoria Island, Lagos",
                City = "Lagos",
                Phone = "+234 803 456 7890",
                OperatingHours = "Mon-Fri: 9AM-6PM",
                IsActive = true
            },
            new PickupStation
            {
                Name = "Altura Hub - Abuja Central",
                Address = "45 Aminu Kano Crescent, Wuse 2, Abuja",
                City = "Abuja",
                Phone = "+234 804 567 8901",
                OperatingHours = "Mon-Fri: 9AM-5PM, Sat: 10AM-2PM",
                IsActive = true
            },
            new PickupStation
            {
                Name = "Altura Hub - Port Harcourt",
                Address = "12 Olu Obasanjo Road, Port Harcourt, Rivers",
                City = "Port Harcourt",
                Phone = "+234 805 678 9012",
                OperatingHours = "Mon-Fri: 9AM-5PM",
                IsActive = true
            }
        );

        await db.SaveChangesAsync(ct);
        logger.LogInformation("Seed complete.");
    }

    private static Product BuildProduct(
        Vendor vendor,
        Category category,
        string name,
        string handle,
        string description,
        decimal price,
        string thumbnailUrl,
        int stock,
        bool withSizes = false)
    {
        var product = new Product
        {
            Vendor = vendor,
            Category = category,
            Name = name,
            Handle = handle,
            Description = description,
            ThumbnailUrl = thumbnailUrl,
            Currency = "EUR",
            IsPublished = true,
            Images =
            {
                new ProductImage { Url = thumbnailUrl, SortOrder = 0 }
            }
        };

        if (withSizes)
        {
            foreach (var (size, index) in new[] { "S", "M", "L", "XL" }.Select((s, i) => (s, i)))
            {
                product.Variants.Add(new ProductVariant
                {
                    Title = size,
                    Sku = $"{handle}-{size}".ToUpperInvariant(),
                    Price = price,
                    Currency = "EUR",
                    InventoryQuantity = stock / 4
                });
            }
        }
        else
        {
            product.Variants.Add(new ProductVariant
            {
                Title = "Default",
                Sku = handle.ToUpperInvariant(),
                Price = price,
                Currency = "EUR",
                InventoryQuantity = stock
            });
        }

        return product;
    }
}
