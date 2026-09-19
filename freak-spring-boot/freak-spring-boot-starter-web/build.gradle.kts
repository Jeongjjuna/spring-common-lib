plugins {
    id("java-library")

    // 배포 방법 : ./gradlew :freak-spring-boot-starter:publishToMavenLocal
    id("maven-publish") // (임시) 로컬 배포를 위한 플러그인
}

group = "yjh.ontongsal"
version = providers.gradleProperty("release.version").get()

repositories {
    mavenCentral()
}

dependencies {
    // "이 BOM이 정해놓은 의존성 버전 규칙을 현재 프로젝트에 적용하겠다"는 의미
    // https://docs.gradle.org/current/userguide/platforms.html?utm_source=chatgpt.com
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))

    api(project(":freak-spring-boot-starter-domain"))

    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0")

    // starter web 구현 내부에서만 직접 사용
    implementation("tools.jackson.core:jackson-databind")

    // JSON 구조화 로깅 (logback XML 에서만 참조)
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:8.0")

    // Swagger 커스텀을 위한 내부에서만 직접 사용
    implementation("io.github.classgraph:classgraph:4.8.194")
    implementation("org.ow2.asm:asm:9.8")
    implementation("org.ow2.asm:asm-tree:9.8")

    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testAnnotationProcessor(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// (임시) 로컬 배포를 위함
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}