import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
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

// Kotlin/Native can only build iOS binaries on macOS. Declaring the iOS targets on
// Windows would force a slow Kotlin/Native toolchain download + native commonization on
// every Android build, so we gate them behind the host OS. Codemagic (macOS) configures
// and compiles the iOS targets; Windows builds/runs Android only.
val isMacOs = System.getProperty("os.name").lowercase().let { it.contains("mac") || it.contains("os x") }

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    if (isMacOs) {
        // Note: iosX64 (Intel-Mac simulator) is intentionally omitted — Compose Multiplatform
        // 1.11 no longer ships iosX64 artifacts. iosArm64 = devices, iosSimulatorArm64 = the
        // Apple-Silicon simulator, which is all Codemagic (M-series) and real devices need.
        listOf(
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "ComposeApp"
                // Dynamic (not static): a static framework can leave Compose's own render
                // assets unbundled, crashing Skia/Metal at first draw on iOS. embedAndSign
                // copies + signs the dynamic framework into the app's Frameworks dir.
                isStatic = false
            }
        }
    }

    sourceSets {
        androidMain.dependencies {
            // Compose Multiplatform (android artifacts resolve for the android target)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Android platform-only
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.play.services.ads)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.datastore.core.okio)
            implementation(libs.okio)
        }
    }
}

android {
    namespace = "com.codebythura.fruit2048"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pp.game2048"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// Generate the typed `Res` accessor class in a stable, explicit package.
compose.resources {
    publicResClass = true
    packageOfResClass = "com.codebythura.fruit2048.resources"
}
