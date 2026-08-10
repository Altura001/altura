using AlturaNova.Domain.Enums;

namespace AlturaNova.Domain.Entities;

/// <summary>An application user. May be a customer, a vendor owner, or an admin.</summary>
public class User
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public string Email { get; set; } = string.Empty;
    public string PasswordHash { get; set; } = string.Empty;
    public string FirstName { get; set; } = string.Empty;
    public string LastName { get; set; } = string.Empty;
    public string? Phone { get; set; }
    public UserRole Role { get; set; } = UserRole.Customer;

    /// <summary>Set when this user owns a vendor store (Role == Vendor).</summary>
    public Guid? VendorId { get; set; }
    public Vendor? Vendor { get; set; }

    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;
    public DateTimeOffset UpdatedAt { get; set; } = DateTimeOffset.UtcNow;

    public ICollection<RefreshToken> RefreshTokens { get; set; } = new List<RefreshToken>();
}
