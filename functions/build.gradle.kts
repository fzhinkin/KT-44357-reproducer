plugins {
    kotlin("multiplatform")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)

    macosArm64 {
        compilations.getByName("main") {
            cinterops {
                val libc by creating {
                    defFile(project.file("src/macosArm64Main/kotlin/interop.def"))
                    packageName("libc")
                }
            }
        }
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
}
