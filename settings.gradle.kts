//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    versionCatalogs {

        val kotlin = "1.7.10"
        val fastAdapter = "5.4.1"

        create("tools") {
            version("kotlin", kotlin)
            version("gradle", "7.2.1")
            version("maven", "2.0")
        }

        create("app") {
            version("compileSdk", "32")
            version("minSdk", "21")
            version("targetSdk", "32")
        }

        create("libs") {
            // Kotlin
            library("kotlin", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin")
            // AndroidX
            library("androidx.core", "androidx.core:core-ktx:1.8.0")
            library("androidx.lifecycle", "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
            library("androidx.appcompat", "androidx.appcompat:appcompat:1.5.0")
            library(
                "androidx.coordinatorlayout",
                "androidx.coordinatorlayout:coordinatorlayout:1.2.0"
            )
            library("androidx.recyclerview", "androidx.recyclerview:recyclerview:1.2.1")
            // Google
            library("google.material", "com.google.android.material:material:1.7.0-rc01")
            // MFlisar
            library("mine.androidText", "com.github.MFlisar:AndroidText:1.2")
            library("mine.lumberjack", "com.github.MFlisar.Lumberjack:lumberjack-library:5.2.5")
            // Others
            library("fastadapter", "com.mikepenz:fastadapter:$fastAdapter")
            library("fastadapter.extensions", "com.mikepenz:fastadapter-extensions-ui:$fastAdapter")
            library("glide", "com.github.bumptech.glide:glide:4.13.2")
            library("glide.transformations", "jp.wasabeef:glide-transformations:4.3.0")
        }
    }
}

// --------------
// App
// --------------

include(":App")
project(":App").projectDir = file("app")

// --------------
// Core
// --------------

include(":MaterialDialogs:Core")
project(":MaterialDialogs:Core").projectDir = file("core")

// --------------
// Extensions
// --------------

include(":MaterialDialogs:Extensions:DialogFragment")
project(":MaterialDialogs:Extensions:DialogFragment").projectDir = file("extensions/dialogfragment")
include(":MaterialDialogs:Extensions:BottomSheetFragment")
project(":MaterialDialogs:Extensions:BottomSheetFragment").projectDir =
    file("extensions/bottomsheetfragment")
include(":MaterialDialogs:Extensions:FullscreenFragment")
project(":MaterialDialogs:Extensions:FullscreenFragment").projectDir =
    file("extensions/fullscreenfragment")
include(":MaterialDialogs:Extensions:Animations")
project(":MaterialDialogs:Extensions:Animations").projectDir = file("extensions/animations")

// --------------
// Dialogs
// --------------

include(":MaterialDialogs:Dialogs:Info")
project(":MaterialDialogs:Dialogs:Info").projectDir = file("dialogs/info")
include(":MaterialDialogs:Dialogs:Input")
project(":MaterialDialogs:Dialogs:Input").projectDir = file("dialogs/input")
include(":MaterialDialogs:Dialogs:List")
project(":MaterialDialogs:Dialogs:List").projectDir = file("dialogs/list")
include(":MaterialDialogs:Dialogs:Number")
project(":MaterialDialogs:Dialogs:Number").projectDir = file("dialogs/number")
include(":MaterialDialogs:Dialogs:DateTime")
project(":MaterialDialogs:Dialogs:DateTime").projectDir = file("dialogs/datetime")
include(":MaterialDialogs:Dialogs:Color")
project(":MaterialDialogs:Dialogs:Color").projectDir = file("dialogs/color")
include(":MaterialDialogs:Dialogs:Ads")
project(":MaterialDialogs:Dialogs:Ads").projectDir = file("dialogs/ads")
include(":MaterialDialogs:Dialogs:Frequency")
project(":MaterialDialogs:Dialogs:Frequency").projectDir = file("dialogs/frequency")
