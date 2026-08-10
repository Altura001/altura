using AlturaNova.Application.DTOs.Auth;

namespace AlturaNova.Application.Interfaces;

/// <summary>Authentication and account lifecycle operations.</summary>
public interface IAuthService
{
    Task<AuthResponse> RegisterCustomerAsync(RegisterCustomerRequest request, CancellationToken ct = default);
    Task<AuthResponse> RegisterVendorAsync(RegisterVendorRequest request, CancellationToken ct = default);
    Task<AuthResponse> LoginAsync(LoginRequest request, CancellationToken ct = default);
    Task<AuthResponse> RefreshAsync(RefreshTokenRequest request, CancellationToken ct = default);
    Task LogoutAsync(string refreshToken, CancellationToken ct = default);
    Task<UserResponse> GetCurrentUserAsync(Guid userId, CancellationToken ct = default);
}
