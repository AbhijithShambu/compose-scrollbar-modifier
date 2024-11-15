plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.composeMultiplatform).apply(false)
    alias(libs.plugins.composeCompiler).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlintGradle)
    alias(libs.plugins.mavenPublish).apply(false)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint") // Version should be inherited from parent

    // Optionally configure plugin
//    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
//        version.set("1.0.1")
//    }

//    dependencies {
//        ktlintRuleset("io.nlopez.compose.rules:ktlint:0.4.17")
//    }
}

tasks.register<Copy>("setUpGitHooks") {
    group = "help"
    from("$rootDir/.hooks")
    into("$rootDir/.git/hooks")
}
