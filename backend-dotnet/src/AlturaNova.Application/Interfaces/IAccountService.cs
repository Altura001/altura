using AlturaNova.Application.DTOs.Account;
using AlturaNova.Application.DTOs.Auth;

namespace AlturaNova.Application.Interfaces;

/// <summary>Self-service account operations for the authenticated user.</summary>
public interface IAccountService
{
    Task<UserResponse> UpdateProfileAsync(Guid userId, UpdateProfileRequest request, CancellationToken ct = default);
    Task ChangePasswordAsync(Guid userId, ChangePasswordRequest request, CancellationToken ct = default);
}
