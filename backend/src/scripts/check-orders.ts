import { ExecArgs } from "@medusajs/framework/types";
import { ContainerRegistrationKeys } from "@medusajs/framework/utils";

export default async function checkOrder({ container }: ExecArgs) {
  const logger = container.resolve(ContainerRegistrationKeys.LOGGER);
  const query = container.resolve(ContainerRegistrationKeys.QUERY);

  logger.info("Checking orders via query...");

  try {
    const { data: orders } = await query.graph({
      entity: "order",
      fields: ["id", "display_id", "email", "total"],
      pagination: { limit: 10 }
    });
    logger.info(`Found ${orders?.length || 0} orders`);
  } catch (e: any) {
    logger.error(`Error: ${e.message}`);
  }

  logger.info("Done.");
}
