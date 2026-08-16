using AlturaNova.Application.Common.Mapping;
using AlturaNova.Application.DTOs.Wishlist;
using AlturaNova.Application.Interfaces;
using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Exceptions;
using AlturaNova.Domain.Interfaces;

namespace AlturaNova.Application.Services;

/// <summary>Implements per-user wishlist operations.</summary>
public sealed class WishlistService(
    IWishlistRepository wishlist,
    IProductRepository products,
    IUnitOfWork unitOfWork) : IWishlistService
{
    public async Task<WishlistResponse> GetWishlistAsync(Guid userId, CancellationToken ct = default)
    {
        var items = await wishlist.GetAllByUserAsync(userId, ct);
        return items.ToResponse(userId);
    }

    public async Task<WishlistResponse> AddItemAsync(Guid userId, AddWishlistItemRequest request, CancellationToken ct = default)
    {
        var existing = await wishlist.FindAsync(userId, request.ProductId, ct);
        if (existing is not null)
            throw new ConflictException("Product is already in your wishlist.");

        var product = await products.GetByIdAsync(request.ProductId, ct)
            ?? throw new NotFoundException("Product not found.");

        var item = new WishlistItem
        {
            UserId = userId,
            ProductId = request.ProductId
        };

        await wishlist.AddAsync(item, ct);
        await unitOfWork.SaveChangesAsync(ct);

        return await GetWishlistAsync(userId, ct);
    }

    public async Task<WishlistResponse> RemoveItemAsync(Guid userId, Guid productId, CancellationToken ct = default)
    {
        var item = await wishlist.FindAsync(userId, productId, ct);
        if (item is null)
            throw new NotFoundException("Product is not in your wishlist.");

        wishlist.Remove(item);
        await unitOfWork.SaveChangesAsync(ct);

        return await GetWishlistAsync(userId, ct);
    }

    public async Task<WishlistResponse> ToggleItemAsync(Guid userId, Guid productId, CancellationToken ct = default)
    {
        var existing = await wishlist.FindAsync(userId, productId, ct);
        if (existing is not null)
        {
            wishlist.Remove(existing);
            await unitOfWork.SaveChangesAsync(ct);
            return await GetWishlistAsync(userId, ct);
        }

        var product = await products.GetByIdAsync(productId, ct)
            ?? throw new NotFoundException("Product not found.");

        var item = new WishlistItem
        {
            UserId = userId,
            ProductId = productId
        };

        await wishlist.AddAsync(item, ct);
        await unitOfWork.SaveChangesAsync(ct);

        return await GetWishlistAsync(userId, ct);
    }

    public async Task<bool> IsProductWishlistedAsync(Guid userId, Guid productId, CancellationToken ct = default)
    {
        var item = await wishlist.FindAsync(userId, productId, ct);
        return item is not null;
    }
}
