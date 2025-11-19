// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    id("com.google.gms.google-services") version "4.4.4" apply false

    //plugin adding api key as compiling variables
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1"

    kotlin("plugin.serialization") version "2.2.21" apply false // Use a recent version
}