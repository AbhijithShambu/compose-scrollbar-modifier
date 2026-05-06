Maven Central Publishing Plan: compose-scrollbar-modifier

This plan is organized into six phases that must be completed in sequence. Each phase has clear pass/fail criteria before moving to the next.
                                                                                                                                                                                
---
Phase 1: Code Cleanup and API Hardening

This is the most important phase — you cannot un-publish a broken or messy API. Everything here must be done before tagging a release.

1.1 Mark as deprecated - ScrollbarModifierOld.kt

The file /lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/ScrollbarModifierOld.kt contains a single private function (scrollbarOld) marked @Suppress("unused"). It is
dead code: nothing references it, it uses the deprecated Modifier.composed API, and it shadows the production implementation. Mark this file deprecated. There is no risk to
consumers because it is private and not part of any compiled public API.

1.2 Delete or move PlatformName.kt and getPlatformName.kt

The expect fun getPlatformName(): String in /lib/src/commonMain/kotlin/PlatformName.kt is internal, but it leaks into the test file only for a println on line 413 of
ScrollbarModifierTest.kt. The expect/actual infrastructure for this ships inside the production artifact (androidMain, desktopMain, iosMain) for a function that is only used
in test output. Two options:

- Preferred: Move the expect/actual declarations entirely into commonTest / androidTest / desktopTest / iosTest source sets so they are never compiled into the release
  artifact.
- Acceptable: Simply remove the println("Platform: ${getPlatformName()}") line from the test, delete PlatformName.kt and all three getPlatformName.kt actuals.

1.3 Audit and fix the public API surface

Walk through every public declaration and decide whether it should actually be public.

Issues to address:

DefaultScrollbarLayoutScope (in ScrollbarModifier.kt, line 488) — This class is public but its only legitimate use is as the receiver inside the onMeasureAndDraw lambda.
Consumers who implement ScrollbarLayoutScope directly could substitute it, which is probably not intended. If you do not want library consumers extending this class, mark it
internal. If custom layout scopes are a supported use case, add KDoc and keep it public.

Rect.applyPadding (in Utils.kt, line 20) — This is a public extension function on androidx.compose.ui.geometry.Rect. It adds a new member to a framework type in a
consumer-visible namespace. If you did not intend it as part of the API, mark it internal. If it is intentional API, add KDoc.

ColorType.isTransparent (in Utils.kt, line 35) — Same issue as above: public val extension property that leaks implementation detail. Mark internal unless you consider it
API.

ScrollbarSemanticProperties.Keys inner object — The old compose-based approach used string-keyed semantics (these are the Keys string constants), while the current node-based
approach uses typed SemanticsPropertyKey objects directly. The Keys object is still public but is no longer used by the current implementation. Check whether any consumer is
expected to use the string keys, and if not, mark the inner Keys object internal or remove it.

ScrollbarMeasurementResult — The class has internal constructor() which correctly prevents instantiation, but the class itself is public. This is intentional as a return
type. Make sure KDoc explains that consumers receive it but cannot construct it.

1.4 Fix the minSdk discrepancy

There are two contradictory values for minSdk:

- gradle/libs.versions.toml line 4: android-minSdk = "28"
- gradle.properties line 17: android.minSdk=24

The lib/build.gradle.kts reads from the version catalog, so the effective value for the published library is 28. The sample/composeApp/build.gradle.kts also reads from the
version catalog, so both would use 28. The gradle.properties file contains stale values from a template that are no longer driving anything (the version catalog takes
precedence in Gradle 7+), but they are confusing.

Decision to make: What is the intended minimum SDK? Android 28 (Android 9.0, ~86% device coverage as of 2025) is quite reasonable for a Compose library, since Jetpack Compose
itself requires API 21 but modern Compose versions work best at 24+. However, if you want the widest reach, API 24 is the standard floor for CMP libraries. Pick one value,
update libs.versions.toml to that value, and remove or correct the stale lines in gradle.properties.

The gradle.properties also contains stale plugin version pins (kotlin.version=1.9.21, agp.version=8.1.1, compose.version=1.5.11, dokka.version=1.9.0, klint.version=11.6.1,
maven-publish.version=0.25.3) that conflict with the version catalog. These properties are not read by any build.gradle.kts in the project (the catalog is used everywhere),
but they are misleading. Remove them, or at minimum add a comment stating they are legacy and not active.

1.5 Verify Kotlin and Compose Multiplatform versions

