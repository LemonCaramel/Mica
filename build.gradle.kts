import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.compileOnlyPlatformDependency(notation: String) {
    add("compileOnly", notation) {
        isTransitive = false
    }
}

plugins {
    id("java")
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
}

base.archivesName = "${rootProject.name.lowercase()}-mc${property("minecraft_version")}-universal"
version = property("mod_version") as String
group = "moe.caramel"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.fabricmc.net/") } // Fabric
    maven { url = uri("https://maven.isxander.dev/releases") } // (common) yacl
    maven { url = uri("https://maven.terraformersmc.com/") } // (fabric) mod-menu
    maven { url = uri("https://maven.minecraftforge.net/") } // Forge
    maven { url = uri("https://maven.neoforged.net/releases") } // NeoForge
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    compileOnly("net.minecraftforge:forge:${property("minecraft_version")}-${property("forge_version")}:universal")
    compileOnlyPlatformDependency("net.minecraftforge:fmlcore:${property("minecraft_version")}-${property("forge_version")}")
    compileOnlyPlatformDependency("net.minecraftforge:fmlloader:${property("minecraft_version")}-${property("forge_version")}")
    compileOnlyPlatformDependency("net.minecraftforge:javafmllanguage:${property("minecraft_version")}-${property("forge_version")}")
    compileOnlyPlatformDependency("net.minecraftforge:eventbus:${property("forge_eventbus_version")}")

    compileOnly("net.neoforged:neoforge:${property("neoforge_version")}:universal")
    compileOnlyPlatformDependency("net.neoforged.fancymodloader:loader:${property("neoforge_fml_version")}")

    implementation("dev.isxander:yet-another-config-lib:${property("yet_another_config_lib_version")}-fabric")
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
