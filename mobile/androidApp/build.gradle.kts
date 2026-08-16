import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.androidApplication)
//	id("org.jetbrains.kotlin.android")
//	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_26
	}
}

dependencies {
	implementation(project(":shared"))

	implementation(libs.androidx.activity.compose)

	implementation(libs.compose.uiTooling.preview)
	debugImplementation(libs.compose.uiTooling)
}

configure<ApplicationExtension> {
	namespace = "com.example.ultra"
	compileSdk = libs.versions.android.compileSdk.get().toInt()

	defaultConfig {
		applicationId = "com.example.ultra"
		minSdk = libs.versions.android.minSdk.get().toInt()
		targetSdk = libs.versions.android.targetSdk.get().toInt()
		versionCode = 1
		versionName = "1.0"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
		}
	}
	buildFeatures {
		compose = true
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_26
		targetCompatibility = JavaVersion.VERSION_26
	}

}
