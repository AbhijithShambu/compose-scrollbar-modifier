import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "lib"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(libs.androidx.annotation.jvm)
        }

        // Adds common test dependencies
        commonTest.dependencies {
            implementation(kotlin("test"))

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.assertk)
        }

        // Adds the desktop test dependency
        val desktopTest by getting
        desktopTest.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk
        .get()
        .toInt()
    namespace = "com.shambu.compose.scrollbar"

    defaultConfig {
        minSdk = libs.versions.android.minSdk
            .get()
            .toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

mavenPublishing {
//    publishToMavenCentral(SonatypeHost.DEFAULT)
    // or when publishing to https://s01.oss.sonatype.org
    publishToMavenCentral(SonatypeHost.S01, automaticRelease = false)
    signAllPublications()
    coordinates("com.shambu.compose", "compose-scrollbar-modifier", "1.0.0")

    pom {
        name.set(project.name)
        description.set("Compose multiplatform library for displaying Scrollbar using modifer.")
        inceptionYear.set("2024")
        url.set("https://github.com/AbhijithShambu/compose-scrollbar-modifier")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("AbhijithShambu")
                name.set("Abhijith Shambu")
                url.set("https://github.com/AbhijithShambu/")
            }
        }
        scm {
            url.set("https://github.com/AbhijithShambu/compose-scrollbar-modifier")
            connection.set("scm:git:git://github.com/AbhijithShambu/compose-scrollbar-modifier.git")
            developerConnection.set("scm:git:ssh://git@github.com:AbhijithShambu/compose-scrollbar-modifier.git")
        }
    }
}
