pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }

//      Square SDK
        maven {
            url = uri("https://sdk.squareup.com/android")
            credentials {
                username = extra["SQUARE_READER_SDK_APPLICATION_ID"]?.toString() ?: ""
                password = extra["SQUARE_READER_SDK_REPOSITORY_PASSWORD"]?.toString() ?: ""

            }
        }

    }
}

rootProject.name = "BTControll"
include(":app")