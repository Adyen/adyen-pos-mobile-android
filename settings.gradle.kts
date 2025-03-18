@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.Properties

rootProject.name = "Sample App"
include(
    ":app-default",
    ":app-dynamic",
    ":dynamic_sdk",
    ":app-manual-initialization",
)


pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

val filePath: String =
    System.getenv("LOCAL_PROPS")
        ?: (rootProject.projectDir.absolutePath + "/local.properties")

val localProps = Properties()
    .apply {
        load(FileInputStream(filePath))
    }

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        // See documentation on our Docs
        // https://docs.adyen.com/point-of-sale/ipp-mobile/tap-to-pay-android/integration-ttp/#add-sdk
        maven {
            // If you have LIVE credentials, you can change to the commented URL bellow and use LIVE API Key instead.
            // The LIVE repository also contains the debug artefacts, so the TEST repository is not needed in that case.
            // url = uri("https://pos-mobile.cdn.adyen.com/adyen-pos-android")
            url = uri("https://pos-mobile-test.cdn.adyen.com/adyen-pos-android")
            credentials(HttpHeaderCredentials::class) {
                name = "x-api-key"
                value = localProps.getProperty("adyen.repo.xapikey")
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
}
