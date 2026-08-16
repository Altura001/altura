# Fix LinkageError by reverting JVM Target to 17

The `LinkageError` is caused by a mismatch between the compiled class version (Java 26) and the runtime environment. We will revert all modules to use Java 17, which is a stable and supported version.

## Proposed Changes

### Build Configuration

#### [MODIFY] [gradle-daemon-jvm.properties](file:///Users/macbook/projects/altura/mobile/gradle/gradle-daemon-jvm.properties)
- Change `toolchainVersion` from `26` to `17`.

#### [MODIFY] [shared/build.gradle.kts](file:///Users/macbook/projects/altura/mobile/shared/build.gradle.kts)
- Revert `jvmTarget` to `JvmTarget.JVM_17`.

#### [MODIFY] [androidApp/build.gradle.kts](file:///Users/macbook/projects/altura/mobile/androidApp/build.gradle.kts)
- Revert `jvmTarget` to `JvmTarget.JVM_17`.
- Revert `sourceCompatibility` and `targetCompatibility` to `JavaVersion.VERSION_17`.

#### [MODIFY] [desktopApp/build.gradle.kts](file:///Users/macbook/projects/altura/mobile/desktopApp/build.gradle.kts)
- Add explicit `jvmTarget = JvmTarget.JVM_17` to ensure consistency.

## Verification Plan

### Automated Tests
- Run `./gradlew :desktopApp:assemble` to verify the build passes.
- Run `./gradlew :desktopApp:run` (if possible in this environment) to verify the app starts without `LinkageError`.
