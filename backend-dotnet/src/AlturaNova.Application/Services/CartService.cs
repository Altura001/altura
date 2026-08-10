using AlturaNova.Application.Common.Mapping;
using AlturaNova.Application.DTOs.Cart;
using AlturaNova.Application.Interfaces;
using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Exceptions;
using AlturaNova.Domain.Interfaces;

namespace AlturaNova.Application.Services;

/// <summary>Implements per-user cart operations with stock validation.</summary>
public sealed class CartService(
    ICartRepository carts,
    IProductRepository products,
    IUnitOfWork unitOfWork) : ICartService
{
    public async Task<CartResponse> GetCartAsync(Guid userId, CancellationToken ct = default)
    {
        var cart = await GetOrCreateCartAsync(userId, ct);
        return cart.ToResponse();
    }

    public async Task<CartResponse> AddItemAsync(Guid userId, AddCartItemRequest request, CancellationToken ct = default)
    {
        var cart = await GetOrCreateCartAsync(userId, ct);

        var variant = await products.GetVariantAsync(request.VariantId, ct)
            ?? throw new NotFoundException("Product variant not found.");

        var existing = cart.Items.FirstOrDefault(i => i.VariantId == request.VariantId);
        var newQuantity = (existing?.Quantity ?? 0) + request.Quantity;

        if (newQuantity > variant.InventoryQuantity)
            throw new ConflictException(
                $"Insufficient stock: {variant.InventoryQuantity} available, {newQuantity} requested.");

        if (existing is not null)
        {
            existing.Quantity = newQuantity;
        }
        else
        {
            var product = variant.Product;
            cart.Items.Add(new CartItem
            {
                CartId = cart.Id,
                ProductId = variant.ProductId,
                VariantId = variant.Id,
                Title = BuildTitle(product?.Name, variant.Title),
                Quantity = request.Quantity,
                UnitPrice = variant.Price,
                Currency = variant.Currency,
                ThumbnailUrl = product?.ThumbnailUrl
            });
        }

        cart.UpdatedAt = DateTimeOffset.UtcNow;
        await unitOfWork.SaveChangesAsync(ct);
        return cart.ToResponse();
    }

    public async Task<CartResponse> UpdateItemAsync(Guid userId, Guid itemId, UpdateCartItemRequest request, CancellationToken ct = default)
    {
        var cart = await carts.GetActiveByUserAsync(userId, ct)
            ?? throw new NotFoundException("Cart not found.");

        var item = cart.Items.FirstOrDefault(i => i.Id == itemId)
            ?? throw new NotFoundException("Cart item not found.");

        if (request.Quantity <= 0)
        {
            cart.Items.Remove(item);
        }
        else
        {
            var variant = await products.GetVariantAsync(item.VariantId, ct)
                ?? throw new NotFoundException("Product variant not found.");

            if (request.Quantity > variant.InventoryQuantity)
                throw new ConflictException(
                    $"Insufficient stock: {variant.InventoryQuantity} available, {request.Quantity} requested.");

            item.Quantity = request.Quantity;
        }

        cart.UpdatedAt = DateTimeOffset.UtcNow;
        await unitOfWork.SaveChangesAsync(ct);
        return cart.ToResponse();
    }

    public async Task<CartResponse> RemoveItemAsync(Guid userId, Guid itemId, CancellationToken ct = default)
    {
        var cart = await carts.GetActiveByUserAsync(userId, ct)
            ?? throw new NotFoundException("Cart not found.");

        var item = cart.Items.FirstOrDefault(i => i.Id == itemId);
        if (item is not null)
        {
            cart.Items.Remove(item);
            cart.UpdatedAt = DateTimeOffset.UtcNow;
            await unitOfWork.SaveChangesAsync(ct);
        }

        return cart.ToResponse();
    }

    public async Task<CartResponse> ClearAsync(Guid userId, CancellationToken ct = default)
    {
        var cart = await carts.GetActiveByUserAsync(userId, ct);
        if (cart is null)
            return EmptyCart();

        cart.Items.Clear();
        cart.UpdatedAt = DateTimeOffset.UtcNow;
        await unitOfWork.SaveChangesAsync(ct);
        return cart.ToResponse();
    }

    private async Task<Cart> GetOrCreateCartAsync(Guid userId, CancellationToken ct)
    {
        var cart = await carts.GetActiveByUserAsync(userId, ct);
        if (cart is not null)
            return cart;

        cart = new Cart { UserId = userId, Currency = "EUR" };
        await carts.AddAsync(cart, ct);
        await unitOfWork.SaveChangesAsync(ct);
        return cart;
    }

    private static string BuildTitle(string? productName, string variantTitle)
    {
        var name = productName ?? "Item";
        return string.IsNullOrWhiteSpace(variantTitle) || variantTitle.Equals("Default", StringComparison.OrdinalIgnoreCase)
            ? name
            : $"{name} - {variantTitle}";
    }

    private static CartResponse EmptyCart() =>
        new(Guid.Empty, Array.Empty<CartItemResponse>(), 0m, 0m, "EUR", 0);
}
