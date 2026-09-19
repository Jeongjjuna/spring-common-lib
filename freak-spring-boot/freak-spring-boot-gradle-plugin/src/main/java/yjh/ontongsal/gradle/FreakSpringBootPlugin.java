package yjh.ontongsal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

public class FreakSpringBootPlugin implements Plugin<Project> {

    private static final String EXTENSION_NAME = "freakDependencies";

    @Override
    public void apply(@NonNull Project project) {
        boolean isRootWithSubprojects = project.equals(project.getRootProject())
                && !project.getSubprojects().isEmpty();
        if (isRootWithSubprojects) {
            // 멀티모듈 : 서브프로젝트 각각에 플러그인 전파
            project.getSubprojects().forEach(sub -> {
                sub.getPluginManager().apply(FreakSpringBootPlugin.class);
            });
        } else {
            applyConfiguration(project);
        }
    }

    private void applyConfiguration(@NonNull Project project) {
        var freakSpringBootVersion = "1.0.0";

        project.getPluginManager().apply(JavaPlugin.class);
        project.getPluginManager().apply(SpringBootPlugin.class);
        project.getPluginManager().apply("io.spring.dependency-management");

        /**
         * Freak Extension
         * 아래와 같이 사용할 수 있다.
         *
         * freak {
         *     web = true
         *     cache = true
         *     db = true
         * }
         */
        FreakExtension freakExtension = project.getExtensions().create(EXTENSION_NAME, FreakExtension.class);

        // Optional Dependencies
        Configuration implementation = project.getConfigurations().getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME);
        implementation.withDependencies(dependencies -> {
            if (freakExtension.isWeb()) {
                dependencies.add(project.getDependencies().create("yjh.ontongsal:freak-spring-boot-starter-web:" + freakSpringBootVersion));
            }
            if (freakExtension.isDb()) {
                dependencies.add(project.getDependencies().create("yjh.ontongsal:freak-spring-boot-starter-db:" + freakSpringBootVersion));
            }
            if (freakExtension.isCache()) {
                dependencies.add(project.getDependencies().create("yjh.ontongsal:freak-spring-boot-starter-cache:" + freakSpringBootVersion));
            }
            if (freakExtension.isHttpClient()) {
                dependencies.add(project.getDependencies().create("yjh.ontongsal:freak-spring-boot-starter-http-client:" + freakSpringBootVersion));
            }
        });

        // Test
        project.getDependencies().add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.springframework.boot:spring-boot-starter-test");
        project.getDependencies().add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, "org.junit.platform:junit-platform-launcher");

        // Java Toolchain(25)
        project.getExtensions()
                .configure(JavaPluginExtension.class, java -> {
                    java.getToolchain()
                            .getLanguageVersion()
                            .set(JavaLanguageVersion.of(25));
                });

        // JUnit
        project.getTasks()
                .withType(Test.class)
                .configureEach(Test::useJUnitPlatform);
    }
}
