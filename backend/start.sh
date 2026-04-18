#!/bin/sh

# Run migrations non-interactively
echo "Running database migrations..."
echo "" | npx medusa db:migrate || true

echo "Starting Medusa development server..."
npm run dev
