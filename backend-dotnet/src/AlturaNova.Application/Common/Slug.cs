using System.Globalization;
using System.Text;

namespace AlturaNova.Application.Common;

/// <summary>Generates URL-friendly slugs/handles from arbitrary text.</summary>
public static class Slug
{
    public static string From(string input)
    {
        if (string.IsNullOrWhiteSpace(input))
            return Guid.NewGuid().ToString("n")[..8];

        var normalized = input.Trim().ToLowerInvariant().Normalize(NormalizationForm.FormD);
        var sb = new StringBuilder(normalized.Length);
        var lastWasDash = false;

        foreach (var ch in normalized)
        {
            var category = CharUnicodeInfo.GetUnicodeCategory(ch);
            if (category == UnicodeCategory.NonSpacingMark)
                continue;

            if (char.IsLetterOrDigit(ch))
            {
                sb.Append(ch);
                lastWasDash = false;
            }
            else if (!lastWasDash && sb.Length > 0)
            {
                sb.Append('-');
                lastWasDash = true;
            }
        }

        var result = sb.ToString().Trim('-');
        return result.Length == 0 ? Guid.NewGuid().ToString("n")[..8] : result;
    }
}
