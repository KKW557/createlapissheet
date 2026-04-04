plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.fabric.loom)
}

loom {
    splitEnvironmentSourceSets()
    accessWidenerPath = project.file("src/main/resources/createlapissheet.accesswidener")
    mods {
        register("createlapissheet") {
            sourceSet("main")
            sourceSet("client")
        }
    }
    sourceSets {
        main {
            resources {
                srcDir(project(":createlapissheet-common").sourceSets.main.get().resources.srcDirs)
            }
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

    inputs.property("version", project.version)
    inputs.property("minecraft", libs.versions.minecraft.get())
    inputs.property("loader", libs.versions.fabric.loader.get())
    inputs.property("kotlin", libs.versions.fabric.kotlin.get())
    inputs.property("create", libs.versions.create.get())

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft" to libs.versions.minecraft.get(),
            "loader" to libs.versions.fabric.loader.get(),
            "kotlin" to libs.versions.fabric.kotlin.get(),
            "create" to libs.versions.create.get()
        )
    }
}