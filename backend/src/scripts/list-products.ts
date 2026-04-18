import { ExecArgs } from "@medusajs/framework/types";
import { DbTypes } from "@medusajs/framework";

export default async function listProducts({ container }: ExecArgs) {
  const logger = container.resolve("logger");
  const db = container.resolve("database") as DbTypes;

  logger.info("Listing all products via DB...");

  try {
    const em = db.withGlobalPrefix("product");
    const products = await em.find("product", {}, { take: 20 });
    
    logger.info(`Found ${products.length} products`);
    for (const p of products as any[]) {
      logger.info(`  - ${p.id}: ${p.title}`);
    }
  } catch (e: any) {
    logger.error(`Error: ${e.message}`);
  }

  logger.info("Done.");
}