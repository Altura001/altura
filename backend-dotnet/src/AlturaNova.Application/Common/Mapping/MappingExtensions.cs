using AlturaNova.Application.DTOs.Auth;
using AlturaNova.Application.DTOs.Cart;
using AlturaNova.Application.DTOs.Catalog;
using AlturaNova.Application.DTOs.Orders;
using AlturaNova.Application.DTOs.Vendor;
using AlturaNova.Application.DTOs.Wishlist;
using AlturaNova.Domain.Entities;

namespace AlturaNova.Application.Common.Mapping;

/// <summary>Maps domain entities to their API response DTOs.</summary>
public static class MappingExtensions
{
    public static UserResponse ToResponse(this User user) => new(
        user.Id,
        user.Email,
        user.FirstName,
        user.LastName,
        user.Phone,
        user.Role.ToString(),
        user.VendorId);

    public static VendorResponse ToResponse(this Vendor vendor) => new(
        vendor.Id,
        vendor.Name,
        vendor.Handle,
        vendor.Description,
        vendor.LogoUrl,
        vendor.BannerUrl);

    public static VendorStoreResponse ToStoreResponse(this Vendor vendor) => new(
        vendor.Id,
        vendor.Name,
        vendor.Handle,
        vendor.Description,
        vendor.LogoUrl,
        vendor.BannerUrl,
        vendor.IsActive,
        vendor.CreatedAt);

    public static CategoryResponse ToResponse(this Category category) => new(
        category.Id,
        category.Name,
        category.Handle);

    public static ProductVariantResponse ToResponse(this ProductVariant v) => new(
        v.Id,
        v.Title,
        v.Sku,
        v.Price,
        v.Currency,
        v.InventoryQuantity,
        v.IsInStock);

    public static ProductResponse ToResponse(this Product p)
    {
        var defaultVariant = p.Variants.OrderBy(v => v.Price).FirstOrDefault();
        return new ProductResponse(
            p.Id,
            p.VendorId,
            p.Vendor?.Name ?? string.Empty,
            p.Name,
            p.Handle,
            p.Description,
            defaultVariant?.Price ?? 0m,
            p.Currency,
            p.ThumbnailUrl,
            p.Images.OrderBy(i => i.SortOrder).Select(i => i.Url).ToList(),
            p.Category?.Name,
            p.Variants.Any(v => v.InventoryQuantity > 0),
            p.Variants.OrderBy(v => v.Price).Select(v => v.ToResponse()).ToList(),
            p.CreatedAt);
    }

    public static CartItemResponse ToResponse(this CartItem i) => new(
        i.Id,
        i.ProductId,
        i.VariantId,
        i.Title,
        i.Quantity,
        i.UnitPrice,
        i.LineTotal,
        i.Currency,
        i.ThumbnailUrl);

    public static CartResponse ToResponse(this Cart cart)
    {
        var items = cart.Items
            .OrderBy(i => i.CreatedAt)
            .Select(i => i.ToResponse())
            .ToList();
        var subtotal = items.Sum(i => i.LineTotal);
        return new CartResponse(
            cart.Id,
            items,
            subtotal,
            subtotal, // Total == Subtotal for v1 (no shipping/tax yet)
            cart.Currency,
            items.Sum(i => i.Quantity));
    }

    public static AddressResponse? ToResponse(this OrderAddress? a) => a is null
        ? null
        : new AddressResponse(
            a.FirstName, a.LastName, a.Line1, a.Line2, a.City,
            a.State, a.PostalCode, a.Country, a.Phone);

    public static OrderItemResponse ToResponse(this OrderItem i) => new(
        i.Id,
        i.ProductId,
        i.VariantId,
        i.ProductName,
        i.Sku,
        i.ThumbnailUrl,
        i.UnitPrice,
        i.Quantity,
        i.LineTotal,
        i.Currency);

    public static OrderResponse ToResponse(this Order o) => new(
        o.Id,
        o.Status.ToString(),
        o.DeliveryMethod.ToString(),
        o.PickupStation?.Name,
        o.Subtotal,
        o.ShippingFee,
        o.Total,
        o.Currency,
        o.ShippingAddress.ToResponse(),
        o.Items.Select(i => i.ToResponse()).ToList(),
        o.CreatedAt);

    public static WishlistItemResponse ToResponse(this WishlistItem i) => new(
        i.Id,
        i.ProductId,
        i.Product?.Name ?? string.Empty,
        i.Product?.ThumbnailUrl,
        i.Product?.Variants.OrderBy(v => v.Price).FirstOrDefault()?.Price ?? 0m,
        i.Product?.Currency ?? "EUR",
        i.Product?.Variants.Any(v => v.InventoryQuantity > 0) ?? false,
        i.CreatedAt);

    public static WishlistResponse ToResponse(this List<WishlistItem> items, Guid userId) => new(
        userId,
        items.OrderByDescending(i => i.CreatedAt).Select(i => i.ToResponse()).ToList(),
        items.Count);
}
