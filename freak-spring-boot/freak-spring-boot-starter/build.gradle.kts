plugins {
    id("java-library")

    // 배포 방법 : ./gradlew :freak-spring-boot-starter:publishToMavenLocal
    id("maven-publish") // (임시) 로컬 배포를 위한 플러그인
}

group = "yjh.ontongsal"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // "이 BOM이 정해놓은 의존성 버전 규칙을 현재 프로젝트에 적용하겠다"는 의미
    // https://docs.gradle.org/current/userguide/platforms.html?utm_source=chatgpt.com
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

// (임시) 로컬 배포를 위함
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}