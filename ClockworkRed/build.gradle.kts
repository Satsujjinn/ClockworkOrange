plugins {
    id("com.android.application") version "8.1.1" apply false
    id("com.android.library") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.1" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
