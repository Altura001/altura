using AlturaNova.Application.Common.Security;
using Microsoft.AspNetCore.Identity;

namespace AlturaNova.Infrastructure.Security;

/// <summary>Adapts ASP.NET Core's <see cref="PasswordHasher{TUser}"/> to the application interface.</summary>
public sealed class PasswordHasherAdapter : IPasswordHasher
{
    private static readonly object Dummy = new();
    private readonly PasswordHasher<object> _hasher = new();

    public string Hash(string password) => _hasher.HashPassword(Dummy, password);

    public bool Verify(string passwordHash, string providedPassword)
    {
        var result = _hasher.VerifyHashedPassword(Dummy, passwordHash, providedPassword);
        return result is PasswordVerificationResult.Success or PasswordVerificationResult.SuccessRehashNeeded;
    }
}
