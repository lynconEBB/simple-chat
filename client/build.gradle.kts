plugins {
    id("java")
}

group = "unioeste.sd"
version = "unspecified"
val imguiVersion = "1.86.10"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.spair:imgui-java-app:${imguiVersion}")
    implementation(project(":netlib"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}