using System.Security.Claims;
using AlturaNova.Application.Common.Security;
using AlturaNova.Domain.Enums;
using AlturaNova.Domain.Exceptions;

namespace AlturaNova.Api.Auth;

/// <summary>Resolves the authenticated caller from the current <see cref="HttpContext"/> claims.</summary>
public sealed class CurrentUser(IHttpContextAccessor accessor) : ICurrentUser
{
    private ClaimsPrincipal? Principal => accessor.HttpContext?.User;

    public Guid? UserId
    {
        get
        {
            var value = Principal?.FindFirstValue(ClaimTypes.NameIdentifier);
            return Guid.TryParse(value, out var id) ? id : null;
        }
    }

    public UserRole? Role
    {
        get
        {
            var value = Principal?.FindFirstValue(ClaimTypes.Role);
            return Enum.TryParse<UserRole>(value, out var role) ? role : null;
        }
    }

    public Guid? VendorId
    {
        get
        {
            var value = Principal?.FindFirstValue("vendor_id");
            return Guid.TryParse(value, out var id) ? id : null;
        }
    }

    public bool IsAuthenticated => Principal?.Identity?.IsAuthenticated ?? false;

    public Guid RequireUserId() =>
        UserId ?? throw new UnauthorizedException("Authentication is required.");

    public Guid RequireVendorId() =>
        VendorId ?? throw new UnauthorizedException("A vendor account is required for this operation.");
}
