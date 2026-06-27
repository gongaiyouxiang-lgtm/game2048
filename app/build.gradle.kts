import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.hilt.android)
}

// AdMob official test ad units (always used by debug builds to stay policy-safe).
val testAdMobAppId = "ca-app-pub-3940256099942544~3347511713"
val testBannerAdUnitId = "ca-app-pub-3940256099942544/6300978111"
val testInterstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712"

// Real ad IDs come from local.properties (gitignored); fall back to test IDs if absent.
val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
fun adMobProp(key: String, fallback: String): String =
    localProps.getProperty(key)?.takeIf { it.isNotBlank() } ?: fallback

// Release signing config from gitignored keystore.properties (absent on CI/other machines).
val keystoreProps = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val hasReleaseKeystore = keystoreProps.getProperty("storeFile")
    ?.let { rootProject.file(it).exists() } ?: false

android {
    namespace = "com.codebythura.fruit2048"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.pp.game2048"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Debug + default: always test IDs so development never serves real ads.
        manifestPlaceholders["admobAppId"] = testAdMobAppId
        buildConfigField("String", "BANNER_AD_UNIT_ID", "\"$testBannerAdUnitId\"")
        buildConfigField("String", "INTERSTITIAL_AD_UNIT_ID", "\"$testInterstitialAdUnitId\"")
    }

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            if (hasReleaseKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Release uses the real AdMob IDs from local.properties (test IDs if unset).
            manifestPlaceholders["admobAppId"] = adMobProp("admob.appId", testAdMobAppId)
            buildConfigField(
                "String",
                "BANNER_AD_UNIT_ID",
                "\"${adMobProp("admob.bannerId", testBannerAdUnitId)}\""
            )
            buildConfigField(
                "String",
                "INTERSTITIAL_AD_UNIT_ID",
                "\"${adMobProp("admob.interstitialId", testInterstitialAdUnitId)}\""
            )
        }
    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
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
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.datastore)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.play.services.ads)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.32.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin")
            }
        }
    }
}
