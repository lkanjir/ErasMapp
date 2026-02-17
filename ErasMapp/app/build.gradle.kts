import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
    kotlin("plugin.serialization")
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {

    namespace = "com.rampu.erasmapp"
    compileSdk {
        version = release(36)
    }

    val props = Properties().apply {
        val f = rootProject.file("keystores/dev.properties")
        if (f.exists()) f.inputStream().use(::load)
    }

    signingConfigs {
        create("dev") {
            val storePath = props.getProperty("storeFile")
                ?: "$rootDir/keystores/dev.keystore"
            storeFile = file(storePath)
            storePassword = props.getProperty("storePassword") ?: "rampu_dev"
            keyAlias = props.getProperty("keyAlias") ?: "dev"
            keyPassword = props.getProperty("keyPassword") ?: "rampu_dev"
        }
    }


    defaultConfig {
        applicationId = "com.rampu.erasmapp"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = localProperties.getProperty("GOOGLE_MAPS_API_KEY")
    }

    buildTypes {

        getByName("debug") {
            signingConfig = signingConfigs.getByName("dev")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation.layout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:4.1.0"))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.koin.androidx.compose)
    implementation(libs.compose)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation (libs.compose.material.icons)
    implementation(libs.places)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
