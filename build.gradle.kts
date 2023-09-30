plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

group = "kr.cosine.library"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.spigotmc", "spigot", "1.13.2-R0.1-SNAPSHOT")

    api("com.zaxxer", "HikariCP", "5.0.1")

    api("org.jetbrains.kotlin", "kotlin-stdlib", "1.8.0")
    api("org.jetbrains.kotlin", "kotlin-reflect", "1.8.0")
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.4")

    api("org.jetbrains.exposed", "exposed-core", "0.41.1")
    api("org.jetbrains.exposed", "exposed-dao", "0.41.1")
    api("org.jetbrains.exposed", "exposed-jdbc", "0.41.1")
    api("org.jetbrains.exposed", "exposed-java-time", "0.41.1")

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
        destinationDirectory.set(file("D:\\서버\\1.20.1 - 개발\\plugins"))
    }
    /*shadowJar {
        relocate("kotlin.reflect", "kr.cosine.kotlin.reflect")
        relocate("kotlin", "kr.cosine.kotlin")
        destinationDirectory.set(file("D:\\서버\\1.20.1 - 개발\\plugins"))
    }*/
}

/*configurations.runtimeClasspath.get().apply {
    exclude("com.zaxxer")
    exclude("org.spongepowered")
    exclude("org.yaml")
    exclude("org.jetbrains.kotlin")
    exclude("org.jetbrains.kotlinx")
    exclude("org.jetbrains.exposed")
    exclude("org.jetbrains.slf4j")
}*/