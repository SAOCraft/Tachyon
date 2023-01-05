plugins {
    kotlin("jvm") version "1.7.21"
    id("java-library")
}

group = "net.swordcraft.server"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    api("com.github.Minestom.Minestom:Minestom:-SNAPSHOT")
    api("dev.dejvokep:boosted-yaml:1.3")
    api("org.mongodb:mongodb-driver-sync:4.8.1")
    api("net.kyori:adventure-text-minimessage:4.12.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    jar {
        manifest {
            attributes(
                "Main-Class" to "net.swordcraft.server.Tachyon"
            )
        }
    }
}
