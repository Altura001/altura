# Altura Nova — .NET Backend

A clean-architecture ASP.NET Core (.NET 10) Web API that provides authentication and
basic store functionality (vendors, products, cart, orders) for the Altura Nova app.

It runs **alongside** the existing Medusa backend — nothing here modifies or removes Medusa.
Ports are deliberately offset so both stacks can run at once:

| Stack        | Postgres | API/Server |
|--------------|----------|------------|
| Medusa       | 5432     | 9000       |
| .NET (this)  | 5433     | 8080       |

## Architecture

Clean Architecture, one project per layer:

```
src/
├── AlturaNova.Domain/          # Entities, enums, exceptions, repository interfaces (no dependencies)
├── AlturaNova.Application/     # DTOs, service interfaces + implementations, mapping, security abstractions
├── AlturaNova.Infrastructure/  # EF Core DbContext, configurations, repositories, JWT, password hashing, seeding
└── AlturaNova.Api/             # Minimal-API endpoints, middleware, validation filter, Program.cs
```

- **API style**: Minimal APIs organised into resource endpoint classes (`*Endpoints.cs`).
- **Data**: EF Core + PostgreSQL (Npgsql). Migrations live in `Infrastructure/Persistence/Migrations`.
- **Auth**: JWT bearer access tokens + rotating refresh tokens (persisted). Roles: `Customer`, `Vendor`, `Admin`.
- **Errors**: RFC 7807 Problem Details via a global `IExceptionHandler` that maps domain exceptions.
- **Validation**: DataAnnotations on request DTOs, enforced by a recursive endpoint filter.
- **Docs**: OpenAPI 3.1 at `/openapi/v1.json` (Development).

## Running

### With Docker (recommended)

```bash
cd backend-dotnet
docker compose up -d --build
# API:      http://localhost:8080
# Health:   http://localhost:8080/health
# OpenAPI:  http://localhost:8080/openapi/v1.json
```

On first boot the API applies migrations and seeds baseline vendors/products and demo accounts.

### Seeded accounts (dev only)

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@altura.test` | `Password123!` |
| Vendor (owns "Altura Tech") | `vendor@altura.test` | `Password123!` |
| Customer | `customer@altura.test` | `Password123!` |

### Locally (API on host, Postgres in Docker)

```bash
cd backend-dotnet
docker compose up -d postgres
dotnet run --project src/AlturaNova.Api --urls http://localhost:8080
```

Connection string / secrets are in `src/AlturaNova.Api/appsettings.json` and can be
overridden with environment variables (e.g. `ConnectionStrings__Default`, `Jwt__Secret`).

### Migrations

```bash
# Add a migration
dotnet ef migrations add <Name> \
  -p src/AlturaNova.Infrastructure -s src/AlturaNova.Infrastructure \
  -o Persistence/Migrations
```

A design-time factory (`DesignTimeDbContextFactory`) supplies the connection string via
`ALTURA_DB_CONNECTION` (defaults to the local dev database).

## API surface

Base URL: `http://localhost:8080`

### Auth
| Method | Route | Auth | Notes |
|--------|-------|------|-------|
| POST | `/api/auth/register/customer` | — | Returns tokens |
| POST | `/api/auth/register/vendor` | — | Creates a store + owner, returns tokens |
| POST | `/api/auth/login` | — | Returns tokens |
| POST | `/api/auth/refresh` | — | Rotates refresh token |
| POST | `/api/auth/logout` | ✔ | Revokes a refresh token |
| GET  | `/api/auth/me` | ✔ | Current user profile |

### Account (auth required)
| Method | Route | Notes |
|--------|-------|-------|
| PUT | `/api/account/profile` | Update first/last name + phone |
| POST | `/api/account/change-password` | Verifies current password |

### Catalog (public)
| Method | Route |
|--------|-------|
| GET | `/api/vendors` |
| GET | `/api/vendors/{id}` |
| GET | `/api/vendors/{id}/products` |
| GET | `/api/categories` |
| GET | `/api/products?search=&vendorId=&categoryId=&page=&pageSize=` |
| GET | `/api/products/{id}` |
| GET | `/api/products/handle/{handle}` |

