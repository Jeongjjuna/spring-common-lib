package yjh.ontongsal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
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

        /**
         * Freak Extension
         * 아래와 같이 사용할 수 있다.
         *
         * freak {
         *     web = true
         *     redis = true
         * }
         */
        FreakExtension freak = project.getExtensions()
                .create("freak", FreakExtension.class);

        // Optional Dependencies
        Configuration implementation =
                project.getConfigurations().getByName("implementation");

        implementation.withDependencies(dependencies -> {

            if (freak.getWeb()) {
                dependencies.add(
                        project.getDependencies().create(
                                "org.springframework.boot:spring-boot-starter-webmvc"
                        )
                );
            }

            if (freak.getRedis()) {
                dependencies.add(
                        project.getDependencies().create(
                                "org.springframework.boot:spring-boot-starter-data-redis"
                        )
                );
            }
        });

        // Test
        project.getDependencies().add(
                "testImplementation",
                "org.springframework.boot:spring-boot-starter-test"
        );

        project.getDependencies().add(
                "testRuntimeOnly",
                "org.junit.platform:junit-platform-launcher"
        );

        // Java Toolchain
        project.getExtensions()
                .configure(JavaPluginExtension.class, java -> {
                    java.getToolchain()
                            .getLanguageVersion()
                            .set(JavaLanguageVersion.of(25));
                });

        // JUnit 5
        project.getTasks()
                .withType(Test.class)
                .configureEach(Test::useJUnitPlatform);
    }
}
