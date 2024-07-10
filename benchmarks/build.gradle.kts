
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlinx.benchmark")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    jvmToolchain(21)

    macosArm64()

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.11")
            }
        }

        val macosArm64Main by getting {
            dependencies {
                implementation(project(":functions"))
            }
        }
    }
}

benchmark {
    targets {
        register("macosArm64")
    }
}