- Kotlin 2.0.21 was released October 2024. As of May 2026 (current date), Kotlin 2.1.x or 2.2.x is likely current. Check the Kotlin releases page
  (https://kotlinlang.org/docs/releases.html). Upgrading ensures consumers get a library compiled with a recent stdlib and that there are no known compiler bugs.
- Compose Multiplatform 1.7.0 was the November 2024 release. Check JetBrains releases (https://github.com/JetBrains/compose-multiplatform/releases) for the current stable.
  CMP 1.7.x or 1.8.x is likely more current. Align these before the first release to avoid publishing a library pinned to a much older Compose version.
- Dokka 1.9.20 is for Kotlin 1.x. With Kotlin 2.x, the Dokka Gradle plugin 2.x series is recommended; the v1.x series works but generates deprecation warnings in the Gradle
  output and will eventually stop working.

1.6 Run the full test suite locally across all targets

Before publishing, all tests must pass green:

./gradlew :lib:desktopTest
./gradlew :lib:testDebugUnitTest   # Android unit tests

iOS tests require a macOS machine with Xcode. Run:
./gradlew :lib:iosSimulatorArm64Test
or the Xcode-integrated test flow. Confirm all 12 test cases in ScrollbarModifierTest pass.

  ---
Phase 2: Versioning and Build Configuration

2.1 Choose a versioning strategy

The current hardcoded "1.0.0" in lib/build.gradle.kts (line 86) is a single source of truth for the first release, which is fine, but it has no connection to the git tag that
will trigger the release. Fix this.

Recommended approach — Read the version from an environment variable or a gradle.properties key set by CI:

In gradle/libs.versions.toml, add:
library-version = "1.0.0"

In lib/build.gradle.kts, change the coordinates line to read:
coordinates("io.github.abhijithshambu", "compose-scrollbar-modifier", libs.versions.library.version.get())

Then in CI, the publish workflow sets the version by modifying libs.versions.toml or by passing -Plibrary.version=1.0.0 via the Gradle command line. An even simpler
alternative used by many OSS libraries:

In lib/build.gradle.kts:
coordinates(
"io.github.abhijithshambu",
"compose-scrollbar-modifier",
System.getenv("LIBRARY_VERSION") ?: "1.0.0-SNAPSHOT"
)

The CI workflow sets LIBRARY_VERSION from the git tag (e.g., v1.0.0 stripped to 1.0.0). Local builds produce a SNAPSHOT automatically, which is easy to test with
publishToMavenLocal.

2.2 Fix the POM description typo

In lib/build.gradle.kts line 91:
description.set("Compose multiplatform library for displaying Scrollbar using modifer.")
"modifer" should be "modifier". Fix the spelling before publishing since this text appears on the Maven Central search page and in Gradle dependency reports.

2.3 Update AGP version

gradle/libs.versions.toml line 2 has agp = "8.5.2". As of May 2026, AGP 8.7.x or 8.8.x is likely available. This is not strictly required for publishing, but pinning to a
very old AGP can cause build failures for consumers who use the sample on modern Android Studio. Update to a current stable AGP alongside the Kotlin/CMP upgrade in step 1.5.

  ---
Phase 3: Sonatype and GPG Setup

3.1 Create a Sonatype OSSRH account

If you do not already have one:

1. Go to https://central.sonatype.com and click "Sign In", then "Register".
2. Use the email address associated with your GitHub profile.
3. Verify your email.

The vanniktech/gradle-maven-publish-plugin v0.25.3 targets the legacy s01.oss.sonatype.org endpoint (SonatypeHost.S01 in lib/build.gradle.kts line 84). Sonatype migrated to
the new Central Portal (https://central.sonatype.com) in 2024. New accounts are created on the new portal. If your account is a new account, upgrade
gradle-maven-publish-plugin to at least 0.28.0 and change SonatypeHost.S01 to SonatypeHost.CENTRAL_PORTAL. This is a critical compatibility issue.

3.2 Claim the io.github.abhijithshambu namespace

1. Log into https://central.sonatype.com.
2. Go to "Namespaces" and add io.github.abhijithshambu.
3. The portal will ask you to verify ownership. For a group ID derived from a GitHub username, the standard method is to create a public repository named io.github.abhijithshambu
   in your GitHub account. Alternatively, if you own the domain shambu.com, add a DNS TXT record.
4. Wait for namespace approval (usually minutes to hours).

3.3 Generate a GPG signing key

lib/build.gradle.kts already has signAllPublications(), so the key must exist and be available to Gradle.

# Generate a 4096-bit RSA key
gpg --gen-key

# Export the public key to a keyserver
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Export the secret key (ASCII-armored, for use as a GitHub Secret)
gpg --armor --export-secret-keys YOUR_KEY_ID

The output of the last command is the value you will set as GPG_PRIVATE_KEY in GitHub Secrets. Also record the passphrase you used.

3.4 Generate Sonatype user tokens

1. Log into https://central.sonatype.com.
2. Go to Account Settings > Generate User Token.
3. Record the username token and password token. These are the values for OSSRH_USERNAME and OSSRH_PASSWORD (or MAVEN_CENTRAL_USERNAME / MAVEN_CENTRAL_PASSWORD in the new
   portal API — check what vanniktech plugin v0.28+ expects).

3.5 Add secrets to GitHub repository

In the GitHub repository at https://github.com/AbhijithShambu/compose-scrollbar-modifier, go to Settings > Secrets and Variables > Actions > New repository secret. Add:

┌─────────────────┬────────────────────────────────────────────────────────────────────────────────────┐
│   Secret name   │                                       Value                                        │
├─────────────────┼────────────────────────────────────────────────────────────────────────────────────┤
│ OSSRH_USERNAME  │ Sonatype user token username                                                       │
├─────────────────┼────────────────────────────────────────────────────────────────────────────────────┤
│ OSSRH_PASSWORD  │ Sonatype user token password                                                       │
├─────────────────┼────────────────────────────────────────────────────────────────────────────────────┤
│ GPG_PRIVATE_KEY │ ASCII-armored GPG private key (entire -----BEGIN PGP PRIVATE KEY BLOCK----- block) │
├─────────────────┼────────────────────────────────────────────────────────────────────────────────────┤
│ GPG_PASSPHRASE  │ GPG key passphrase                                                                 │
└─────────────────┴────────────────────────────────────────────────────────────────────────────────────┘

  ---
Phase 4: CI Publishing Workflow

4.1 Create .github/workflows/publish.yml

The existing build.yml only runs ./gradlew assemble on push to main. You need a separate workflow triggered on version tags.

The workflow structure must:

1. Trigger on push with tags matching v*.*.* only (never on main pushes, to avoid accidental releases).
2. Run on macOS (required for iOS framework compilation; ubuntu cannot compile iOS targets).
3. Set up Java 17.
4. Extract the version number from the git tag (v1.0.0 → 1.0.0).
5. Run tests before publishing (fail-fast).
6. Configure GPG signing in Gradle.
7. Run the publish task.

Concrete workflow sketch (do not copy blindly — verify vanniktech plugin task names for your version):

name: Publish to Maven Central
on:
push:
tags:
- 'v[0-9]+.[0-9]+.[0-9]+'

jobs:
publish:
runs-on: macos-latest
steps:
- uses: actions/checkout@v4

        - uses: actions/setup-java@v4
          with:
            distribution: 'corretto'
            java-version: '17'

        - name: Cache Gradle
          uses: actions/cache@v4
          with:
            path: |
              ~/.gradle/caches
              ~/.gradle/wrapper
            key: gradle-${{ hashFiles('**/*.gradle.kts', '**/libs.versions.toml') }}

        - name: Extract version from tag
          id: version
          run: echo "VERSION=${GITHUB_REF_NAME#v}" >> $GITHUB_OUTPUT

        - name: Run tests
          run: ./gradlew :lib:desktopTest :lib:testDebugUnitTest

        - name: Publish to Maven Central
          env:
            LIBRARY_VERSION: ${{ steps.version.outputs.VERSION }}
            ORG_GRADLE_PROJECT_mavenCentralUsername: ${{ secrets.OSSRH_USERNAME }}
            ORG_GRADLE_PROJECT_mavenCentralPassword: ${{ secrets.OSSRH_PASSWORD }}
            ORG_GRADLE_PROJECT_signingInMemoryKey: ${{ secrets.GPG_PRIVATE_KEY }}
            ORG_GRADLE_PROJECT_signingInMemoryKeyPassword: ${{ secrets.GPG_PASSPHRASE }}
          run: ./gradlew publishAllPublicationsToMavenCentralRepository

The exact Gradle property names for the vanniktech plugin are documented in its README (https://github.com/vanniktech/gradle-maven-publish-plugin). For the new Central Portal
API, you would replace mavenCentralUsername/Password with the appropriate property names listed in the plugin's documentation for your version.

Note: macos-latest is used because iOS framework compilation (iosX64, iosArm64, iosSimulatorArm64) requires Xcode toolchain. Publishing all KMP targets requires a macOS
runner.

4.2 Update existing CI workflow

In .github/workflows/build.yml:

1. Remove feature/ci_support from the branches list (it appears to be a stale branch used during CI setup). Keep main and pull_request.
2. Add iOS simulator test step conditional on macos-* runner (the current matrix already includes macos-12; the matrix OS macos-12 is now EOL on GitHub Actions — update to
   macos-13 or macos-14).
3. Update actions/checkout and actions/setup-java from v3 to v4.

4.3 Local smoke test before triggering CI

Before tagging, verify the publishing pipeline works locally by publishing to Maven Local:

./gradlew :lib:publishToMavenLocal

Then create a minimal test project that adds mavenLocal() to its repositories and adds the dependency io.github.abhijithshambu:compose-scrollbar-modifier:1.0.0. Verify the
dependency resolves and compiles. This catches missing artifacts (e.g., a target that failed to publish silently) before you push a tag.

  ---
Phase 5: Documentation Finalization

5.1 Update README Installation section

Replace the placeholder in README.md:

// To be published soon ;)

with the actual coordinates for Gradle Kotlin DSL and Gradle Groovy DSL:

// build.gradle.kts (Kotlin DSL)
implementation("io.github.abhijithshambu:compose-scrollbar-modifier:1.0.0")

// build.gradle (Groovy DSL)
implementation 'io.github.abhijithshambu:compose-scrollbar-modifier:1.0.0'

Also add a version badge at the top of the README. Maven Central provides an SVG badge at:
https://img.shields.io/maven-central/v/io.github.abhijithshambu/compose-scrollbar-modifier

5.2 Fix the README copy error in the Vertical Scrollbar section

In README.md line 59:
Similarly, to add horizontal scrolling with a scrollbar, use the `horizontalScrollWithScrollbar` function:
This describes a vertical scrollbar but says "horizontal scrolling". Fix to say "vertical scrolling" and reference verticalScrollWithScrollbar.

5.3 Create CHANGELOG.md

Maven Central consumers expect a changelog. Create /CHANGELOG.md with this structure:

# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-05-XX

### Added
- Initial release
- Vertical and horizontal scrollbar modifier support (`verticalScrollWithScrollbar`, `horizontalScrollWithScrollbar`)
- Low-level `Modifier.scrollbar` API with custom measurement and draw support
- `ScrollbarConfig` for appearance customization (indicator/bar color, thickness, corner radius, padding, border, auto-hide animation)
- `ScrollbarState` for programmatic control (`dragTo`, `dragBy`)
- Draggable scrollbar indicator
- Multiplatform support: Android, iOS (iosX64, iosArm64, iosSimulatorArm64), Desktop JVM
- `LazyListState` support via low-level `scrollbar` modifier

5.4 Improve KDoc for @sample references

Both VerticalScrollWithScrollbar.kt and HorizontalScrollWithScrollbar.kt have @sample KDoc tags referencing io.github.abhijithshambu.scrollbar.sample.VerticalScrollExample and
io.github.abhijithshambu.scrollbar.sample.HorizontalScrollWithScrollbarSample. Dokka will try to resolve these from the sample module. Verify that:

1. The sample module is included in the Dokka configuration.
2. The exact class and function names referenced in @sample actually exist (e.g., VerticalScrollExample vs VerticalScrollWithScrollbarSample).

Open /sample/composeApp/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/sample/VerticalScrollWithScrollbarSample.kt and confirm the function name matches what is
referenced. If Dokka cannot resolve the samples, they are silently dropped from documentation, which is fine, but broken sample references generate Dokka warnings.

  ---
Phase 6: The Actual Release

6.1 Final pre-release checklist

Before creating the tag, verify every item:

- ScrollbarModifierOld.kt deleted
- getPlatformName infrastructure removed or moved to test source sets
- API visibility decisions made (DefaultScrollbarLayoutScope, Rect.applyPadding, ColorType.isTransparent)
- minSdk value is consistent between libs.versions.toml and gradle.properties
- POM description typo fixed ("modifer" → "modifier")
- Version strategy implemented (env var or version catalog key)
- All tests pass locally: ./gradlew :lib:desktopTest
- ./gradlew :lib:publishToMavenLocal succeeds without errors
- Local smoke test project confirms the artifact resolves
- Sonatype namespace io.github.abhijithshambu is verified and approved
- All four GitHub Secrets are set
- publish.yml workflow file is committed to main
- README Installation section has real coordinates
- CHANGELOG.md exists

6.2 Create and push the release tag

git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0

This triggers .github/workflows/publish.yml.

6.3 Monitor the CI publish run

Go to the GitHub Actions tab and watch the publish workflow. The publishAllPublicationsToMavenCentralRepository task will upload all artifacts and sign them. With
automaticRelease = false in lib/build.gradle.kts line 84, the artifacts land in a staging repository and must be manually released.

6.4 Manually release from the Sonatype portal

Because automaticRelease = false is set:

For legacy S01 (if you stay on SonatypeHost.S01):
1. Go to https://s01.oss.sonatype.org.
2. Log in and navigate to "Staging Repositories".
3. Find your staging repository (it will be named something like comshambucompose-1001).
4. Click "Close" and wait for all validation rules to pass (POM completeness, Javadoc/sources JARs, signing, etc.).
5. Once closed successfully, click "Release".
6. The release propagates to Maven Central within 10–30 minutes.

For the new Central Portal:
1. Go to https://central.sonatype.com.
2. Navigate to the "Deployments" section.
3. Find the pending deployment and click "Publish".

6.5 Verify the release on Maven Central

After 10–30 minutes:
1. Search https://central.sonatype.com/search?q=io.github.abhijithshambu
2. Confirm compose-scrollbar-modifier:1.0.0 appears.
3. Add the dependency to the local smoke test project using mavenCentral() (not mavenLocal()) and confirm it resolves from the real repository.

6.6 Post-release actions

1. Create a GitHub Release at https://github.com/AbhijithShambu/compose-scrollbar-modifier/releases/new, pointing to tag v1.0.0. Paste the CHANGELOG.md entry as the release
   notes.
2. Update README.md version badge once the artifact is live (the shield.io badge auto-updates from Maven Central metadata).
3. On main branch, update LIBRARY_VERSION (or libs.versions.toml library-version entry) to 1.0.1-SNAPSHOT to indicate active development after the release.

  ---
Summary of Decisions Requiring Your Input

Before starting implementation, decide:

1. minSdk: 24 (Compose minimum, widest reach) or 28 (current catalog value)? This affects who can use the library on Android.
2. DefaultScrollbarLayoutScope visibility: Should it be internal (no external custom implementations) or stay public (consumers can implement ScrollbarLayoutScope)?
3. Rect.applyPadding and ColorType.isTransparent: Internal implementation helpers or intentionally exposed API?
4. ScrollbarSemanticProperties.Keys inner object: Remove (no consumers will use string key constants) or keep as internal?
5. Sonatype endpoint: Are you a new Sonatype account (use new Central Portal, upgrade plugin to 0.28+) or an existing S01 account (keep current config)?
6. Kotlin/CMP version upgrades: Do them as part of 1.0.0 or cut the release with current versions and upgrade in 1.0.1?

  ---
6. Kotlin/CMP version upgrades: Do them as part of 1.0.0 or cut the release with current versions and upgrade in 1.0.1?
   notes.
2. Update README.md version badge once the artifact is live (the shield.io badge auto-updates from Maven Central metadata).
3. On main branch, update LIBRARY_VERSION (or libs.versions.toml library-version entry) to 1.0.1-SNAPSHOT to indicate active development after the release.

  ---
Summary of Decisions Requiring Your Input

Before starting implementation, decide:

1. minSdk: 24 (Compose minimum, widest reach) or 28 (current catalog value)? This affects who can use the library on Android.
2. DefaultScrollbarLayoutScope visibility: Should it be internal (no external custom implementations) or stay public (consumers can implement ScrollbarLayoutScope)?
3. Rect.applyPadding and ColorType.isTransparent: Internal implementation helpers or intentionally exposed API?
4. ScrollbarSemanticProperties.Keys inner object: Remove (no consumers will use string key constants) or keep as internal?
5. Sonatype endpoint: Are you a new Sonatype account (use new Central Portal, upgrade plugin to 0.28+) or an existing S01 account (keep current config)?
6. Kotlin/CMP version upgrades: Do them as part of 1.0.0 or cut the release with current versions and upgrade in 1.0.1?

  ---
Critical Files for Implementation

- /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/lib/build.gradle.kts
2. Update README.md version badge once the artifact is live (the shield.io badge auto-updates from Maven Central metadata).
3. On main branch, update LIBRARY_VERSION (or libs.versions.toml library-version entry) to 1.0.1-SNAPSHOT to indicate active development after the release.

  ---
Summary of Decisions Requiring Your Input

Before starting implementation, decide:

1. minSdk: 24 (Compose minimum, widest reach) or 28 (current catalog value)? This affects who can use the library on Android.
2. DefaultScrollbarLayoutScope visibility: Should it be internal (no external custom implementations) or stay public (consumers can implement ScrollbarLayoutScope)?
3. Rect.applyPadding and ColorType.isTransparent: Internal implementation helpers or intentionally exposed API?

# Generate a 4096-bit RSA key
gpg --gen-key

# Export the public key to a keyserver
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Export the secret key (ASCII-armored, for use as a GitHub Secret)
gpg --armor --export-secret-keys YOUR_KEY_ID

The output of the last command is the value you will set as GPG_PRIVATE_KEY in GitHub Secrets. Also record the passphrase you used.

3.4 Generate Sonatype user tokens

1. Log into https://central.sonatype.com.
2. Go to Account Settings > Generate User Token.
3. Record the username token and password token. These are the values for OSSRH_USERNAME and OSSRH_PASSWORD (or MAVEN_CENTRAL_USERNAME / MAVEN_CENTRAL_PASSWORD in the new
   portal API — check what vanniktech plugin v0.28+ expects).

3.5 Add secrets to GitHub repository

In the GitHub repository at https://github.com/AbhijithShambu/compose-scrollbar-modifier, go to Settings > Secrets and Variables > Actions > New repository secret. Add:

┌─────────────────┬────────────────────────────────────────────────────────────────────────────────────┐
│   Secret name   │                                       Value                                        │                                                                      
├─────────────────┼────────────────────────────────────────────────────────────────────────────────────┤
│ OSSRH_USERNAME  │ Sonatype user token username                                                       │                                                                      
├─────────────────┼────────────────────────────────────────────────────────────────────────────────────┤                                                                    
│ OSSRH_PASSWORD  │ Sonatype user token password                                                       │
├─────────────────┼────────────────────────────────────────────────────────────────────────────────────┤                                                                      
│ GPG_PRIVATE_KEY │ ASCII-armored GPG private key (entire -----BEGIN PGP PRIVATE KEY BLOCK----- block) │                                                                      
├─────────────────┼────────────────────────────────────────────────────────────────────────────────────┤
│ GPG_PASSPHRASE  │ GPG key passphrase                                                                 │
└─────────────────┴────────────────────────────────────────────────────────────────────────────────────┘

  ---
Phase 4: CI Publishing Workflow

4.1 Create .github/workflows/publish.yml

The existing build.yml only runs ./gradlew assemble on push to main. You need a separate workflow triggered on version tags.

The workflow structure must:

1. Trigger on push with tags matching v*.*.* only (never on main pushes, to avoid accidental releases).
2. Run on macOS (required for iOS framework compilation; ubuntu cannot compile iOS targets).
3. Set up Java 17.
4. Extract the version number from the git tag (v1.0.0 → 1.0.0).
5. Run tests before publishing (fail-fast).
6. Configure GPG signing in Gradle.
7. Run the publish task.

Concrete workflow sketch (do not copy blindly — verify vanniktech plugin task names for your version):

name: Publish to Maven Central
on:
push:
tags:
- 'v[0-9]+.[0-9]+.[0-9]+'

jobs:
publish:
runs-on: macos-latest
steps:
- uses: actions/checkout@v4

        - uses: actions/setup-java@v4
          with:
            distribution: 'corretto'
            java-version: '17'

        - name: Cache Gradle
          uses: actions/cache@v4
          with:
            path: |
              ~/.gradle/caches
              ~/.gradle/wrapper
            key: gradle-${{ hashFiles('**/*.gradle.kts', '**/libs.versions.toml') }}

        - name: Extract version from tag
          id: version
          run: echo "VERSION=${GITHUB_REF_NAME#v}" >> $GITHUB_OUTPUT

        - name: Run tests
          run: ./gradlew :lib:desktopTest :lib:testDebugUnitTest

        - name: Publish to Maven Central
          env:
            LIBRARY_VERSION: ${{ steps.version.outputs.VERSION }}
            ORG_GRADLE_PROJECT_mavenCentralUsername: ${{ secrets.OSSRH_USERNAME }}
            ORG_GRADLE_PROJECT_mavenCentralPassword: ${{ secrets.OSSRH_PASSWORD }}
            ORG_GRADLE_PROJECT_signingInMemoryKey: ${{ secrets.GPG_PRIVATE_KEY }}
            ORG_GRADLE_PROJECT_signingInMemoryKeyPassword: ${{ secrets.GPG_PASSPHRASE }}
          run: ./gradlew publishAllPublicationsToMavenCentralRepository

The exact Gradle property names for the vanniktech plugin are documented in its README (https://github.com/vanniktech/gradle-maven-publish-plugin). For the new Central Portal
API, you would replace mavenCentralUsername/Password with the appropriate property names listed in the plugin's documentation for your version.

Note: macos-latest is used because iOS framework compilation (iosX64, iosArm64, iosSimulatorArm64) requires Xcode toolchain. Publishing all KMP targets requires a macOS
runner.

4.2 Update existing CI workflow

In .github/workflows/build.yml:

1. Remove feature/ci_support from the branches list (it appears to be a stale branch used during CI setup). Keep main and pull_request.
2. Add iOS simulator test step conditional on macos-* runner (the current matrix already includes macos-12; the matrix OS macos-12 is now EOL on GitHub Actions — update to
   macos-13 or macos-14).
3. Update actions/checkout and actions/setup-java from v3 to v4.

4.3 Local smoke test before triggering CI

Before tagging, verify the publishing pipeline works locally by publishing to Maven Local:

./gradlew :lib:publishToMavenLocal

Then create a minimal test project that adds mavenLocal() to its repositories and adds the dependency io.github.abhijithshambu:compose-scrollbar-modifier:1.0.0. Verify the
dependency resolves and compiles. This catches missing artifacts (e.g., a target that failed to publish silently) before you push a tag.

  ---
Phase 5: Documentation Finalization

5.1 Update README Installation section

Replace the placeholder in README.md:

// To be published soon ;)

with the actual coordinates for Gradle Kotlin DSL and Gradle Groovy DSL:

// build.gradle.kts (Kotlin DSL)
implementation("io.github.abhijithshambu:compose-scrollbar-modifier:1.0.0")

// build.gradle (Groovy DSL)
implementation 'io.github.abhijithshambu:compose-scrollbar-modifier:1.0.0'

Also add a version badge at the top of the README. Maven Central provides an SVG badge at:
https://img.shields.io/maven-central/v/io.github.abhijithshambu/compose-scrollbar-modifier

5.2 Fix the README copy error in the Vertical Scrollbar section

In README.md line 59:
Similarly, to add horizontal scrolling with a scrollbar, use the `horizontalScrollWithScrollbar` function:
This describes a vertical scrollbar but says "horizontal scrolling". Fix to say "vertical scrolling" and reference verticalScrollWithScrollbar.

5.3 Create CHANGELOG.md

Maven Central consumers expect a changelog. Create /CHANGELOG.md with this structure:

# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-05-XX

### Added
- Initial release
- Vertical and horizontal scrollbar modifier support (`verticalScrollWithScrollbar`, `horizontalScrollWithScrollbar`)
- Low-level `Modifier.scrollbar` API with custom measurement and draw support
- `ScrollbarConfig` for appearance customization (indicator/bar color, thickness, corner radius, padding, border, auto-hide animation)
- `ScrollbarState` for programmatic control (`dragTo`, `dragBy`)
- Draggable scrollbar indicator
- Multiplatform support: Android, iOS (iosX64, iosArm64, iosSimulatorArm64), Desktop JVM
- `LazyListState` support via low-level `scrollbar` modifier

5.4 Improve KDoc for @sample references

Both VerticalScrollWithScrollbar.kt and HorizontalScrollWithScrollbar.kt have @sample KDoc tags referencing io.github.abhijithshambu.scrollbar.sample.VerticalScrollExample and
io.github.abhijithshambu.scrollbar.sample.HorizontalScrollWithScrollbarSample. Dokka will try to resolve these from the sample module. Verify that:

1. The sample module is included in the Dokka configuration.
2. The exact class and function names referenced in @sample actually exist (e.g., VerticalScrollExample vs VerticalScrollWithScrollbarSample).

Open /sample/composeApp/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/sample/VerticalScrollWithScrollbarSample.kt and confirm the function name matches what is
referenced. If Dokka cannot resolve the samples, they are silently dropped from documentation, which is fine, but broken sample references generate Dokka warnings.

  ---
Phase 6: The Actual Release

6.1 Final pre-release checklist

Before creating the tag, verify every item:

- ScrollbarModifierOld.kt deleted
- getPlatformName infrastructure removed or moved to test source sets
- API visibility decisions made (DefaultScrollbarLayoutScope, Rect.applyPadding, ColorType.isTransparent)
- minSdk value is consistent between libs.versions.toml and gradle.properties
- POM description typo fixed ("modifer" → "modifier")
- Version strategy implemented (env var or version catalog key)
- All tests pass locally: ./gradlew :lib:desktopTest
- ./gradlew :lib:publishToMavenLocal succeeds without errors
- Local smoke test project confirms the artifact resolves
- Sonatype namespace io.github.abhijithshambu is verified and approved
- All four GitHub Secrets are set
- publish.yml workflow file is committed to main
- README Installation section has real coordinates
- CHANGELOG.md exists

6.2 Create and push the release tag

git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0

This triggers .github/workflows/publish.yml.

6.3 Monitor the CI publish run

Go to the GitHub Actions tab and watch the publish workflow. The publishAllPublicationsToMavenCentralRepository task will upload all artifacts and sign them. With
automaticRelease = false in lib/build.gradle.kts line 84, the artifacts land in a staging repository and must be manually released.

6.4 Manually release from the Sonatype portal

Because automaticRelease = false is set:

For legacy S01 (if you stay on SonatypeHost.S01):
1. Go to https://s01.oss.sonatype.org.
2. Log in and navigate to "Staging Repositories".
3. Find your staging repository (it will be named something like comshambucompose-1001).
4. Click "Close" and wait for all validation rules to pass (POM completeness, Javadoc/sources JARs, signing, etc.).
5. Once closed successfully, click "Release".
6. The release propagates to Maven Central within 10–30 minutes.

For the new Central Portal:
1. Go to https://central.sonatype.com.
2. Navigate to the "Deployments" section.
3. Find the pending deployment and click "Publish".

6.5 Verify the release on Maven Central

After 10–30 minutes:
1. Search https://central.sonatype.com/search?q=io.github.abhijithshambu
2. Confirm compose-scrollbar-modifier:1.0.0 appears.
3. Add the dependency to the local smoke test project using mavenCentral() (not mavenLocal()) and confirm it resolves from the real repository.

6.6 Post-release actions

1. Create a GitHub Release at https://github.com/AbhijithShambu/compose-scrollbar-modifier/releases/new, pointing to tag v1.0.0. Paste the CHANGELOG.md entry as the release
   notes.
2. Update README.md version badge once the artifact is live (the shield.io badge auto-updates from Maven Central metadata).
3. On main branch, update LIBRARY_VERSION (or libs.versions.toml library-version entry) to 1.0.1-SNAPSHOT to indicate active development after the release.

  ---
Summary of Decisions Requiring Your Input

Before starting implementation, decide:

1. minSdk: 24 (Compose minimum, widest reach) or 28 (current catalog value)? This affects who can use the library on Android.
2. DefaultScrollbarLayoutScope visibility: Should it be internal (no external custom implementations) or stay public (consumers can implement ScrollbarLayoutScope)?
3. Rect.applyPadding and ColorType.isTransparent: Internal implementation helpers or intentionally exposed API?
4. ScrollbarSemanticProperties.Keys inner object: Remove (no consumers will use string key constants) or keep as internal?
5. Sonatype endpoint: Are you a new Sonatype account (use new Central Portal, upgrade plugin to 0.28+) or an existing S01 account (keep current config)?
6. Kotlin/CMP version upgrades: Do them as part of 1.0.0 or cut the release with current versions and upgrade in 1.0.1?

  ---
Critical Files for Implementation

- /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/lib/build.gradle.kts
- /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/gradle/libs.versions.toml
- /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/ScrollbarModifierOld.kt
- /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/foundation/Utils.kt
- /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/.github/workflows/build.yml

❯ If the minSdk 24 does not break the project, go ahead and use it, consumers should not implement ScrollbarLayoutScope - only be able to use it, intentionally exposed API,    
keep as internal, i dont have any account as of now - Give me details - is there any pricing? - i have no idea about Sonatype account or publishing, upgrade them as 1.0.1

⏺ Let me check whether minSdk 24 actually causes any issues in the codebase.

Searched for 8 patterns (ctrl+o to expand)

⏺ Bash(find /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier -name "AndroidManifest.xml" | xargs grep -l "" 2>/dev/null)

Searched for 3 patterns, read 2 files (ctrl+o to expand)

⏺ Good. Now I have everything I need. The @FloatRange annotation is the only non-Compose androidx dependency in the library. @FloatRange is a compile-time annotation — it has  
no API level requirement and works on all Android versions. minSdk 24 is completely safe. Let me now produce the concrete, implementation-ready plan.
                                                                                                                                                                                
---                                                                                                                                                                         
minSdk 24 is safe — no issues found

The library source contains zero Build.VERSION.SDK_INT checks, zero @RequiresApi annotations, and the only non-Compose androidx dependency is @FloatRange from annotation-jvm,
which is a pure compile-time annotation with no runtime API level requirement. The only file that currently says minSdk=28 is the built artifact manifest at
lib/build/intermediates/... — that is generated from the catalog value and will update automatically once you change the catalog. Changing android-minSdk from "28" to "24" is
fully safe.

The fontWeightAdjustment and colorMode in the sample app's AndroidManifest do require API 26+ and API 31+ respectively, but those are in the sample application, not the
published library. Consumers of the library are completely unaffected.

  ---
Now here is the fully concrete, copy-paste-ready implementation plan incorporating all your decisions.

  ---
What is Sonatype / Maven Central, and does it cost anything?

Maven Central is free. There is no charge to publish open source libraries. Sonatype runs the infrastructure that backs Maven Central and provides the tooling to upload and
release artifacts. Here is the full picture:

- Maven Central (https://central.sonatype.com) — the public repository where billions of dependency downloads happen. It is what you add when you write mavenCentral() in a
  Gradle file.
- Sonatype — the company that operates Maven Central. They provide two things relevant to you: the new Central Portal (web UI to manage your releases) and the legacy OSSRH
  (s01.oss.sonatype.org, the old JIRA-based system). New accounts created today go through the Central Portal only.
- GPG signing — Maven Central requires every artifact to be cryptographically signed. This proves the artifact was published by you and was not tampered with. You generate
  the key locally and upload the public half to a public keyserver.
- Cost: $0. All of this is free for open source projects.

  ---
Complete Step-by-Step Implementation Plan

Step 1 — Code changes (do these in one commit)

1a. gradle/libs.versions.toml — three changes

Change line 4: android-minSdk = "28" to android-minSdk = "24"

Add a new line for the library version (put it near the top with other version entries):
library-version = "1.0.0"

No other version catalog changes are needed now. Kotlin/CMP upgrades go in the 1.0.1 cycle as you decided.

1b. gradle.properties — remove stale lines

The following lines are dead weight — they were from the original project template and are overridden by the version catalog. Remove them entirely:

kotlin.version=1.9.21
agp.version=8.1.1
compose.version=1.5.11
dokka.version=1.9.0
klint.version=11.6.1
maven-publish.version=0.25.3

Also fix the android.minSdk line: change android.minSdk=24 (it already says 24 — this is actually already correct in gradle.properties, so just leave it as the surviving
single source of truth for any tooling that reads it, after you change the catalog too).

The android.compileSdk=34 and android.targetSdk=34 in gradle.properties are also overridden by the catalog but harmless to leave if you want.

1c. lib/build.gradle.kts — three changes

Version — change line 86:
// Before:
coordinates("io.github.abhijithshambu", "compose-scrollbar-modifier", "1.0.0")

// After:
coordinates(
"io.github.abhijithshambu",
"compose-scrollbar-modifier",
System.getenv("LIBRARY_VERSION") ?: libs.versions.library.version.get(),
)
This means: in CI the env var drives the version; locally (no env var set) it falls back to whatever is in libs.versions.toml. For local publishToMavenLocal testing this just
resolves to 1.0.0 cleanly.

POM description typo — fix line 91:
// Before:
description.set("Compose multiplatform library for displaying Scrollbar using modifer.")

// After:
description.set("Compose multiplatform library for displaying a scrollbar using a modifier.")

ScrollbarLayoutScope — seal it so consumers cannot implement it. The interface is in ScrollbarLayoutScope.kt not build.gradle.kts, but the Gradle change that supports this is
making sure DefaultScrollbarLayoutScope becomes internal. The Gradle file needs no change for this — it is a source-level change described below.

1d. Delete ScrollbarModifierOld.kt

Delete the entire file:
lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/ScrollbarModifierOld.kt
It contains one private function that is never called. Nothing references it. It will not compile warnings-clean because it uses the deprecated Modifier.composed API. Just
delete it.

1e. Move getPlatformName out of production source into test source

Currently PlatformName.kt (the expect declaration) and all three actuals ship inside the release artifact. The function is only used in one test's println. Move all four
files:

- Move /lib/src/commonMain/kotlin/PlatformName.kt → create it instead in /lib/src/commonTest/kotlin/PlatformName.kt
- Move /lib/src/androidMain/kotlin/getPlatformName.kt → /lib/src/androidUnitTest/kotlin/getPlatformName.kt (or androidTest)
- Move /lib/src/desktopMain/kotlin/getPlatformName.kt → /lib/src/desktopTest/kotlin/getPlatformName.kt
- Move /lib/src/iosMain/kotlin/getPlatformName.kt → /lib/src/iosTest/kotlin/getPlatformName.kt

The import in ScrollbarModifierTest.kt (import getPlatformName) will continue to work because the test file and the moved expect declaration are both in commonTest.

1f. lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/ScrollbarModifier.kt — make DefaultScrollbarLayoutScope internal

Change line 488:
// Before:
class DefaultScrollbarLayoutScope(

// After:
internal class DefaultScrollbarLayoutScope(

This class is the concrete implementation of ScrollbarLayoutScope that is instantiated and passed as the receiver inside onMeasureAndDraw. Consumers receive it as the
ScrollbarLayoutScope interface — they call drawWithMeasurements on it. They never need to name the concrete class. Making it internal does not break any consumer code.

1g. lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/foundation/Utils.kt — keep Rect.applyPadding and ColorType.isTransparent public per your decision

No change needed. Both stay public as intentionally exposed API. Add KDoc comments to both since Dokka will now surface them to consumers.

For applyPadding, add above line 20:
/**
* Applies [PaddingValues] insets to this [Rect], shrinking it from each edge by the
* corresponding padding amount resolved for the given [layoutDirection].
  */

For isTransparent, add above line 35:
/** Returns true if this [ColorType] is a [ColorType.Solid] with fully transparent color (alpha == 0). */

1h. lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/foundation/ScrollbarSemanticProperties.kt — make Keys internal

Change:
// Before:
object Keys {

// After:
internal object Keys {

The Keys object contains string constants used by the old Modifier.composed-based implementation (now deleted). The new ModifierNode implementation uses typed
SemanticsPropertyKey objects directly and never references these strings. No consumer needs them.

1i. README.md — fix the copy error in the vertical scrollbar section

Line 59 currently reads:
Similarly, to add horizontal scrolling with a scrollbar, use the `horizontalScrollWithScrollbar` function:

Fix to:
To add vertical scrolling with a scrollbar, use the `verticalScrollWithScrollbar` function:

1j. Create CHANGELOG.md at the repo root

# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

## [1.0.0] - 2026-05-XX

### Added
- Initial public release
- `verticalScrollWithScrollbar` and `horizontalScrollWithScrollbar` modifier functions
- Low-level `Modifier.scrollbar` for custom measurement and draw control
- `ScrollbarConfig` for full appearance customization (colors, thickness, corner radius,
  padding, borders, auto-hide animation, drag enable/disable)
- `ScrollbarState` with programmatic `dragTo` and `dragBy` control
- Draggable scrollbar indicator with gesture handling
- `LazyListState` support via the low-level `scrollbar` modifier
- Multiplatform: Android (minSdk 24), iOS (iosX64, iosArm64, iosSimulatorArm64), Desktop JVM

  ---
Step 2 — Sonatype Central Portal account setup (one-time, ~30 minutes)

This is the new free account system. No JIRA, no tickets, no waiting weeks.

2a. Create your account

1. Go to https://central.sonatype.com
2. Click "Sign In" in the top-right corner
3. Click "Sign in with GitHub" — this is the easiest path since your library is already on GitHub
4. Authorize the OAuth flow
5. You now have a Sonatype Central Portal account, linked to your GitHub identity

2b. Verify your namespace

The namespace io.github.abhijithshambu must be verified before you can publish to it.

1. After logging in, click your username avatar (top right) → "View Namespaces"
2. Click "Add Namespace"
3. Enter com.shambu (you own the whole com.shambu tree, not just the compose sub-namespace — claim the parent)
4. The portal shows you a verification key — a short code like ABCD1234
5. Create a public GitHub repository in your account named exactly that verification key — e.g., https://github.com/AbhijithShambu/ABCD1234 (it can be empty, just needs to
   exist publicly)
6. Click "Verify Namespace" in the portal
7. Delete the verification repo after confirmation (optional, it is no longer needed)

The verification is usually instant but can take up to a few minutes.

2c. Generate a user token (your publishing credentials)

1. In the portal, click your username avatar → "View Account"
2. Scroll down to "Generate User Token"
3. Click "Generate User Token"
4. Copy the Username and Password shown (these are not your login credentials — they are API tokens). Save them somewhere secure. You cannot retrieve the password again — if
   you lose it, regenerate new tokens.

These become your OSSRH_USERNAME and OSSRH_PASSWORD GitHub Secrets.

  ---
Step 3 — Upgrade gradle-maven-publish-plugin to work with the new portal

Your current config uses SonatypeHost.S01 which is the old OSSRH endpoint. New accounts use the Central Portal API. You need plugin version 0.28.0 or later.

3a. Update gradle/libs.versions.toml:

maven-publish = "0.30.0"
(0.30.0 is the stable release that fully supports Central Portal as of early 2025.)

3b. Update lib/build.gradle.kts line 84:

// Before:
publishToMavenCentral(SonatypeHost.S01, automaticRelease = false)

// After:
publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

Note: automaticRelease is removed here. The new Central Portal has its own release flow (you can choose in the portal UI or set it per-publish). For a first release, leaving
it as the default (requires manual confirmation in the portal) is the safest choice. If you want fully automatic release (no manual step at all), pass automaticRelease =
true. For 1.0.0, manual confirmation in the portal UI is recommended so you can double-check the artifacts.

  ---
Step 4 — GPG signing key setup (~15 minutes)

GPG is required by Maven Central to sign every file you publish. This proves the artifact is genuinely from you.

4a. Check if you already have a GPG key:
gpg --list-secret-keys --keyid-format LONG
If the output is empty, you have no key. Generate one:

4b. Generate a new GPG key:
gpg --full-generate-key
When prompted:
- Key type: 1 (RSA and RSA)
- Key size: 4096
- Expiration: 0 (does not expire) — acceptable for a signing key you control
- Real name: Abhijith Shambu
- Email: shambu.abhi007@gmail.com
- Comment: leave blank
- Passphrase: choose a strong passphrase and save it in your password manager — you will need it again

4c. Get your key ID:
gpg --list-secret-keys --keyid-format LONG
Output looks like:
sec   rsa4096/ABCDEF1234567890 2026-05-06 [SC]
Your key ID is the ABCDEF1234567890 part after the slash.

4d. Publish the public key to a keyserver (Maven Central validates signatures against public keyservers):
gpg --keyserver keyserver.ubuntu.com --send-keys ABCDEF1234567890

4e. Export the private key for GitHub Secrets:
gpg --armor --export-secret-keys ABCDEF1234567890
Copy the entire output including -----BEGIN PGP PRIVATE KEY BLOCK----- and -----END PGP PRIVATE KEY BLOCK-----. This is your GPG_PRIVATE_KEY secret.

  ---
Step 5 — Configure GitHub Secrets (5 minutes)

1. Go to https://github.com/AbhijithShambu/compose-scrollbar-modifier
2. Click Settings → Secrets and variables → Actions
3. Click New repository secret for each of these four secrets:

┌─────────────────┬───────────────────────────────────────────────────┐
│   Secret Name   │              Where to find the value              │
├─────────────────┼───────────────────────────────────────────────────┤
│ OSSRH_USERNAME  │ The "Username" from step 2c (portal user token)   │
├─────────────────┼───────────────────────────────────────────────────┤
│ OSSRH_PASSWORD  │ The "Password" from step 2c (portal user token)   │
├─────────────────┼───────────────────────────────────────────────────┤
│ GPG_PRIVATE_KEY │ The full output of step 4e (the entire PGP block) │
├─────────────────┼───────────────────────────────────────────────────┤
│ GPG_PASSPHRASE  │ The passphrase you chose in step 4b               │
└─────────────────┴───────────────────────────────────────────────────┘

  ---
Step 6 — Create the CI publish workflow

Create the file .github/workflows/publish.yml:

name: Publish to Maven Central

on:
push:
tags:
- 'v[0-9]+.[0-9]+.[0-9]+'

jobs:
publish:
runs-on: macos-14   # macOS required for iOS framework compilation
timeout-minutes: 60

      steps:
        - name: Checkout repository
          uses: actions/checkout@v4

        - name: Set up Java 17
          uses: actions/setup-java@v4
          with:
            distribution: corretto
            java-version: '17'

        - name: Cache Gradle
          uses: actions/cache@v4
          with:
            path: |
              ~/.gradle/caches
              ~/.gradle/wrapper
            key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle.kts', '**/libs.versions.toml') }}
            restore-keys: |
              ${{ runner.os }}-gradle-

        - name: Extract version from tag
          id: version
          # strips the leading 'v': tag v1.0.0 becomes LIBRARY_VERSION=1.0.0
          run: echo "LIBRARY_VERSION=${GITHUB_REF_NAME#v}" >> "$GITHUB_OUTPUT"

        - name: Run desktop tests
          run: ./gradlew :lib:desktopTest --no-daemon

        - name: Run iOS simulator tests
          run: ./gradlew :lib:iosSimulatorArm64Test --no-daemon

        - name: Publish to Maven Central
          env:
            LIBRARY_VERSION: ${{ steps.version.outputs.LIBRARY_VERSION }}
            ORG_GRADLE_PROJECT_mavenCentralUsername: ${{ secrets.OSSRH_USERNAME }}
            ORG_GRADLE_PROJECT_mavenCentralPassword: ${{ secrets.OSSRH_PASSWORD }}
            ORG_GRADLE_PROJECT_signingInMemoryKey: ${{ secrets.GPG_PRIVATE_KEY }}
            ORG_GRADLE_PROJECT_signingInMemoryKeyPassword: ${{ secrets.GPG_PASSPHRASE }}
          run: ./gradlew publishAllPublicationsToMavenCentralRepository --no-daemon

A few important points about this workflow:

- macos-14 is used because iOS target compilation (iosX64, iosArm64, iosSimulatorArm64) requires Xcode, which is only on macOS runners. If you used ubuntu, the iOS
  publications would fail to build and the entire publish would fail.
- The ORG_GRADLE_PROJECT_ prefix is how the vanniktech plugin reads credentials: it maps Gradle project properties set via environment variables. This is the plugin's
  documented approach for CI.
- signingInMemoryKey tells the plugin to use in-memory GPG signing (the key never touches disk on the runner), which is the secure approach for CI. You do not need to import
  the key into a keyring.
- Tests run before publish. If a test fails, the publish step never runs.
- The LIBRARY_VERSION env var flows into the System.getenv("LIBRARY_VERSION") call in lib/build.gradle.kts.

Update the existing build.yml while you are at it

Change the OS matrix from macos-12 (EOL) to macos-14, update action versions from v3 to v4, and remove the stale feature/ci_support branch:

name: Build Multiplatform project
on:
push:
branches:
- main
pull_request:

jobs:
build-multiplatform-project:
strategy:
fail-fast: false
matrix:
os: [ubuntu-latest, macos-14, windows-latest]
runs-on: ${{ matrix.os }}
steps:
- uses: actions/checkout@v4
- uses: actions/setup-java@v4
with:
distribution: corretto
java-version: '17'
- name: Build Multiplatform project
shell: bash
run: ./gradlew assemble --no-daemon

Similarly update code_style.yml to use actions/checkout@v4 and actions/setup-java@v4.

  ---
Step 7 — Update the README Installation section

Replace the placeholder block in README.md:

## Installation

Add the dependency to your `build.gradle.kts`:

[![Maven Central](https://img.shields.io/maven-central/v/io.github.abhijithshambu/compose-scrollbar-modifier)](https://central.sonatype.com/artifact/io.github.abhijithshambu/compose-scro
llbar-modifier)

  ```kotlin
  // Kotlin DSL (build.gradle.kts)
  implementation("io.github.abhijithshambu:compose-scrollbar-modifier:1.0.0")

  For Groovy DSL (build.gradle):

  implementation 'io.github.abhijithshambu:compose-scrollbar-modifier:1.0.0'

  Make sure mavenCentral() is in your project's repository list:

  repositories {
      mavenCentral()
  }

  Also add the badge to the very top of the README, right after the `# Compose Scrollbar Modifier` heading:
```
  [![Maven Central](https://img.shields.io/maven-central/v/io.github.abhijithshambu/compose-scrollbar-modifier?label=Maven%20Central)](https://central.sonatype.com/artifact/com.shamb
  u.compose/compose-scrollbar-modifier)
  [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE.txt)

  ---
  Step 8 — Local smoke test before tagging

  Before creating the git tag, verify publishing locally:

  ./gradlew :lib:publishToMavenLocal

  This should complete without errors and produce artifacts in ~/.m2/repository/com/shambu/compose/compose-scrollbar-modifier/1.0.0/.

  Check that these files exist in that directory:
  - compose-scrollbar-modifier-1.0.0.pom
  - compose-scrollbar-modifier-1.0.0-kotlin-tooling-metadata.json
  - compose-scrollbar-modifier-android-1.0.0.aar
  - compose-scrollbar-modifier-desktop-1.0.0.jar
  - Various -iosArm64-, -iosX64-, -iosSimulatorArm64- klibs

  If any of these are missing, there is a build configuration problem to fix before tagging.

  To do a full signing smoke test locally, run:
  ./gradlew :lib:publishToMavenLocal \
    -Psigning.gnupg.keyId=ABCDEF1234567890 \
    -Psigning.gnupg.passphrase=YOUR_PASSPHRASE
  And verify that .asc signature files appear alongside every artifact.

  ---
  Step 9 — Tag and release

  Once every step above is done and committed to main:

  git tag -a v1.0.0 -m "Release 1.0.0 - Initial public release"
  git push origin v1.0.0

  Watch the Actions tab at https://github.com/AbhijithShambu/compose-scrollbar-modifier/actions. The publish workflow triggers. It takes roughly 15–20 minutes on macos-14 for
  the full KMP build + test + publish.

  When the workflow completes successfully, log into https://central.sonatype.com, go to Deployments, and you will see a pending deployment. Review the artifact list to confirm
   all platforms are present, then click Publish. Propagation to the searchable Maven Central index takes 10–30 minutes.

  ---
  Step 10 — Post-release

  1. Create a GitHub Release: go to https://github.com/AbhijithShambu/compose-scrollbar-modifier/releases/new, select tag v1.0.0, paste the CHANGELOG.md content as the release
  notes, check "Set as the latest release", click Publish.
  2. Verify on Maven Central: search https://central.sonatype.com/search?q=io.github.abhijithshambu after 15–30 minutes. Confirm the artifact appears. Click through to verify the POM
   metadata, Javadoc jar, and sources jar are all present.
  3. Prepare for 1.0.1: update gradle/libs.versions.toml library-version to "1.0.1-SNAPSHOT" on main to signal active post-release development. When you are ready to do the
  version upgrades (Kotlin, CMP, Dokka, AGP), do them on main, run all tests, update the changelog, bump to "1.0.1", and push tag v1.0.1.

  ---
  Summary of Every File Change Required

  ┌──────────────────────────────────────────────────────────────────────────────────────────────────┬───────────────────────────────────────────────────────────────────────┐
  │                                               File                                               │                                Change                                 │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ gradle/libs.versions.toml                                                                        │ android-minSdk → "24", add library-version = "1.0.0"                  │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ gradle.properties                                                                                │ Remove the 6 stale version pin lines                                  │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │                                                                                                  │ Version reads env var with catalog fallback, fix POM description      │
  │ lib/build.gradle.kts                                                                             │ typo, SonatypeHost.S01 → SonatypeHost.CENTRAL_PORTAL, update plugin   │
  │                                                                                                  │ version to 0.30.0                                                     │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/ScrollbarModifierOld.kt                   │ Delete entirely                                                       │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/commonMain/kotlin/PlatformName.kt                                                        │ Delete (move to commonTest)                                           │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/androidMain/kotlin/getPlatformName.kt                                                    │ Delete (move to androidTest)                                          │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/desktopMain/kotlin/getPlatformName.kt                                                    │ Delete (move to desktopTest)                                          │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/iosMain/kotlin/getPlatformName.kt                                                        │ Delete (move to iosTest)                                              │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/commonTest/kotlin/PlatformName.kt                                                        │ Create (moved from commonMain)                                        │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/androidUnitTest/kotlin/getPlatformName.kt                                                │ Create (moved from androidMain)                                       │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/desktopTest/kotlin/getPlatformName.kt                                                    │ Create (moved from desktopMain)                                       │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/iosTest/kotlin/getPlatformName.kt                                                        │ Create (moved from iosMain)                                           │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/ScrollbarModifier.kt                      │ class DefaultScrollbarLayoutScope → internal class                    │
  │                                                                                                  │ DefaultScrollbarLayoutScope                                           │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/foundation/ScrollbarSemanticProperties.kt │ object Keys → internal object Keys                                    │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/foundation/Utils.kt                       │ Add KDoc to applyPadding and isTransparent                            │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ README.md                                                                                        │ Fix vertical scrollbar copy error, replace Installation placeholder   │
  │                                                                                                  │ with real coordinates and badge                                       │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ CHANGELOG.md                                                                                     │ Create at repo root                                                   │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ .github/workflows/publish.yml                                                                    │ Create                                                                │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ .github/workflows/build.yml                                                                      │ Update OS matrix, action versions, remove stale branch                │
  ├──────────────────────────────────────────────────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────┤
  │ .github/workflows/code_style.yml                                                                 │ Update action versions to v4                                          │
  └──────────────────────────────────────────────────────────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────┘

  Critical Files for Implementation

  - /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/lib/build.gradle.kts
  - /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/gradle/libs.versions.toml
  - /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/ScrollbarModifier.kt
  - /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/lib/src/commonMain/kotlin/io/github/abhijithshambu/scrollbar/foundation/ScrollbarSemanticProperties.kt
  - /Users/abhishambu/AndroidStudioProjects/compose-scrollbar-modifier/.github/workflows/publish.yml