### Cart (auth required)
| Method | Route |
|--------|-------|
| GET | `/api/cart` |
| POST | `/api/cart/items` |
| PATCH | `/api/cart/items/{itemId}` |
| DELETE | `/api/cart/items/{itemId}` |
| DELETE | `/api/cart` |

### Orders (auth required)
| Method | Route | Notes |
|--------|-------|-------|
| POST | `/api/orders/checkout` | Validates stock, snapshots items, decrements inventory, clears cart |
| GET | `/api/orders` | Caller's orders |
| GET | `/api/orders/{id}` | One of caller's orders |
| POST | `/api/orders/{id}/pay` | Starts Paystack hosted checkout → returns `authorizationUrl` + `reference` |
| POST | `/api/orders/{id}/verify` | Verifies the payment with Paystack; marks the order Paid on success |
| POST | `/api/orders/{id}/cancel` | Pending/Paid → Cancelled, restocks inventory |

### Webhooks (public, signature-verified)
| Method | Route | Notes |
|--------|-------|-------|
| POST | `/api/webhooks/paystack` | Validates `x-paystack-signature` (HMAC-SHA512), re-verifies the reference, marks the order Paid (idempotent) |

### Vendor console (role: Vendor)
| Method | Route | Notes |
|--------|-------|-------|
| GET | `/api/vendor/store` | Own store profile |
| PUT | `/api/vendor/store` | Update store name/description/logo/banner |
| GET | `/api/vendor/products` | Own products (incl. unpublished) |
| POST | `/api/vendor/products` | Create product + variants + images |
| GET | `/api/vendor/products/{id}` | Own product |
| PUT | `/api/vendor/products/{id}` | Update (variants reconciled by id) |
| PATCH | `/api/vendor/products/{id}/publish` | Publish/unpublish |
| DELETE | `/api/vendor/products/{id}` | Delete |
| GET | `/api/vendor/orders` | Orders containing the vendor's items (vendor-scoped view) |

### Admin (role: Admin)
| Method | Route | Notes |
|--------|-------|-------|
| GET | `/api/admin/orders` | All orders |
| PATCH | `/api/admin/orders/{id}/status` | Transition status (restocks on cancel) |

See `src/AlturaNova.Api/AlturaNova.Api.http` for ready-to-run example requests.

## Notes / follow-ups

- **Order lifecycle**: `Pending → Paid → Shipped → Delivered`, with `Cancelled` (restocks inventory) reachable
  from `Pending`/`Paid`. Customers pay/cancel their own orders; admins drive `Shipped`/`Delivered` via
  `/api/admin/orders/{id}/status`.
- **Payments (Paystack)**: `/api/orders/{id}/pay` initializes a Paystack transaction and returns a hosted
  `authorizationUrl`; the client opens it, then calls `/api/orders/{id}/verify` (and/or Paystack calls the
  webhook) to confirm. The secret key stays server-side. Set keys via env:
  `PAYSTACK_SECRET_KEY`, `PAYSTACK_PUBLIC_KEY`, `PAYSTACK_CURRENCY` (default `NGN`) — e.g.
  `PAYSTACK_SECRET_KEY=sk_test_xxx docker compose up -d --build`. Without keys, `/pay` returns a clean
  409 "Paystack is not configured". Amounts are charged as `order.Total × 100` in the configured currency
  (no FX conversion in v1 — price in the target currency for production).
- **Inventory concurrency**: product variants use PostgreSQL's `xmin` as an optimistic-concurrency token, so
  a race between two checkouts of the last unit surfaces as a retryable `409` instead of overselling.
- The **KMP mobile app now talks to this backend** (base URL `http://10.0.2.2:8080` on the Android emulator).
- `NU1903` (transitive `Microsoft.OpenApi 2.0.0` via `Microsoft.AspNetCore.OpenApi 10.0.10`) is suppressed
  in the API csproj pending an upstream bump — 3.x breaks the .NET 10 XML-comment source generator.
