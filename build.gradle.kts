import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30" apply false
}

allprojects {
    group = "de.richargh.sandbox.wiremock.externalservices"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

}

subprojects {

    tasks.withType<Test> {
        useJUnitPlatform {
            excludeTags("excluded")
        }
        testLogging {
            showExceptions = true
            showStandardStreams = true
            events("passed", "skipped", "failed")
        }
    }
}

tasks.wrapper {
    gradleVersion = "6.8.2"
}
