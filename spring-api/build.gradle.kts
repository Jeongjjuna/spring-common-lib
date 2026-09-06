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

dependencies {
    // 이미 공통 플러그인에서 관리해주는 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // 이 프로젝트에서만 독립적으로 추가할때
    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

//    implementation("org.springframework.boot:spring-boot-starter")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}