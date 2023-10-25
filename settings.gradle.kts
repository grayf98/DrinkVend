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
//        maven {
//            url = uri("https://sdk.squareup.com/android")
//            credentials {
//                TODO GET APPLICATION ID AND REPO PASSWORD
//                username = SQUARE_READER_SDK_APPLICATION_ID;
//                password = SQUARE_READER_SDK_REPOSITORY_PASSWORD;

//            }
//        }

    }
}

rootProject.name = "BTControll"
include(":app")