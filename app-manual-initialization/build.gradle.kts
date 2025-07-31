import com.android.build.api.variant.BuildConfigField
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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
    compileSdk = 36

    defaultConfig {
        applicationId = "com.adyen.sampleapp"
        minSdk = 30
        targetSdk = 36
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
        apiVersion = KotlinVersion.KOTLIN_2_2
    }
}

androidComponents {
    onVariants { variant ->
        val environmentApiKey = localProperties.getProperty("environment.apiKey")
        val environmentMerchantAccount = localProperties.getProperty("environment.merchantAccount")
        variant.buildConfigFields?.run {
            put(
                "EnvironmentApiKey",
                BuildConfigField("String", "\"$environmentApiKey\"", "API Key"),
            )
            put(
                "EnvironmentMerchantAccount",
                BuildConfigField("String", "\"$environmentMerchantAccount\"", "Merchant Account"),
            )
        }
    }
}

dependencies {
    // Be aware that importing additional modules will increase the size of your application.
    // To optimize your app's size and build times, only include the specific payment features you require.
    debugImplementation(libs.pos.mobile.debug)
    debugImplementation(libs.payment.tap.to.pay.debug)
    debugImplementation(libs.payment.card.reader.debug)

    // To build with the release dependencies, you need use the LIVE repository in `settings.gradle`.
    releaseImplementation(libs.pos.mobile.release)
    releaseImplementation(libs.payment.tap.to.pay.release)
    releaseImplementation(libs.payment.card.reader.release)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.corek.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.google.material)
    implementation(libs.squareup.logcat)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.okhttp.logging.interceptor)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
