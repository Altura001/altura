using AlturaNova.Application.Interfaces;
using AlturaNova.Application.Services;
using Microsoft.Extensions.DependencyInjection;

namespace AlturaNova.Application;

/// <summary>Registers Application-layer services into the DI container.</summary>
public static class DependencyInjection
{
    public static IServiceCollection AddApplication(this IServiceCollection services)
    {
        services.AddScoped<IAuthService, AuthService>();
        services.AddScoped<IAccountService, AccountService>();
        services.AddScoped<ICatalogService, CatalogService>();
        services.AddScoped<ICartService, CartService>();
        services.AddScoped<IOrderService, OrderService>();
        services.AddScoped<IVendorService, VendorService>();
        return services;
    }
}
