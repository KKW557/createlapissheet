import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.fabric.loom)
}

version = "2026.3.1"
group = "icu.suc"

base {
    archivesName.set("createlapissheet")
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()
    accessWidenerPath = file("src/main/resources/createlapissheet.accesswidener")
    mods {
        register("createlapissheet") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
    maven { url = uri("https://maven.shedaniel.me") }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)
    modImplementation(libs.fabric.api)
    modImplementation(libs.create)
    modImplementation(libs.jei)
    modCompileOnly(libs.rei)
}

tasks.processResources {
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft" to libs.versions.minecraft.get(),
            "loader" to libs.versions.fabric.loader.get(),
            "kotlin" to libs.versions.fabric.kotlin.get()
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}
