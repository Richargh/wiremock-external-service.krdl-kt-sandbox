import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    kotlin("jvm") version "1.4.20"
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    mavenCentral()
}

dependencies {
    /** Main dependencies **/
    implementation(gradleApi())

    /** Test dependencies **/
    // none
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
