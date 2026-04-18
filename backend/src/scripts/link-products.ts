import { ExecArgs } from "@medusajs/framework/types";
import { ContainerRegistrationKeys } from "@medusajs/framework/utils";
import { linkProductsToSalesChannelWorkflow } from "@medusajs/medusa/core-flows";

export default async function linkProductsToChannel({ container }: ExecArgs) {
  const logger = container.resolve(ContainerRegistrationKeys.LOGGER);
  const query = container.resolve(ContainerRegistrationKeys.QUERY);

  logger.info("Linking products to sales channel...");

  const { data: products } = await query.graph({
    entity: "product",
    fields: ["id"],
  });

  const { data: salesChannels } = await query.graph({
    entity: "sales_channel",
    fields: ["id", "name"],
  });

  if (!products || products.length === 0) {
    logger.warn("No products found");
    return;
  }

  if (!salesChannels || salesChannels.length === 0) {
    logger.warn("No sales channels found");
    return;
  }

  const salesChannel = salesChannels[0];

  try {
    await linkProductsToSalesChannelWorkflow(container).run({
      input: {
        productIds: products.map(p => p.id),
        salesChannelId: salesChannel.id,
      },
    });
    logger.info(`Linked ${products.length} products to sales channel ${salesChannel.name}`);
  } catch (e: any) {
    logger.warn(`Error: ${e.message}`);
  }

  logger.info("Done.");
}