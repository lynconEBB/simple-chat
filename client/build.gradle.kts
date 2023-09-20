plugins {
    id("java")
}

group = "unioeste.sd"
version = "unspecified"
val imguiVersion = "1.86.10"
val lwjglNatives = "natives-windows"

repositories {
    mavenCentral()
}

dependencies {

    implementation("io.github.spair:imgui-java-app:${imguiVersion}")

    runtimeOnly("org.lwjgl:lwjgl-nfd::$lwjglNatives")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":netlib"))
}

tasks.test {
    useJUnitPlatform()
}