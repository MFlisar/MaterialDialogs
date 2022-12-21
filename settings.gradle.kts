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

        // TOML Files
        create("androidx") {
            from(files("gradle/androidx.versions.toml"))
        }
        create("deps") {
            from(files("gradle/dependencies.versions.toml"))
        }

        // Rest
        val kotlin = "1.7.20"
        create("tools") {
            version("kotlin", kotlin)
            version("gradle", "7.2.2")
            version("maven", "2.0")
        }
        create("app") {
            version("compileSdk", "32")
            version("minSdk", "21")
            version("targetSdk", "32")
        }
        create("libs") {
            library("kotlin", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin")
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
include(":MaterialDialogs:Dialogs:Debug")
project(":MaterialDialogs:Dialogs:Debug").projectDir = file("dialogs/debug")
include(":MaterialDialogs:Dialogs:Progress")
project(":MaterialDialogs:Dialogs:Progress").projectDir = file("dialogs/progress")
include(":MaterialDialogs:Dialogs:GDPR")
project(":MaterialDialogs:Dialogs:GDPR").projectDir = file("dialogs/gdpr")
include(":MaterialDialogs:Dialogs:Billing")
project(":MaterialDialogs:Dialogs:Billing").projectDir = file("dialogs/billing")

// Started template for a new dialog...
include(":MaterialDialogs:Dialogs:Template")
project(":MaterialDialogs:Dialogs:Template").projectDir = file("dialogs/template")
