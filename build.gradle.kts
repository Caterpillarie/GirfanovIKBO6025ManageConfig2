plugins {
    kotlin("jvm") version "2.3.20"
    application
}

group = "org.depgravis"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
    main {
        kotlin.setSrcDirs(listOf("src"))
    }
    test {
        kotlin.setSrcDirs(listOf("tests"))
    }
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
