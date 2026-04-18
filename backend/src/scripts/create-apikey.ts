import { ExecArgs } from "@medusajs/framework/types";
import { ContainerRegistrationKeys } from "@medusajs/framework/utils";

export default async function createApiKey({ container }: ExecArgs) {
  const logger = container.resolve(ContainerRegistrationKeys.LOGGER);
  const query = container.resolve(ContainerRegistrationKeys.QUERY);

  logger.info("Listing all API keys...");

  const { data: allKeys } = await query.graph({
    entity: "api_key",
    fields: ["id", "title", "type", "created_at"],
  });

  if (allKeys && allKeys.length > 0) {
    for (const key of allKeys) {
      logger.info(`Key: ${key.id} (${key.type}) - ${key.title} - created: ${key.created_at}`);
    }
  } else {
    logger.info("No API keys found");
  }

  logger.info("Listing sales channels...");

  const { data: salesChannels } = await query.graph({
    entity: "sales_channel",
    fields: ["id", "name"],
  });

  if (salesChannels && salesChannels.length > 0) {
    for (const sc of salesChannels) {
      logger.info(`Sales Channel: ${sc.id} - ${sc.name}`);
    }
  }

  logger.info("Done.");
}