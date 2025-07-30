import com.android.build.api.variant.BuildConfigField
import java.io.FileInputStream
import java.util.Properties
import kotlin.apply

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
    compileSdk = 35

    defaultConfig {
        applicationId = "com.adyen.sampleapp"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    dynamicFeatures += setOf(":dynamic_sdk")
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
    api(libs.feature.delivery)

    implementation(libs.pos.mobile.dynamic.base)

    implementation(libs.androidx.corek.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.espresso.idling.resource)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
}

// ===================================================================
//
//      Gradle Task for Testing Dynamic Feature App Installation
//
// ===================================================================

val bundletoolVersion = "1.18.1"
val bundletoolPath = rootProject.file("tools/bundletool/bundletool-all-$bundletoolVersion.jar")
val dynamicAppId = "com.adyen.sampleapp"
val buildType = "debug"

tasks.register("uninstallApp", Exec::class) {
    description = "Helper Tasks: Uninstalls '$dynamicAppId' from the connected device."
    commandLine(android.adbExecutable, "shell", "pm", "uninstall", "--user", "0", dynamicAppId)
    isIgnoreExitValue = true
}

tasks.register("buildDebugApks", Exec::class) {
    description = "Builds a local testing .apks file from the $buildType .aab."
    dependsOn(":app-dynamic:bundleDebug")

    val aabFile =
        rootProject.file("${project.name}/build/outputs/bundle/$buildType/app-dynamic-$buildType.aab")
    val apksFile =
        rootProject.file("${project.name}/build/outputs/apks/$buildType/app-dynamic-$buildType.apks")

    doFirst {
        apksFile.parentFile?.mkdirs()
    }

    commandLine(
        "java", "-jar", bundletoolPath,
        "build-apks",
        "--bundle", aabFile.toString(),
        "--output", apksFile.toString(),
        "--local-testing"
    )
}

tasks.register("installDebugApks", Exec::class) {
    description = "Installs the $buildType .apks file on the connected device."
    dependsOn(tasks.named("buildDebugApks"))

    val apksFile =
        rootProject.file("${project.name}/build/outputs/apks/$buildType/app-dynamic-$buildType.apks")
    inputs.file(apksFile)

    commandLine(
        "java", "-jar", bundletoolPath,
        "install-apks",
        "--apks", apksFile.toString()
    )
}


tasks.register("installDynamicDebugApp") {
    group = "Dynamic App Install"
    description = "Uninstalls, cleans, builds / installs the a debug app."
    dependsOn(tasks.named("uninstallApp"))
    dependsOn(tasks.named("installDebugApks"))
}

tasks.register("runSmokeTest", Exec::class) {
    group = "Verification"
    description = "Installs the dynamic app and test APK, then runs the SmokeTest."

    dependsOn(tasks.named("clean"))
    dependsOn(tasks.named("installDebugAndroidTest"))
    dependsOn(tasks.named("installDynamicDebugApp"))

    commandLine(
        android.adbExecutable,
        "shell",
        "am",
        "instrument",
        "-w",
        "-e",
        "class",
        "com.adyen.sampleapp.SmokeTest",
        "com.adyen.sampleapp.test/androidx.test.runner.AndroidJUnitRunner"
    )
}
