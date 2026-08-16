import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.kotlinJvm)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
}

dependencies {
	implementation(project(":shared"))

	implementation(compose.desktop.currentOs)
	implementation(libs.kotlinx.coroutinesSwing)

	implementation(libs.compose.uiTooling.preview)
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

compose.desktop {
	application {
		mainClass = "com.example.ultra.MainKt"

		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
			packageName = "com.example.ultra"
			packageVersion = "1.0.0"
		}
	}
}