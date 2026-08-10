using AlturaNova.Application.Common;
using AlturaNova.Application.Common.Mapping;
using AlturaNova.Application.Common.Security;
using AlturaNova.Application.DTOs.Auth;
using AlturaNova.Application.Interfaces;
using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Enums;
using AlturaNova.Domain.Exceptions;
using AlturaNova.Domain.Interfaces;

namespace AlturaNova.Application.Services;

/// <summary>Implements authentication, registration, and token lifecycle.</summary>
public sealed class AuthService(
    IUserRepository users,
    IVendorRepository vendors,
    IRefreshTokenRepository refreshTokens,
    IPasswordHasher passwordHasher,
    IJwtTokenService jwt,
    IUnitOfWork unitOfWork) : IAuthService
{
    public async Task<AuthResponse> RegisterCustomerAsync(RegisterCustomerRequest request, CancellationToken ct = default)
    {
        var email = Normalize(request.Email);
        if (await users.EmailExistsAsync(email, ct))
            throw new ConflictException("An account with this email already exists.");

        var user = new User
        {
            Email = email,
            PasswordHash = passwordHasher.Hash(request.Password),
            FirstName = request.FirstName.Trim(),
            LastName = request.LastName.Trim(),
            Phone = request.Phone,
            Role = UserRole.Customer
        };

        await users.AddAsync(user, ct);
        return await IssueTokensAsync(user, ct);
    }

    public async Task<AuthResponse> RegisterVendorAsync(RegisterVendorRequest request, CancellationToken ct = default)
    {
        var email = Normalize(request.Email);
        if (await users.EmailExistsAsync(email, ct))
            throw new ConflictException("An account with this email already exists.");

        var handle = await GenerateUniqueVendorHandleAsync(request.StoreName, ct);
        var vendor = new Vendor
        {
            Name = request.StoreName.Trim(),
            Handle = handle,
            Description = string.Empty,
            IsActive = true
        };
        await vendors.AddAsync(vendor, ct);

        var user = new User
        {
            Email = email,
            PasswordHash = passwordHasher.Hash(request.Password),
            FirstName = request.FirstName.Trim(),
            LastName = request.LastName.Trim(),
            Phone = request.Phone,
            Role = UserRole.Vendor,
            VendorId = vendor.Id
        };
        await users.AddAsync(user, ct);

        return await IssueTokensAsync(user, ct);
    }

    public async Task<AuthResponse> LoginAsync(LoginRequest request, CancellationToken ct = default)
    {
        var email = Normalize(request.Email);
        var user = await users.GetByEmailAsync(email, ct);
        if (user is null || !passwordHasher.Verify(user.PasswordHash, request.Password))
            throw new UnauthorizedException("Invalid email or password.");

        return await IssueTokensAsync(user, ct);
    }

    public async Task<AuthResponse> RefreshAsync(RefreshTokenRequest request, CancellationToken ct = default)
    {
        var existing = await refreshTokens.GetByTokenAsync(request.RefreshToken, ct);
        if (existing is null || !existing.IsActive)
            throw new UnauthorizedException("Invalid or expired refresh token.");

        // Rotate: revoke the presented token and issue a new pair.
        existing.RevokedAt = DateTimeOffset.UtcNow;
        refreshTokens.Update(existing);

        var user = await users.GetByIdAsync(existing.UserId, ct)
            ?? throw new UnauthorizedException("Invalid or expired refresh token.");

        return await IssueTokensAsync(user, ct);
    }

    public async Task LogoutAsync(string refreshToken, CancellationToken ct = default)
    {
        var existing = await refreshTokens.GetByTokenAsync(refreshToken, ct);
        if (existing is { RevokedAt: null })
        {
            existing.RevokedAt = DateTimeOffset.UtcNow;
            refreshTokens.Update(existing);
            await unitOfWork.SaveChangesAsync(ct);
        }
    }

    public async Task<UserResponse> GetCurrentUserAsync(Guid userId, CancellationToken ct = default)
    {
        var user = await users.GetByIdAsync(userId, ct)
            ?? throw new NotFoundException("User not found.");
        return user.ToResponse();
    }

    private async Task<AuthResponse> IssueTokensAsync(User user, CancellationToken ct)
    {
        var access = jwt.CreateAccessToken(user);
        var refresh = jwt.CreateRefreshToken();

        await refreshTokens.AddAsync(new RefreshToken
        {
            UserId = user.Id,
            Token = refresh,
            ExpiresAt = jwt.GetRefreshTokenExpiry()
        }, ct);

        await unitOfWork.SaveChangesAsync(ct);

        return new AuthResponse(user.ToResponse(), access.Value, refresh, access.ExpiresAt);
    }

    private async Task<string> GenerateUniqueVendorHandleAsync(string storeName, CancellationToken ct)
    {
        var baseHandle = Slug.From(storeName);
        var handle = baseHandle;
        var suffix = 1;
        while (await vendors.HandleExistsAsync(handle, ct))
            handle = $"{baseHandle}-{++suffix}";
        return handle;
    }

    private static string Normalize(string email) => email.Trim().ToLowerInvariant();
}
