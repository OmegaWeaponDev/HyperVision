import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.omegaweapondev"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(files("${projectDir}/libs/OmegaLibs-1.0.0.jar"))

    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("com.tchristofferson:ConfigUpdater:2.0-SNAPSHOT")
    compileOnly("org.bstats:bstats-bukkit:3.0.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(17)
}

tasks.withType<ProcessResources> {
    filteringCharset = Charsets.UTF_8.name()
}

tasks.withType<ShadowJar> {
    listOf("com.tchristofferson",
           "org.bstats",
           "me.omegaweapondev.omegalibs",
           "dev.dbassett",
           "com.zaxxer"
    ).forEach { relocate(it, "me.omegaweapondev.hypervision.libs.$it") }

    archiveFileName.set("HyperVision-${project.version}.jar")
}