using AlturaNova.Domain.Exceptions;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace AlturaNova.Api.Middleware;

/// <summary>Maps domain exceptions to RFC 7807 Problem Details responses.</summary>
internal sealed class ApiExceptionHandler(ILogger<ApiExceptionHandler> logger) : IExceptionHandler
{
    public async ValueTask<bool> TryHandleAsync(
        HttpContext httpContext,
        Exception exception,
        CancellationToken cancellationToken)
    {
        var (statusCode, title, detail) = exception switch
        {
            NotFoundException => (StatusCodes.Status404NotFound, "Not Found", exception.Message),
            ConflictException => (StatusCodes.Status409Conflict, "Conflict", exception.Message),
            DomainValidationException => (StatusCodes.Status400BadRequest, "Bad Request", exception.Message),
            UnauthorizedException => (StatusCodes.Status401Unauthorized, "Unauthorized", exception.Message),
            // Malformed request body (bad JSON, invalid enum value, etc.).
            BadHttpRequestException => (StatusCodes.Status400BadRequest, "Bad Request",
                "The request body is malformed or contains invalid values."),
            // Optimistic-concurrency failures (e.g. inventory changed mid-checkout) → retryable conflict.
            DbUpdateConcurrencyException => (StatusCodes.Status409Conflict, "Conflict",
                "The item was modified by another request. Please retry."),
            _ => (0, string.Empty, string.Empty)
        };

        if (statusCode == 0)
            return false; // Not a known domain exception; let the default handler produce a 500.

        logger.LogWarning(exception, "Handled exception: {Title}", title);

        var problem = new ProblemDetails
        {
            Status = statusCode,
            Title = title,
            Detail = detail,
            Instance = httpContext.Request.Path
        };

        httpContext.Response.StatusCode = statusCode;
        await httpContext.Response.WriteAsJsonAsync(problem, cancellationToken);
        return true;
    }
}
