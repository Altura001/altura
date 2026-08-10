using AlturaNova.Application.Common.Payments;
using AlturaNova.Application.Common.Security;
using AlturaNova.Domain.Interfaces;
using AlturaNova.Infrastructure.Payments;
using AlturaNova.Infrastructure.Persistence;
using AlturaNova.Infrastructure.Persistence.Repositories;
using AlturaNova.Infrastructure.Security;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Options;

namespace AlturaNova.Infrastructure;

/// <summary>Registers Infrastructure-layer services (EF Core, repositories, security) into DI.</summary>
public static class DependencyInjection
{
    public static IServiceCollection AddInfrastructure(
        this IServiceCollection services,
        IConfiguration configuration)
    {
        // Bind JWT options from the "Jwt" configuration section.
        services.Configure<JwtOptions>(configuration.GetSection(JwtOptions.SectionName));

        var connectionString = configuration.GetConnectionString("Default")
            ?? throw new InvalidOperationException("Connection string 'Default' is not configured.");

        services.AddDbContext<AppDbContext>(options =>
            options.UseNpgsql(connectionString, npgsql =>
                    npgsql.MigrationsAssembly(typeof(AppDbContext).Assembly.FullName))
                .EnableDetailedErrors());

        // Unit of work + repositories.
        services.AddScoped<IUnitOfWork, UnitOfWork>();
        services.AddScoped<IUserRepository, UserRepository>();
        services.AddScoped<IVendorRepository, VendorRepository>();
        services.AddScoped<ICategoryRepository, CategoryRepository>();
        services.AddScoped<IProductRepository, ProductRepository>();
        services.AddScoped<ICartRepository, CartRepository>();
        services.AddScoped<IOrderRepository, OrderRepository>();
        services.AddScoped<IRefreshTokenRepository, RefreshTokenRepository>();

        // Security services.
        services.AddSingleton<IPasswordHasher, PasswordHasherAdapter>();
        services.AddSingleton<IJwtTokenService, JwtTokenService>();

        // Payment provider (Paystack).
        services.Configure<PaystackOptions>(configuration.GetSection(PaystackOptions.SectionName));
        services.AddHttpClient<IPaymentGateway, PaystackPaymentGateway>((sp, client) =>
        {
            var paystack = sp.GetRequiredService<IOptions<PaystackOptions>>().Value;
            client.BaseAddress = new Uri(paystack.BaseUrl.TrimEnd('/') + "/");
            if (!string.IsNullOrEmpty(paystack.SecretKey))
            {
                client.DefaultRequestHeaders.Authorization =
                    new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", paystack.SecretKey);
            }
        });

        return services;
    }
}
