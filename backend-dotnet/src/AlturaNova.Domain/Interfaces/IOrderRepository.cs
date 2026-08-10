using AlturaNova.Domain.Entities;

namespace AlturaNova.Domain.Interfaces;

/// <summary>Data access for <see cref="Order"/> aggregates (includes items and address).</summary>
public interface IOrderRepository
{
    Task<IReadOnlyList<Order>> GetByUserAsync(Guid userId, CancellationToken ct = default);
    Task<Order?> GetByIdForUserAsync(Guid orderId, Guid userId, CancellationToken ct = default);

    /// <summary>Change-tracked order (with items) owned by a user, for pay/cancel flows.</summary>
    Task<Order?> GetTrackedByIdForUserAsync(Guid orderId, Guid userId, CancellationToken ct = default);

    /// <summary>Change-tracked order (with items) regardless of owner, for admin status changes.</summary>
    Task<Order?> GetTrackedByIdAsync(Guid orderId, CancellationToken ct = default);

    /// <summary>Change-tracked order located by its payment reference, for webhook confirmation.</summary>
    Task<Order?> GetTrackedByPaymentReferenceAsync(string paymentReference, CancellationToken ct = default);

    /// <summary>All orders (admin view).</summary>
    Task<IReadOnlyList<Order>> GetAllAsync(CancellationToken ct = default);

    /// <summary>Orders that contain at least one item sold by the given vendor.</summary>
    Task<IReadOnlyList<Order>> GetForVendorAsync(Guid vendorId, CancellationToken ct = default);

    Task AddAsync(Order order, CancellationToken ct = default);
    void Update(Order order);
}
