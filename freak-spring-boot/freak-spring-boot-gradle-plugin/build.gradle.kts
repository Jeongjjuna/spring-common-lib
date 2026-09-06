plugins {
    id("java-gradle-plugin")

    // 배포 방법 : ./gradlew :freak-spring-boot-gradle-plugin:publishToMavenLocal
    id("maven-publish") // (임시) 로컬 배포를 위한 플러그인
}

group = "yjh.ontongsal"
version = "1.0.0"

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

// (임시) 로컬 배포를 위함
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"]) // ~/.m2/repository
        }
    }
}