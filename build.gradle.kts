plugins {
    kotlin("jvm") version "1.7.21"
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.0"
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
    // Encoding utf-8
    shadowJar {
        archiveFileName.set("server.jar")
        manifest {
            attributes(
                "Main-Class" to "net.swordcraft.server.Tachyon"
            )
        }
    }

}

// Publishing to jitpack
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "net.swordcraft.server"
            artifactId = "Tachyon"
            version = "1.0"

            from(components["java"])
        }
    }
}