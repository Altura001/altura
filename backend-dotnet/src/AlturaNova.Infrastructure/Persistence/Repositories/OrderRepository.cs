using AlturaNova.Domain.Entities;
using AlturaNova.Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace AlturaNova.Infrastructure.Persistence.Repositories;

/// <summary>EF Core implementation of <see cref="IOrderRepository"/>.</summary>
public sealed class OrderRepository(AppDbContext db) : IOrderRepository
{
    public async Task<IReadOnlyList<Order>> GetByUserAsync(Guid userId, CancellationToken ct = default) =>
        await ReadGraph(db.Orders.AsNoTracking().Where(o => o.UserId == userId))
            .OrderByDescending(o => o.CreatedAt)
            .ToListAsync(ct);

    public Task<Order?> GetByIdForUserAsync(Guid orderId, Guid userId, CancellationToken ct = default) =>
        ReadGraph(db.Orders.AsNoTracking())
            .FirstOrDefaultAsync(o => o.Id == orderId && o.UserId == userId, ct);

    public Task<Order?> GetTrackedByIdForUserAsync(Guid orderId, Guid userId, CancellationToken ct = default) =>
        db.Orders
            .Include(o => o.Items)
            .Include(o => o.ShippingAddress)
            .AsSplitQuery()
            .FirstOrDefaultAsync(o => o.Id == orderId && o.UserId == userId, ct);

    public Task<Order?> GetTrackedByIdAsync(Guid orderId, CancellationToken ct = default) =>
        db.Orders
            .Include(o => o.Items)
            .Include(o => o.ShippingAddress)
            .AsSplitQuery()
            .FirstOrDefaultAsync(o => o.Id == orderId, ct);

    public Task<Order?> GetTrackedByPaymentReferenceAsync(string paymentReference, CancellationToken ct = default) =>
        db.Orders
            .Include(o => o.Items)
            .Include(o => o.ShippingAddress)
            .AsSplitQuery()
            .FirstOrDefaultAsync(o => o.PaymentReference == paymentReference, ct);

    public async Task<IReadOnlyList<Order>> GetAllAsync(CancellationToken ct = default) =>
        await ReadGraph(db.Orders.AsNoTracking())
            .OrderByDescending(o => o.CreatedAt)
            .ToListAsync(ct);

    public async Task<IReadOnlyList<Order>> GetForVendorAsync(Guid vendorId, CancellationToken ct = default) =>
        await ReadGraph(db.Orders.AsNoTracking()
                .Where(o => o.Items.Any(i => i.VendorId == vendorId)))
            .OrderByDescending(o => o.CreatedAt)
            .ToListAsync(ct);

    public async Task AddAsync(Order order, CancellationToken ct = default) =>
        await db.Orders.AddAsync(order, ct);

    public void Update(Order order) => db.Orders.Update(order);

    private static IQueryable<Order> ReadGraph(IQueryable<Order> query) =>
        query
            .Include(o => o.Items)
            .Include(o => o.ShippingAddress)
            .AsSplitQuery();
}
