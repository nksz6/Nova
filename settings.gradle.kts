@file:Suppress("UnstableApiUsage") //giving me dumb warnings

//settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    //enforce repository resolution from this settings file.
    //any repositories added directly in sub-projects (build.gradle files) will be ignored.
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        //central repository definitions for the entire project.
        google()
        mavenCentral()

        //add any other shared repositories here...
        //example: maven("https://...")
    }
}

rootProject.name = "MyAndroidProject"

include(":app")
//include other modules...