plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.dokka") version "1.8.10"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":common"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:2.0.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    implementation("io.insert-koin:koin-core:3.3.3")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    //implementation("org.slf4j:slf4j-api:1.7.30")
    //implementation("ch.qos.logback:logback-classic:1.2.9")
    //implementation("ch.qos.logback:logback-core:1.2.9")

    //implementation(group = "org.apache.logging.log4j", name = "log4j-api", version = "2.13.1")
    //implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.17.1")

    implementation(platform("io.projectreactor:reactor-bom:2022.0.6"))
    implementation("io.projectreactor:reactor-core")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}

tasks.withType<Jar>{
    manifest {
        attributes["Main-Class"] = "MainKt"
        attributes["Multi-Release"] = true
        //attributes["Class-Path"] = "../../lib/*.jar"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}