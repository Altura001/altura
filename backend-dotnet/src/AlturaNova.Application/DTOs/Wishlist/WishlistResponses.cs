namespace AlturaNova.Application.DTOs.Wishlist;

/// <summary>A single wishlisted product.</summary>
public sealed record WishlistItemResponse(
    Guid Id,
    Guid ProductId,
    string ProductName,
    string? ThumbnailUrl,
    decimal Price,
    string Currency,
    bool InStock,
    DateTimeOffset AddedAt);

/// <summary>The user's full wishlist.</summary>
public sealed record WishlistResponse(
    Guid UserId,
    IReadOnlyList<WishlistItemResponse> Items,
    int ItemCount);
