plugins {
    kotlin("jvm") version "1.8.22"
    application
}

group = "org.app"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.haifengl:smile-core:2.6.0")
    implementation("com.github.haifengl:smile-data:2.6.0")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("org.app.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
