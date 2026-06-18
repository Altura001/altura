# Copilot Instructions for `altura_nova`

## Build, test, and lint commands

Run commands from the package directory they belong to.

### Backend (`backend/`, Medusa v2)

- Install deps: `yarn`
- Dev server: `yarn dev`
- Build: `yarn build`
- Seed data: `yarn seed`
- Tests:
  - All unit tests: `yarn test:unit`
  - Single unit test file: `yarn test:unit -- src/**/__tests__/**/your-test.unit.spec.ts`
  - All HTTP integration tests: `yarn test:integration:http`
  - Single HTTP integration test file: `yarn test:integration:http -- integration-tests/http/your-test.spec.ts`
  - All module integration tests: `yarn test:integration:modules`
  - Single module integration test file: `yarn test:integration:modules -- src/modules/*/__tests__/**/your-test.spec.ts`

### Web storefront (`web/`, Next.js 15)

- Install deps: `yarn`
- Dev server (port 8000): `yarn dev`
- Build: `yarn build`
- Start production server (port 8000): `yarn start`
- Lint: `yarn lint`

### Mobile (`mobile/`, Kotlin Multiplatform + Compose)

- Build Android debug app: `./gradlew :composeApp:assembleDebug`
- Build all + run tests: `./gradlew :composeApp:build`
- Run all tests: `./gradlew :composeApp:allTests`
- Run one JVM unit test: `./gradlew :composeApp:testDebugUnitTest --tests "com.example.ultra.YourTestClass.yourTestMethod"`
- Lint: `./gradlew :composeApp:lint`
- Web dev targets:
  - WASM: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`
  - JS: `./gradlew :composeApp:jsBrowserDevelopmentRun`

## High-level architecture

This repo is a multi-client commerce setup around a Medusa backend:

1. `backend/`: Medusa server (plugins + workflows/tests/seeding)
2. `web/`: Next.js storefront consuming Medusa Store APIs
3. `mobile/`: Compose Multiplatform client consuming Medusa Store APIs

Request flow is region-aware on web:

- `web/src/middleware.ts` ensures routes are country-prefixed (`/[countryCode]/...`) and resolves region/country from Medusa regions.
- Server-side data/actions in `web/src/lib/data/*` call Medusa via `sdk` from `web/src/lib/config.ts`.
- Cart/auth/cache state is cookie-driven (`_medusa_cart_id`, `_medusa_jwt`, `_medusa_cache_id`) via `web/src/lib/data/cookies.ts`.

Backend is currently mostly Medusa extension scaffolding plus seed scripts:

- Extension points live under `backend/src/{api,workflows,modules,subscribers,jobs,admin,links,scripts}`.
- `backend/medusa-config.ts` wires env-driven project config and `@techlabi/medusa-marketplace-plugin`.
- `backend/src/scripts/seed.ts` / `seed-demo.ts` seed regions, channels, stock locations, shipping, products, and API key links using Medusa core workflows.

Mobile follows clean feature boundaries under `com.example.ultra.*` in `mobile/composeApp/src/commonMain/kotlin`:

- Feature packages (`auth`, `catalog`, `cart`, `profile`, `navigation`) each split into `data` / `domain` / `presentation`.
- DI is centralized in `di/Modules.kt` using Koin.
- API integration is in `core/data/MedusaApiService.kt` and `HttpClientFactory.kt`.

## Key conventions in this codebase

### Web conventions

- Keep Medusa calls in `src/lib/data/*` server files (`"use server"`), not in UI components.
- After cart/customer/order writes, revalidate cache tags via `getCacheTag(...)` + `revalidateTag(...)` (see `src/lib/data/cart.ts`, `customer.ts`).
- Keep country-aware routing intact: user-facing pages belong under `src/app/[countryCode]/...`.
- Use TS path aliases from `web/tsconfig.json`: `@lib/*`, `@modules/*`, `@pages/*`.
- This repo intentionally hardcodes `pk_test_dummy` in `web/src/lib/config.ts` and `web/src/middleware.ts`; do not switch this flow without updating both places and env expectations.

### Backend conventions

- Prefer Medusa-native extension points over ad-hoc service wiring: file-based API routes (`route.ts`), workflows, subscribers, jobs, and module definitions under `src/`.
- Tests are filtered by `TEST_TYPE` in `backend/jest.config.js`; place new tests in the expected folder patterns so scripts pick them up.

### Mobile conventions

- Follow MVI contracts per feature: `*Contract.kt` defines `State` + `Intent`, ViewModels expose `StateFlow` and a single `onAction(...)` dispatcher.
- Keep dependency direction strict: Domain is pure and does not depend on Data/Presentation.
- Use Koin `ScreenRoot` + pure `Screen` split (DI access in root composable; presentational composables remain DI-free).
- Base URLs and publishable key are centralized in `AppConfig` (Android emulator uses `10.0.2.2`, iOS uses `localhost`).

