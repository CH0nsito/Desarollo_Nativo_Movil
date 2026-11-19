plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Agregar esta línea y hacer Sync Now
    id("com.google.devtools.ksp") version "2.3.8" apply false
}