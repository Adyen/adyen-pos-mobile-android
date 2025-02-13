import com.android.build.api.variant.BuildConfigField
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val localPropsFile: String =
    System.getenv("LOCAL_PROPS")
        ?: (rootProject.rootDir.absolutePath + "/local.properties")
val localProperties = Properties()
    .apply {
        load(FileInputStream(localPropsFile))
    }

android {
    namespace = "com.adyen.sampleapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adyen.sampleapp"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "2.1"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

androidComponents {
    onVariants {
        it.apply {
            val environmentApiKey = localProperties.getProperty("environment.apiKey")
            buildConfigFields.put(
                "EnvironmentApiKey",
                BuildConfigField("String", "\"$environmentApiKey\"", "API Key"),
            )
            val environmentMerchantAccount =
                localProperties.getProperty("environment.merchantAccount")
            buildConfigFields.put(
                "EnvironmentMerchantAccount",
                BuildConfigField("String", "\"$environmentMerchantAccount\"", "Merchant Account"),
            )
        }
    }
}

dependencies {

    debugImplementation(libs.pos.mobile.debug)
    // To build with the release dependencies, you need use the LIVE repository in settings.gradle
    releaseImplementation(libs.pos.mobile.release)

    implementation(libs.androidx.corek.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.google.material)

    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.okhttp.logging.interceptor)
    implementation(libs.squareup.logcat)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
