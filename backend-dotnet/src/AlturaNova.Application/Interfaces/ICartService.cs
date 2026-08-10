using AlturaNova.Application.DTOs.Cart;

namespace AlturaNova.Application.Interfaces;

/// <summary>Cart operations scoped to a single authenticated user.</summary>
public interface ICartService
{
    Task<CartResponse> GetCartAsync(Guid userId, CancellationToken ct = default);
    Task<CartResponse> AddItemAsync(Guid userId, AddCartItemRequest request, CancellationToken ct = default);
    Task<CartResponse> UpdateItemAsync(Guid userId, Guid itemId, UpdateCartItemRequest request, CancellationToken ct = default);
    Task<CartResponse> RemoveItemAsync(Guid userId, Guid itemId, CancellationToken ct = default);
    Task<CartResponse> ClearAsync(Guid userId, CancellationToken ct = default);
}
