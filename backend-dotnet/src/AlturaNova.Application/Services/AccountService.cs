using AlturaNova.Application.Common.Mapping;
using AlturaNova.Application.Common.Security;
using AlturaNova.Application.DTOs.Account;
using AlturaNova.Application.DTOs.Auth;
using AlturaNova.Application.Interfaces;
using AlturaNova.Domain.Exceptions;
using AlturaNova.Domain.Interfaces;

namespace AlturaNova.Application.Services;

/// <summary>Implements self-service profile and password operations.</summary>
public sealed class AccountService(
    IUserRepository users,
    IPasswordHasher passwordHasher,
    IUnitOfWork unitOfWork) : IAccountService
{
    public async Task<UserResponse> UpdateProfileAsync(Guid userId, UpdateProfileRequest request, CancellationToken ct = default)
    {
        var user = await users.GetTrackedByIdAsync(userId, ct)
            ?? throw new NotFoundException("User not found.");

        user.FirstName = request.FirstName.Trim();
        user.LastName = request.LastName.Trim();
        user.Phone = request.Phone;
        user.UpdatedAt = DateTimeOffset.UtcNow;
        users.Update(user);

        await unitOfWork.SaveChangesAsync(ct);
        return user.ToResponse();
    }

    public async Task ChangePasswordAsync(Guid userId, ChangePasswordRequest request, CancellationToken ct = default)
    {
        var user = await users.GetTrackedByIdAsync(userId, ct)
            ?? throw new NotFoundException("User not found.");

        if (!passwordHasher.Verify(user.PasswordHash, request.CurrentPassword))
            throw new UnauthorizedException("The current password is incorrect.");

        user.PasswordHash = passwordHasher.Hash(request.NewPassword);
        user.UpdatedAt = DateTimeOffset.UtcNow;
        users.Update(user);

        await unitOfWork.SaveChangesAsync(ct);
    }
}
