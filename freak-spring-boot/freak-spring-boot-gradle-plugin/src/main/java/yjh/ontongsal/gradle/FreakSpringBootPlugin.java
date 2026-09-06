package yjh.ontongsal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jspecify.annotations.NonNull;

public class FreakSpringBootPlugin implements Plugin<Project> {

    @Override
    public void apply(@NonNull Project project) {
        // Java
        project.getPluginManager().apply("java");

        // Spring Boot
        project.getPluginManager().apply("org.springframework.boot");

        // Dependency Management
        project.getPluginManager().apply("io.spring.dependency-management");

        // Freak SpringBoot Starter
        project.getDependencies().add(
                "implementation",
                "yjh.ontongsal:freak-spring-boot-starter:1.0.0"
        );

        // Test
        project.getDependencies().add(
                "testImplementation",
                "org.springframework.boot:spring-boot-starter-test"
        );

        project.getDependencies().add(
                "testRuntimeOnly",
                "org.junit.platform:junit-platform-launcher"
        );

        project.getExtensions()
                .configure(JavaPluginExtension.class, java -> {
                    java.getToolchain()
                            .getLanguageVersion()
                            .set(JavaLanguageVersion.of(25));
                });

        project.getTasks()
                .withType(Test.class)
                .configureEach(Test::useJUnitPlatform);
    }
}
