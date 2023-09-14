plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

allprojects {
    group = "kr.cosine.library"
    version = "1.0.0"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    val subName = name
    val buildableProject = subName.contains("bukkit") || subName.contains("proxy")

    apply {
        plugin("org.gradle.maven-publish")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.github.johnrengelman.shadow")
    }

    dependencies {
        api("org.yaml", "snakeyaml", "2.0")
        api("org.spongepowered", "configurate-yaml", "4.1.2")

        api("org.jetbrains.kotlin", "kotlin-stdlib", "1.8.0")
        api("org.jetbrains.kotlin", "kotlin-reflect", "1.8.0")
        api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.4")

        api("org.jetbrains.exposed", "exposed-core", "0.41.1")
        api("org.jetbrains.exposed", "exposed-dao", "0.41.1")
        api("org.jetbrains.exposed", "exposed-jdbc", "0.41.1")
        api("org.jetbrains.exposed", "exposed-java-time", "0.41.1")

        testImplementation(kotlin("test"))
    }

    if (buildableProject) {
        dependencies {
            api(project(":core"))
        }
        configurations.runtimeClasspath.get().apply {
            exclude("org.jetbrains.kotlin")
            exclude("org.jetbrains.kotlinx")
            exclude("org.jetbrains.exposed")
            exclude("org.jetbrains.slf4j")
            exclude("org.spongepowered")
            exclude("org.yaml")
        }
        tasks {
            shadowJar {
                val path = if (subName == "bukkit") "로비1" else "프록시"
                archiveFileName.set("${rootProject.name}-$subName-${rootProject.version}.jar")
                destinationDirectory.set(file("D:\\서버\\1.20.1 - 번지\\$path\\plugins"))
            }
        }
    }

    tasks.test {
        useJUnitPlatform()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = this@subprojects.name
                version = rootProject.version.toString()

                from(components["java"])
            }
        }
    }
}

/*
repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc", "spigot", "1.12.2-R0.1-SNAPSHOT")

    api("org.jetbrains.kotlin", "kotlin-stdlib", "1.8.0")
    api("org.jetbrains.kotlin", "kotlin-reflect", "1.8.0")
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.4")

    api("org.jetbrains.exposed:exposed-core:0.41.1")
    api("org.jetbrains.exposed:exposed-dao:0.41.1")
    api("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    api("org.jetbrains.exposed:exposed-java-time:0.41.1")

    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()

            from(components["java"])
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
    jar {
        archiveFileName.set("${rootProject.name}-${project.version}.jar")
        destinationDirectory.set(File("D:\\서버\\1.20.1 - 개발\\plugins"))
    }
    shadowJar {
        relocate("kotlin.reflect", "kr.cosine.kotlin.reflect")
        relocate("kotlin", "kr.cosine.kotlin")
    }
}*/
