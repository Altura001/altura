import { ExecArgs } from "@medusajs/framework/types";
import { ContainerRegistrationKeys, Modules, ProductStatus } from "@medusajs/framework/utils";
import { 
  createProductsWorkflow,
  createProductCategoriesWorkflow,
  createShippingProfilesWorkflow,
  createShippingOptionsWorkflow,
  linkProductsToSalesChannelWorkflow
} from "@medusajs/medusa/core-flows";

export default async function seedProducts({ container }: ExecArgs) {
  const logger = container.resolve(ContainerRegistrationKeys.LOGGER);
  const fulfillmentModuleService = container.resolve(Modules.FULFILLMENT);
  const query = container.resolve(ContainerRegistrationKeys.QUERY);

  logger.info("Seeding products...");

  const { data: products } = await query.graph({
    entity: "product",
    fields: ["id"],
    pagination: { limit: 1 }
  });

  if (products && products.length > 0) {
    logger.info(`Products already exist (${products.length}), skipping.`);
    return;
  }

  const shippingProfiles = await fulfillmentModuleService.listShippingProfiles({ type: "default" });
  const shippingProfile = shippingProfiles[0];

  if (!shippingProfile) {
    const { result: spResult } = await createShippingProfilesWorkflow(container).run({
      input: { data: [{ name: "Default Shipping Profile", type: "default" }] }
    });
    logger.info(`Created shipping profile: ${spResult[0].id}`);
  }

  const { result: categoryResult } = await createProductCategoriesWorkflow(container).run({
    input: {
      product_categories: [
        { name: "Shirts", is_active: true },
        { name: "Sweatshirts", is_active: true },
        { name: "Pants", is_active: true },
        { name: "Merch", is_active: true },
      ],
    },
  });

  const { result: productResult } = await createProductsWorkflow(container).run({
    input: {
      products: [
        {
          title: "Medusa T-Shirt",
          category_ids: [categoryResult.find(c => c.name === "Shirts")!.id],
          description: "Classic cotton t-shirt.",
          handle: "t-shirt",
          status: ProductStatus.PUBLISHED,
          shipping_profile_id: shippingProfile?.id,
          options: [{ title: "Size", values: ["S", "M", "L", "XL"] }],
          variants: [
            { title: "S", sku: "SHIRT-S", options: { Size: "S" }, prices: [{ amount: 1000, currency_code: "eur" }] },
            { title: "M", sku: "SHIRT-M", options: { Size: "M" }, prices: [{ amount: 1000, currency_code: "eur" }] },
            { title: "L", sku: "SHIRT-L", options: { Size: "L" }, prices: [{ amount: 1000, currency_code: "eur" }] },
            { title: "XL", sku: "SHIRT-XL", options: { Size: "XL" }, prices: [{ amount: 1000, currency_code: "eur" }] },
          ],
        },
        {
          title: "Medusa Sweatshirt",
          category_ids: [categoryResult.find(c => c.name === "Sweatshirts")!.id],
          description: "Classic sweatshirt.",
          handle: "sweatshirt",
          status: ProductStatus.PUBLISHED,
          shipping_profile_id: shippingProfile?.id,
          options: [{ title: "Size", values: ["S", "M", "L", "XL"] }],
          variants: [
            { title: "S", sku: "SWEAT-S", options: { Size: "S" }, prices: [{ amount: 2500, currency_code: "eur" }] },
            { title: "M", sku: "SWEAT-M", options: { Size: "M" }, prices: [{ amount: 2500, currency_code: "eur" }] },
            { title: "L", sku: "SWEAT-L", options: { Size: "L" }, prices: [{ amount: 2500, currency_code: "eur" }] },
            { title: "XL", sku: "SWEAT-XL", options: { Size: "XL" }, prices: [{ amount: 2500, currency_code: "eur" }] },
          ],
        },
        {
          title: "Medusa Sweatpants",
          category_ids: [categoryResult.find(c => c.name === "Pants")!.id],
          description: "Classic sweatpants.",
          handle: "sweatpants",
          status: ProductStatus.PUBLISHED,
          shipping_profile_id: shippingProfile?.id,
          options: [{ title: "Size", values: ["S", "M", "L", "XL"] }],
          variants: [
            { title: "S", sku: "PANT-S", options: { Size: "S" }, prices: [{ amount: 2000, currency_code: "eur" }] },
            { title: "M", sku: "PANT-M", options: { Size: "M" }, prices: [{ amount: 2000, currency_code: "eur" }] },
            { title: "L", sku: "PANT-L", options: { Size: "L" }, prices: [{ amount: 2000, currency_code: "eur" }] },
            { title: "XL", sku: "PANT-XL", options: { Size: "XL" }, prices: [{ amount: 2000, currency_code: "eur" }] },
          ],
        },
      ],
    },
  });

  logger.info(`Created ${productResult.length} products`);

  let linkedCount = 0;
  for (const p of productResult) {
    logger.info(`Created product: ${p.id} - ${p.title}`);
  }

  logger.info("Done. Please link products to sales channel via admin.");
}