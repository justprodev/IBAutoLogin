plugins {
    kotlin("jvm") version "2.4.0"
    id("com.gradleup.shadow") version "9.0.0-beta8"
}

group = "com.justprodev.trading"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.slf4j:slf4j-api:2.0.17")

    // Source: https://mvnrepository.com/artifact/com.microsoft/credential-secure-storage
    implementation("com.microsoft:credential-secure-storage:1.0.3")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.14.0")
    testImplementation("org.slf4j:slf4j-api:2.0.17")
    // Source: https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    testImplementation("ch.qos.logback:logback-classic:1.5.38")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.desktop/java.awt=ALL-UNNAMED",
        "--add-opens", "java.desktop/javax.swing=ALL-UNNAMED",
        "--add-opens", "java.desktop/java.awt.event=ALL-UNNAMED"
    )
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes(
            "Premain-Class" to "com.justprodev.trading.ib.IBAutoLoginAgent",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true"
        )
    }
    // Resolve duplicate file conflicts: take the first one
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}