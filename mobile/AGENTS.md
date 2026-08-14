# Ultra E-Commerce App

## Project Overview
Multi-vendor e-commerce app built with Compose Multiplatform. Users can browse vendors and products, add items to cart, and checkout.

## Tech Stack
- **UI**: Compose Multiplatform (Android, iOS, Desktop, Web)
- **DI**: Koin 4.0
- **Networking**: Ktor
- **Serialization**: Kotlinx Serialization
- **Async**: Kotlinx Coroutines
- **Backend**: NodeJS-Medusa (abstracted with mock data)

## Architecture

### Module Structure
The project uses the new KMP default structure (AGP 9 requires the Android app entry point in its own module):
```
shared/       # KMP library: commonMain + androidMain/iosMain/jvmMain/jsMain/wasmJsMain (all feature code)
androidApp/   # Android application entry point (MainActivity, manifest, app res)
desktopApp/   # Desktop (JVM) application entry point (main(), compose.desktop config)
webApp/       # Web entry point (js + wasmJs main() and resources)
iosApp/       # Xcode project consuming the `Shared` framework from :shared
```
All shared feature code lives under `shared/src/commonMain/kotlin/com/example/ultra/`.
Only entry points and platform actuals live in `shared/src/{target}Main`. App-level
entry points are in `androidApp`/`desktopApp`/`webApp`. Desktop hot reload:
`./gradlew :desktopApp:hotRun --auto`.

### Feature Package Structure
Each feature is a standalone package directly under `com.example.ultra/`:
```
com.example.ultra/
├── app/                    # App entry point, routes
├── core/                   # Shared code
│   ├── data/              # HttpClientFactory
│   ├── domain/
│   │   ├── model/        # User, Vendor, Product, Cart, CartItem
│   │   └── repository/    # Repository interfaces
│   └── presentation/
│       └── theme/        # Colors, Typography, Theme
├── di/                     # Koin DI modules
├── auth/                   # Authentication feature
│   ├── data/
│   │   └── repository/   # DefaultAuthRepository
│   ├── domain/
│   │   └── usecase/     # LoginUseCase, LogoutUseCase, etc.
│   └── presentation/
│       ├── auth/screen/  # AuthScreen, AuthScreenRoot
│       ├── intent/       # AuthIntent, AuthState
│       └── viewmodel/    # AuthViewModel
├── catalog/                # Product catalog feature
│   ├── data/
│   │   └── repository/   # DefaultCatalogRepository
│   ├── domain/
│   │   └── usecase/     # GetVendorsUseCase, GetProductsUseCase
│   └── presentation/
│       ├── catalog/screen/  # CatalogScreen, CatalogScreenRoot
│       ├── intent/         # CatalogIntent, CatalogState
│       └── viewmodel/      # CatalogViewModel
├── cart/                   # Shopping cart feature
│   ├── data/
│   │   └── repository/   # DefaultCartRepository
│   ├── domain/
│   │   └── usecase/     # GetCartUseCase, AddToCartUseCase, etc.
│   └── presentation/
│       ├── cart/screen/    # CartScreen, CartScreenRoot
│       ├── intent/         # CartIntent, CartState
│       └── viewmodel/      # CartViewModel
├── navigation/             # Navigation feature
│   └── presentation/     # MainScreen, BottomNavItem
└── profile/               # User profile feature
    └── presentation/
        ├── profile/screen/  # ProfileScreen, ProfileScreenRoot
        ├── intent/          # ProfileIntent, ProfileState
        └── viewmodel/      # ProfileViewModel
```

### Dependency Rule
- Presentation and Data layers CAN access Domain
- Domain layer CANNOT access Presentation or Data (pure Kotlin stdlib only)

### MVI Pattern
```kotlin
// Intent - user actions (sealed interface)
sealed interface FeatureIntent {
    data class DoSomething(val id: String) : FeatureIntent
    data object Refresh : FeatureIntent
}

// State - immutable UI state (data class)
data class FeatureState(
    val isLoading: Boolean = false,
    val items: List<Item> = emptyList(),
    val error: String? = null
)

// ViewModel - holds state and processes intents
class FeatureViewModel(
    private val getItems: GetItemsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(FeatureState())
    val state: StateFlow<FeatureState> = _state.asStateFlow()

    fun onAction(action: FeatureIntent) {
        when (action) {
            is FeatureIntent.DoSomething -> doSomething(action.id)
            is FeatureIntent.Refresh -> refresh()
        }
    }
}
```

### Koin DI Pattern
```kotlin
// ScreenRoot - Entry point with Koin access
@Composable
fun FeatureScreenRoot(
    viewModel: FeatureViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    FeatureScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

// Screen - Pure composable - no Koin dependency
@Composable
fun FeatureScreen(
    state: FeatureState,
    onAction: (FeatureIntent) -> Unit
) {
    // UI implementation
}
```

## Features

### 1. Core
- Domain models: User, Vendor, Product, Cart, CartItem
- HTTP client factory with Ktor
- Repository interfaces
- App theme (colors, typography)
- Core DI module

### 2. Auth
- Login with email/password
- Social login: Google, Apple
- Auth state persistence
- Logout
- Auth DI module

### 3. Navigation
- Bottom navigation with 3 tabs: Catalog, Cart, Profile
- Main screen with NavHost

### 4. Catalog
- Browse vendors
- Browse products by vendor
- Catalog DI module

### 5. Cart
- View cart items
- Add to cart
- Update item quantity
- Remove from cart
- Cart summary
- Server-side persistence (requires auth)
- Cart DI module

### 6. Profile
- User info display
- Logout
- Profile DI module

## Navigation Flow
- App starts on Catalog screen (anonymous browsing allowed)
- Cart tab shows LoginPrompt if not authenticated
- Profile tab shows LoginScreen if not authenticated
- After login, user returns to previous screen

## API Abstraction
All data layer implementations use mock data to simulate the Medusa backend.
