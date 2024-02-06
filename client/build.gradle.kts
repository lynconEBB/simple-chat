plugins {
    id("java")
}

group = "unioeste.sd"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {

    implementation("io.github.spair:imgui-java-app:1.86.11")
    implementation("org.apache.commons:commons-lang3:3.0")

    implementation("commons-codec:commons-codec:1.16.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":netlib"))

}

tasks.test {
    useJUnitPlatform()
}