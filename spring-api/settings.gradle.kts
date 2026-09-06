// (임시) 로컬 빌드 다운로드를 위한 설정 추가
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "spring-api"
