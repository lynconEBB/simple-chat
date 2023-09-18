plugins {
    id("java")
}

group = "unioeste.sd"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":netlib"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}