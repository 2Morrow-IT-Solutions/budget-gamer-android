import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    alias(libs.plugins.com.google.gms.google.services)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.com.google.firebase.crashlytics)
}
// region Reading "keys.properties"
val keysPropertiesFile = rootProject.file("keys.properties")
val keysProperties = Properties()
keysProperties.load(FileInputStream(keysPropertiesFile))
// endregion

// region Reading "keystore.properties"
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))
// endregion

android {
    namespace = "com.tomorrowit.budgetgamer"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.tomorrowit.budgetgamer"
        minSdk = 23
        targetSdk = 35
        versionCode = 13
        versionName = "1.0.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    buildTypes {
        debug {
            manifestPlaceholders += mapOf()
            manifestPlaceholders["applicationLabel"] = "@string/app_name_debug"
            applicationIdSuffix = ".debug"
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "FIREBASE_STORAGE_URL",
                keysProperties.getProperty("FIREBASE_STORAGE_URL_DEBUG")
            )
            buildConfigField("String", "API_URL", keysProperties.getProperty("API_URL_DEBUG"))
            buildConfigField(
                "String",
                "GOOGLE_SERVER_ID",
                keysProperties.getProperty("GOOGLE_SERVER_ID_DEBUG")
            )
        }
        register("staging") {
            manifestPlaceholders += mapOf()
            manifestPlaceholders["applicationLabel"] = "@string/app_name_staging"
            applicationIdSuffix = ".staging"
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "FIREBASE_STORAGE_URL",
                keysProperties.getProperty("FIREBASE_STORAGE_URL_STAGING")
            )
            buildConfigField("String", "API_URL", keysProperties.getProperty("API_URL_STAGING"))
            buildConfigField(
                "String",
                "GOOGLE_SERVER_ID",
                keysProperties.getProperty("GOOGLE_SERVER_ID_STAGING")
            )
        }
        release {

            manifestPlaceholders += mapOf()
            manifestPlaceholders["applicationLabel"] = "@string/app_name"
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "FIREBASE_STORAGE_URL",
                keysProperties.getProperty("FIREBASE_STORAGE_URL_RELEASE")
            )
            buildConfigField("String", "API_URL", keysProperties.getProperty("API_URL_RELEASE"))
            buildConfigField(
                "String",
                "GOOGLE_SERVER_ID",
                keysProperties.getProperty("GOOGLE_SERVER_ID_RELEASE")
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    //Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    //Retrofit for HTTP requests.
    implementation(libs.bundles.retrofit)

    //Coil
    implementation(libs.coil.android)

    //Standard libraries
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)

    //Lottie
    implementation(libs.lottie)

    //Google Play services library for Google Account authentication and identity.
    implementation(libs.play.services.auth)

    //Google Auth
    implementation(libs.bundles.google.auth)

    //Core ktx
    implementation(libs.core.ktx)

    //DaggerHilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.dagger.compiler)

    //DataStore
    implementation(libs.datastore)

    //Navigation
    implementation(libs.bundles.navigation)

    //Testing libraries
    implementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mokito.ktx.core)
    testImplementation(libs.mokito.core)
    testImplementation(libs.android.test.core)
}