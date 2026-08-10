using AlturaNova.Domain.Enums;

namespace AlturaNova.Application.Common.Security;

/// <summary>Provides identity information about the authenticated caller for the current request.</summary>
public interface ICurrentUser
{
    Guid? UserId { get; }
    UserRole? Role { get; }

    /// <summary>The vendor the caller owns, when the account is a vendor.</summary>
    Guid? VendorId { get; }

    bool IsAuthenticated { get; }

    /// <summary>Returns the authenticated user id or throws if the request is anonymous.</summary>
    Guid RequireUserId();

    /// <summary>Returns the caller's vendor id or throws if the account is not a vendor.</summary>
    Guid RequireVendorId();
}
