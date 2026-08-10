using AlturaNova.Domain.Entities;

namespace AlturaNova.Domain.Interfaces;

/// <summary>Data access for <see cref="User"/> aggregates.</summary>
public interface IUserRepository
{
    Task<User?> GetByIdAsync(Guid id, CancellationToken ct = default);

    /// <summary>Returns a change-tracked user for mutation (profile/password updates).</summary>
    Task<User?> GetTrackedByIdAsync(Guid id, CancellationToken ct = default);

    Task<User?> GetByEmailAsync(string email, CancellationToken ct = default);
    Task<bool> EmailExistsAsync(string email, CancellationToken ct = default);
    Task AddAsync(User user, CancellationToken ct = default);
    void Update(User user);
}
