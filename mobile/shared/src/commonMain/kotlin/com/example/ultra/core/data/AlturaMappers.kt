package com.example.ultra.core.data

import com.example.ultra.core.domain.model.Address
import com.example.ultra.core.domain.model.AuthAccountType
import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.CartItem
import com.example.ultra.core.domain.model.Order
import com.example.ultra.core.domain.model.OrderItem
import com.example.ultra.core.domain.model.PaymentInitiation
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.ProductVariant
import com.example.ultra.core.domain.model.User
import com.example.ultra.core.domain.model.Vendor

/** Maps Altura .NET backend DTOs to domain models. */

fun UserDto.toUser(): User = User(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    accountType = if (role.equals("Vendor", ignoreCase = true)) AuthAccountType.VENDOR else AuthAccountType.CUSTOMER,
    phone = phone
)

fun VendorDto.toVendor(): Vendor = Vendor(
    id = id,
    name = name,
    description = description,
    logoUrl = logoUrl,
    bannerUrl = bannerUrl
)

fun ProductVariantDto.toVariant(): ProductVariant = ProductVariant(
    id = id,
    title = title,
    sku = sku,
    price = price,
    currency = currency,
    inventoryQuantity = inventoryQuantity
)

fun ProductDto.toProduct(): Product = Product(
    id = id,
    vendorId = vendorId,
    name = name,
    description = description,
    price = price,
    currency = currency,
    imageUrl = thumbnailUrl ?: images.firstOrNull(),
    thumbnailUrl = thumbnailUrl ?: images.firstOrNull(),
    category = category,
    inStock = inStock,
    variants = variants.map { it.toVariant() },
    handle = handle
)

fun CartItemDto.toCartItem(): CartItem = CartItem(
    id = id,
    productId = productId,
    variantId = variantId,
    title = title,
    quantity = quantity,
    unitPrice = unitPrice,
    total = lineTotal,
    currency = currency,
    imageUrl = thumbnailUrl
)

fun CartDto.toCart(): Cart = Cart(
    id = id,
    items = items.map { it.toCartItem() },
    subtotal = subtotal,
    total = total,
    currency = currency
)

fun AddressDto.toAddress(): Address = Address(
    firstName = firstName,
    lastName = lastName,
    line1 = line1,
    line2 = line2,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    phone = phone
)

fun Address.toDto(): AddressDto = AddressDto(
    firstName = firstName,
    lastName = lastName,
    line1 = line1,
    line2 = line2,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    phone = phone
)

fun OrderItemDto.toOrderItem(): OrderItem = OrderItem(
    id = id,
    productId = productId,
    variantId = variantId,
    productName = productName,
    quantity = quantity,
    unitPrice = unitPrice,
    lineTotal = lineTotal,
    currency = currency,
    thumbnailUrl = thumbnailUrl
)

fun OrderDto.toOrder(): Order = Order(
    id = id,
    status = status,
    subtotal = subtotal,
    total = total,
    currency = currency,
    items = items.map { it.toOrderItem() },
    shippingAddress = shippingAddress?.toAddress(),
    createdAt = createdAt
)

fun PaymentInitiationDto.toModel(): PaymentInitiation = PaymentInitiation(
    orderId = orderId,
    provider = provider,
    authorizationUrl = authorizationUrl,
    reference = reference,
    publicKey = publicKey,
    amountSubunits = amountSubunits,
    currency = currency
)

fun WishlistItemResponseDto.toProduct(): Product = Product(
    id = productId,
    vendorId = "",
    name = productName,
    description = "",
    price = price,
    currency = currency,
    imageUrl = thumbnailUrl,
    thumbnailUrl = thumbnailUrl,
    inStock = inStock
)
