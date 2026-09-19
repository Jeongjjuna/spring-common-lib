plugins {
    id("yjh.ontongsal.spring-boot") version "1.0.0"
}

group = "yjh.ontongsal"
version = "0.0.1-SNAPSHOT"
description = "spring-api"

repositories {
    mavenLocal() //(임시) 로컬 starter repository 다운로드를 위한 추가
    mavenCentral()
}

freakDependencies {
    isWeb = true
    isDb = true
    isCache = true
    isHttpClient = true
}

dependencies {
    implementation("org.springframework.boot:spring-boot-h2console")
    runtimeOnly("com.h2database:h2")

    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}