@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.Properties

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
        // Add your repository configuration here.
        google()
        mavenLocal()
        mavenCentral()
        maven {
            url = uri(localProps.getProperty("artifacts.url") ?: "ARTIFACTS URL NOT FOUND")
            credentials {
                username = localProps.getProperty("artifacts.username")
                password = localProps.getProperty("artifacts.token")
            }
        }
    }
}
rootProject.name = "Sample App"
include(":app")
include(":t2p")
