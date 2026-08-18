using System.Text;
using System.Text.Json.Serialization;
using AlturaNova.Api.Auth;
using AlturaNova.Api.Endpoints;
using AlturaNova.Api.Middleware;
using AlturaNova.Application;
using AlturaNova.Application.Common.Security;
using AlturaNova.Infrastructure;
using AlturaNova.Infrastructure.Persistence;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;

var builder = WebApplication.CreateBuilder(args);

// --- Services -------------------------------------------------------------

builder.Services.AddOpenApi();

builder.Services.AddApplication();
builder.Services.AddInfrastructure(builder.Configuration);

builder.Services.AddHttpContextAccessor();
builder.Services.AddScoped<ICurrentUser, CurrentUser>();

// RFC 7807 problem details + domain-exception mapping.
builder.Services.AddProblemDetails();
builder.Services.AddExceptionHandler<ApiExceptionHandler>();

// Serialize enums as strings in JSON responses/schemas.
builder.Services.ConfigureHttpJsonOptions(options =>
    options.SerializerOptions.Converters.Add(new JsonStringEnumConverter()));

// Authentication / authorization.
var jwtOptions = builder.Configuration.GetSection(JwtOptions.SectionName).Get<JwtOptions>()
    ?? throw new InvalidOperationException("Jwt configuration section is missing.");

builder.Services
    .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidIssuer = jwtOptions.Issuer,
            ValidateAudience = true,
            ValidAudience = jwtOptions.Audience,
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtOptions.Secret)),
            ValidateLifetime = true,
            ClockSkew = TimeSpan.FromSeconds(30)
        };
    });

builder.Services.AddAuthorization();

const string CorsPolicy = "AlturaNovaCors";
builder.Services.AddCors(options =>
    options.AddPolicy(CorsPolicy, policy =>
        policy.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod()));

var app = builder.Build();

// --- Pipeline -------------------------------------------------------------

app.UseExceptionHandler();
app.UseStatusCodePages();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseCors(CorsPolicy);
app.UseAuthentication();
app.UseAuthorization();

app.MapGet("/health", () => Results.Ok(new { status = "healthy" }))
    .WithTags("System")
    .WithName("HealthCheck")
    .WithSummary("Liveness probe");

var api = app.MapGroup("/api");
api.MapAuthEndpoints();
api.MapAccountEndpoints();
api.MapCatalogEndpoints();
api.MapCartEndpoints();
api.MapWishlistEndpoints();
api.MapOrderEndpoints();
api.MapPickupStationEndpoints();
api.MapVendorEndpoints();
api.MapAdminEndpoints();
api.MapWebhookEndpoints();

// Apply migrations and seed baseline data (toggle with Database:MigrateOnStartup).
if (app.Configuration.GetValue("Database:MigrateOnStartup", true))
{
    await DataSeeder.MigrateAndSeedAsync(app.Services);
}

app.Run();

/// <summary>Exposed so integration tests can reference the API host via WebApplicationFactory.</summary>
public partial class Program;
