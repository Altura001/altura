using AlturaNova.Domain.Entities;

namespace AlturaNova.Domain.Interfaces;

/// <summary>Data access for persisted <see cref="RefreshToken"/> values.</summary>
public interface IRefreshTokenRepository
{
    Task<RefreshToken?> GetByTokenAsync(string token, CancellationToken ct = default);
    Task AddAsync(RefreshToken token, CancellationToken ct = default);
    void Update(RefreshToken token);
}
