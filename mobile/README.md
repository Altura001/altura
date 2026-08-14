This is a Kotlin Multiplatform project targeting Android, iOS, Desktop (JVM), and Web.

It uses the [new KMP default project structure](https://blog.jetbrains.com/kotlin/2026/05/new-kmp-default-structure/):
shared UI/business logic lives in a `shared` KMP library, with thin per-platform
application modules on top.

* [/shared](./shared/src) is a KMP library with all shared code (models, repositories, ViewModels, shared Compose UI).
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that's common for all targets.
  - [androidMain](./shared/src/androidMain/kotlin), [iosMain](./shared/src/iosMain/kotlin),
    [jvmMain](./shared/src/jvmMain/kotlin), [jsMain](./shared/src/jsMain/kotlin),
    [wasmJsMain](./shared/src/wasmJsMain/kotlin) contain target-specific `expect`/`actual` code.

* [/androidApp](./androidApp) is the Android application entry point (`MainActivity`, manifest, app resources).

* [/desktopApp](./desktopApp) is the Desktop (JVM) application entry point, launched with Compose Hot Reload.

* [/webApp](./webApp) is the Web application entry point (Kotlin/JS and Kotlin/Wasm).

* [/iosApp](./iosApp/iosApp) contains the iOS application. It consumes the `Shared` framework built from `:shared`.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE's toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :androidApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :androidApp:assembleDebug
  ```

### Build and Run Desktop Application (with Hot Reload)

Compose Hot Reload is bundled and enabled by default. Run it from the terminal or the IDE gutter:

```shell
./gradlew :desktopApp:hotRun --auto
```

Use `--auto` (or `--autoReload`) for file-watch auto-reload; omit it for manual reload (Reload UI button or `./gradlew reload`).

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :webApp:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :webApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :webApp:jsBrowserDevelopmentRun
    ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE's toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…
