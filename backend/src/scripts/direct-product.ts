import { ExecArgs } from "@medusajs/framework/types";
import { ContainerRegistrationKeys, Modules, ProductStatus } from "@medusajs/framework/utils";

export default async function createProductDirect({ container }: ExecArgs) {
  const logger = container.resolve(ContainerRegistrationKeys.LOGGER);
  const productModule = container.resolve(Modules.PRODUCT);

  logger.info("Checking for existing products...");

  const existing = await productModule.listProducts({}, { take: 1 });
  
  if (existing.length > 0) {
    logger.info(`Found ${existing.length} products`);
    for (const p of existing) {
      logger.info(`  - ${p.id}: ${p.title}`);
      logger.info(`    status: ${p.status}`);
      logger.info(`    handle: ${p.handle}`);
    }
    return;
  }

  logger.info("Creating a test product directly...");

  try {
    const product = await productModule.createProducts({
      title: "Test Product",
      status: ProductStatus.PUBLISHED,
      handle: "test-product",
      description: "A test product",
      options: [
        { title: "Size", values: ["S", "M", "L"] }
      ],
      variants: [
        { title: "S", sku: "TEST-S", prices: [{ amount: 1000, currency_code: "eur" }], options: { Size: "S" } },
        { title: "M", sku: "TEST-M", prices: [{ amount: 1000, currency_code: "eur" }], options: { Size: "M" } },
        { title: "L", sku: "TEST-L", prices: [{ amount: 1000, currency_code: "eur" }], options: { Size: "L" } },
      ]
    });

    logger.info(`Created product: ${product.id}`);
    logger.info(`Title: ${product.title}`);
    logger.info(`Status: ${product.status}`);
  } catch (e: any) {
    logger.error(`Error: ${e.message}`);
    logger.error(e.stack);
  }

  logger.info("Done.");
}