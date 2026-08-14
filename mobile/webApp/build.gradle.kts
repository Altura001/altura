import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
}

kotlin {
	js {
		browser {
			commonWebpackConfig {
				outputFileName = "webApp.js"
			}
		}
		binaries.executable()
	}

	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		browser {
			commonWebpackConfig {
				outputFileName = "webApp.js"
			}
		}
		binaries.executable()
	}

	sourceSets {
		jsMain.dependencies {
			implementation(project(":shared"))
			implementation(libs.compose.runtime)
			implementation(libs.compose.foundation)
			implementation(libs.compose.material3)
			implementation(libs.compose.ui)
		}
		wasmJsMain.dependencies {
			implementation(project(":shared"))
			implementation(libs.compose.runtime)
			implementation(libs.compose.foundation)
			implementation(libs.compose.material3)
			implementation(libs.compose.ui)
		}
	}
}
