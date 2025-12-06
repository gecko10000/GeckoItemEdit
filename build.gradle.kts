plugins {
    id("java")
    id("de.eldoria.plugin-yml.bukkit") version "0.6.0"
}

sourceSets {
    main {
        java {
            srcDir("src")
        }
        resources {
            srcDir("res")
        }
    }
}

group = "gecko10000.geckoitemedit"
version = "0.1"

bukkit {
    name = "GeckoItemEdit"
    main = "$group.$name"
    apiVersion = "1.13"
    depend = listOf("GeckoLib")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-public/")
    mavenLocal()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    compileOnly("gecko10000.geckolib:GeckoLib:1.1")
    compileOnly("net.strokkur", "commands-annotations", "1.5.0")
    annotationProcessor("net.strokkur", "commands-processor", "1.5.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}


tasks.register("update") {
    dependsOn(tasks.build)
    doLast {
        exec {
            workingDir(".")
            commandLine("../../dot/local/bin/update.sh")
        }
    }
}
