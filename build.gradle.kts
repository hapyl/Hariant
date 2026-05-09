plugins {
    `java-library`
    `maven-publish`

    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")

    maven {
        url = uri("https://maven.pkg.github.com/hapyl/EternaAPI")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")

    implementation("org.mongodb:mongo-java-driver:3.12.12")
    implementation("me.hapyl:eternaapi:6.2.3-SNAPSHOT")
}

group = "me.hapyl"
version = "1.0.0-SNAPSHOT"
description = "Hariant"

java {
    java.sourceCompatibility = JavaVersion.VERSION_25
    java.targetCompatibility = JavaVersion.VERSION_25
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    // Silence javadocs
    withType<Javadoc> {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
        isFailOnError = false
    }

    // Copy version from project to `plugin.yml`, because gradle can't do
    // that automatically ¯\_(ツ)_/¯
    named<ProcessResources>("processResources") {
        filteringCharset = "UTF-8"
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(sourceSets.main.get().resources.srcDirs) {
            include("plugin.yml")
            expand("version" to project.version)
        }
    }

    runServer {
        minecraftVersion("26.1.2")
    }
}


