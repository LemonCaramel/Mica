import org.gradle.kotlin.dsl.expand
import org.gradle.kotlin.dsl.property

plugins {
    id("java")
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
}

base.archivesName = "${rootProject.name.lowercase()}-mc${property("minecraft_version")}-universal"
version = property("mod_version") as String
group = "moe.caramel"

allprojects {
    apply(plugin = "java")
    apply(plugin = "net.fabricmc.fabric-loom")

    repositories {
        maven { url = uri("https://maven.fabricmc.net/") } // Fabric
        mavenCentral()
    }

    dependencies {
        minecraft("com.mojang:minecraft:${property("minecraft_version")}")
        implementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    }
}

repositories {
    maven { url = uri("https://maven.isxander.dev/releases") } // (common) yacl
    maven { url = uri("https://maven.terraformersmc.com/") } // (fabric) mod-menu
}

dependencies {
    compileOnly(project(":api-forge"))
    compileOnly(project(":api-neoforge"))

    compileOnly("dev.isxander:yet-another-config-lib:${property("yet_another_config_lib_version")}-fabric")
    implementation("com.terraformersmc:modmenu:${property("mod_menu_version")}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        val props = mapOf(
            "version" to project.version,
            "mod_id" to rootProject.name.lowercase()
        )
        inputs.properties(props)

        filesMatching(listOf(
            "fabric.mod.json",
            "META-INF/mods.toml",
            "META-INF/neoforge.mods.toml"
        )) {
            expand(props)
        }
    }
}
