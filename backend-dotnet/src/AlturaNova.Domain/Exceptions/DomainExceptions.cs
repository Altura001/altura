namespace AlturaNova.Domain.Exceptions;

/// <summary>Base type for domain-level errors that map to specific HTTP responses.</summary>
public abstract class DomainException(string message) : Exception(message);

/// <summary>Thrown when a requested entity does not exist. Maps to HTTP 404.</summary>
public sealed class NotFoundException(string message) : DomainException(message);

/// <summary>Thrown when an operation conflicts with current state. Maps to HTTP 409.</summary>
public sealed class ConflictException(string message) : DomainException(message);

/// <summary>Thrown when input fails a business/validation rule. Maps to HTTP 400.</summary>
public sealed class DomainValidationException(string message) : DomainException(message);

/// <summary>Thrown when credentials are invalid or the caller is not authorized. Maps to HTTP 401.</summary>
public sealed class UnauthorizedException(string message) : DomainException(message);
