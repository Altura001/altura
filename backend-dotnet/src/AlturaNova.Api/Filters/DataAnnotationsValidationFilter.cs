using System.ComponentModel.DataAnnotations;

namespace AlturaNova.Api.Filters;

/// <summary>
/// Endpoint filter that validates request DTO arguments using DataAnnotations,
/// recursing into nested DTO objects. Returns 400 ValidationProblem on failure.
/// </summary>
public sealed class DataAnnotationsValidationFilter : IEndpointFilter
{
    public async ValueTask<object?> InvokeAsync(
        EndpointFilterInvocationContext context,
        EndpointFilterDelegate next)
    {
        var errors = new Dictionary<string, string[]>();

        foreach (var argument in context.Arguments)
        {
            if (argument is null)
                continue;

            var type = argument.GetType();
            if (!IsDtoType(type))
                continue;

            Validate(argument, prefix: null, errors, new HashSet<object>(ReferenceEqualityComparer.Instance));
        }

        return errors.Count > 0
            ? Results.ValidationProblem(errors)
            : await next(context);
    }

    private static void Validate(object instance, string? prefix, Dictionary<string, string[]> errors, HashSet<object> visited)
    {
        if (!visited.Add(instance))
            return;

        var context = new ValidationContext(instance);
        var results = new List<ValidationResult>();
        Validator.TryValidateObject(instance, context, results, validateAllProperties: true);

        foreach (var result in results)
        {
            var members = result.MemberNames.Any() ? result.MemberNames : [string.Empty];
            foreach (var member in members)
            {
                var key = Combine(prefix, member);
                var message = result.ErrorMessage ?? "Invalid value.";
                errors[key] = errors.TryGetValue(key, out var existing)
                    ? [.. existing, message]
                    : [message];
            }
        }

        // Recurse into nested DTO properties (e.g. CheckoutRequest.ShippingAddress).
        foreach (var property in instance.GetType().GetProperties())
        {
            if (!property.CanRead || property.GetIndexParameters().Length > 0)
                continue;

            if (!IsDtoType(property.PropertyType))
                continue;

            var value = property.GetValue(instance);
            if (value is not null)
                Validate(value, Combine(prefix, property.Name), errors, visited);
        }
    }

    private static bool IsDtoType(Type type) =>
        type is { IsClass: true, Namespace: not null }
        && type != typeof(string)
        && type.Namespace.StartsWith("AlturaNova.Application.DTOs", StringComparison.Ordinal);

    private static string Combine(string? prefix, string member) =>
        string.IsNullOrEmpty(prefix)
            ? member
            : string.IsNullOrEmpty(member) ? prefix : $"{prefix}.{member}";
}
