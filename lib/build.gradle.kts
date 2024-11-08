import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
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
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    namespace = "com.shambu.compose.scrollbar"

//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//    sourceSets["main"].res.srcDirs("src/androidMain/res")
//    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
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
        description.set("Compose multiplatform library for displaying Scrollbar on Row or Column.")
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
