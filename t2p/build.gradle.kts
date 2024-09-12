plugins {
    id("com.android.dynamic-feature")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.adyen.sampletestuploadapp.t2p"
    compileSdk = 34

    defaultConfig {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    signingConfigs {
        create("dummy") {
            storeFile = file("../myreleasekey.keystoreDUMMY")
            storePassword = "password"
            keyAlias = "key0"
            keyPassword = "password"
        }
    }

    buildTypes.getByName("release") {
        isMinifyEnabled = false
        isDebuggable = false
        signingConfig = signingConfigs.getByName("dummy")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

val adyenPosMobileVersion = "1.1.2"
dependencies {

    debugImplementation("com.adyen.ipp:pos-mobile-debug:$adyenPosMobileVersion")
    releaseImplementation("com.adyen.ipp:pos-mobile-release:$adyenPosMobileVersion")

    implementation(project(":app"))
    implementation("androidx.startup:startup-runtime:1.1.1")


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.core:core-ktx:1.13.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}