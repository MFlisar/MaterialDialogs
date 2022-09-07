plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {

    compileSdk = app.versions.compileSdk.get().toInt()

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
        targetSdk = app.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    // ------------------------
    // AndroidX / Google
    // ------------------------

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.google.material)

    // ------------------------
    // Core
    // ------------------------

    implementation(project(":MaterialDialogs:Core"))

    // -------------------------
    // Extensions for animations and different dialog types
    // -------------------------

    implementation(project(":MaterialDialogs:Extensions:Animations"))
    implementation(project(":MaterialDialogs:Extensions:DialogFragment"))
    implementation(project(":MaterialDialogs:Extensions:BottomSheetFragment"))
    implementation(project(":MaterialDialogs:Extensions:FullscreenFragment"))

    // -------------------------
    // following are optional (but you at least want one of them for sure)
    // -------------------------

    implementation(project(":MaterialDialogs:Dialogs:Info"))
    implementation(project(":MaterialDialogs:Dialogs:Input"))
    implementation(project(":MaterialDialogs:Dialogs:List"))
    implementation(project(":MaterialDialogs:Dialogs:Number"))
    implementation(project(":MaterialDialogs:Dialogs:DateTime"))
    implementation(project(":MaterialDialogs:Dialogs:Color"))
    //implementation(project(":MaterialDialogs:Dialogs:Ads"))
    //implementation(project(":MaterialDialogs:Dialogs:Frequency"))

    // ------------------------
    // Others
    // ------------------------

    implementation(libs.mine.lumberjack)
    implementation(libs.fastadapter)
    implementation(libs.fastadapter.extensions)
    implementation(libs.glide)
    implementation(libs.glide.transformations)
}
