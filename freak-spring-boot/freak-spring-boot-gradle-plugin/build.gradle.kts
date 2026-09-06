plugins {
    id("java-gradle-plugin")
}

group = "yjh.ontongsal"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("freakSpringBoot") {
            id = "yjh.ontongsal.spring-boot"
            implementationClass = "yjh.ontongsal.gradle.FreakSpringBootPlugin"
        }
    }
}