plugins {
    id("java-library")
    id("maven-publish")
}

group = "yjh.ontongsal"
version = providers.gradleProperty("release.version").get()

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))

    api(project(":freak-spring-boot-starter-domain"))

    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework:spring-web")

    implementation("org.slf4j:slf4j-api")
    implementation("org.apache.httpcomponents.client5:httpclient5")

    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}