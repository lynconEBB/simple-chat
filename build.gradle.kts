plugins {
    java
}

group = "dev.lyncon"
version = "1.0-SNAPSHOT"
val lwjglVersion = "3.3.2"
val lwjglNatives = "natives-windows"
val imguiVersion = "1.86.10"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.spair:imgui-java-app:${imguiVersion}")
}