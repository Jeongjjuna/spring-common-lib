plugins {
    id("java-gradle-plugin")
}

group = "yjh.ontongsal"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:4.1.1")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.7")
}

gradlePlugin {
    plugins {
        create("freakSpringBoot") {
            id = "yjh.ontongsal.spring-boot"
            implementationClass = "yjh.ontongsal.gradle.FreakSpringBootPlugin"
        }
    }
}