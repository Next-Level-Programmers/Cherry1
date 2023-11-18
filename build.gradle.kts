// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}
buildscript {
    repositories {
        google() // Google's Maven repository
        // other repositories
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4") // Latest version
        // other dependencies
    }
}

true // Needed to make the Suppress annotation work for the plugins block