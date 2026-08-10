import {
  createApiKeysWorkflow,
  linkSalesChannelsToApiKeyWorkflow,
} from "@medusajs/medusa/core-flows"
import { ExecArgs } from "@medusajs/framework/types"
import { ContainerRegistrationKeys } from "@medusajs/framework/utils"

export default async function seedPublishableKey({ container }: ExecArgs) {
  const logger = container.resolve(ContainerRegistrationKeys.LOGGER)
  const query = container.resolve(ContainerRegistrationKeys.QUERY)

  const {
    data: [salesChannel],
  } = await query.graph({
    entity: "sales_channel",
    fields: ["id", "name"],
    filters: {
      name: "Default Sales Channel",
    },
  })

  if (!salesChannel) {
    throw new Error(
      'Default Sales Channel not found. Run the full seed script first: `npm run seed`.'
    )
  }

  const {
    data: [existingKey],
  } = await query.graph({
    entity: "api_key",
    fields: ["id", "title", "token", "type"],
    filters: {
      title: "Webshop",
      type: "publishable",
    },
  })

  if (existingKey?.token) {
    logger.info(`Publishable key already exists: ${existingKey.token}`)
    return
  }

  const { result } = await createApiKeysWorkflow(container).run({
    input: {
      api_keys: [
        {
          title: "Webshop",
          type: "publishable",
          created_by: "",
        },
      ],
    },
  })

  const publishableApiKey = result[0]

  await linkSalesChannelsToApiKeyWorkflow(container).run({
    input: {
      id: publishableApiKey.id,
      add: [salesChannel.id],
    },
  })

  const {
    data: [createdKey],
  } = await query.graph({
    entity: "api_key",
    fields: ["token"],
    filters: {
      id: publishableApiKey.id,
    },
  })

  logger.info(`Created publishable key: ${createdKey?.token ?? "<missing-token>"}`)
}
