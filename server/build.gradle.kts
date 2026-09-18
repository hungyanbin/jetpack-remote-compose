plugins {
    kotlin("jvm") version "2.3.20"
    application
}

dependencies {
    implementation("androidx.compose.remote:remote-creation-jvm:1.0.0-alpha19")

    implementation("io.ktor:ktor-server-core-jvm:3.6.0")
    implementation("io.ktor:ktor-server-netty-jvm:3.6.0")
}

application {
    mainClass.set("com.example.composeremote.server.MainKt")
}

kotlin {
    jvmToolchain(17)
}
