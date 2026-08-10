using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;

namespace AlturaNova.Infrastructure.Persistence;

/// <summary>
/// Enables EF Core design-time tooling (migrations) without booting the API host.
/// Uses ALTURA_DB_CONNECTION when present, otherwise a local default.
/// </summary>
public sealed class DesignTimeDbContextFactory : IDesignTimeDbContextFactory<AppDbContext>
{
    public AppDbContext CreateDbContext(string[] args)
    {
        var connectionString =
            Environment.GetEnvironmentVariable("ALTURA_DB_CONNECTION")
            ?? "Host=localhost;Port=5433;Database=altura_nova;Username=altura;Password=altura_dev_password";

        var options = new DbContextOptionsBuilder<AppDbContext>()
            .UseNpgsql(connectionString, npgsql =>
                npgsql.MigrationsAssembly(typeof(AppDbContext).Assembly.FullName))
            .Options;

        return new AppDbContext(options);
    }
}
