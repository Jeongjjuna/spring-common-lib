package yjh.ontongsal.common.http;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;


public class HttpClientBeanDefinitionRegistrar implements BeanDefinitionRegistryPostProcessor, BeanFactoryAware {

    private static final Logger log = LoggerFactory.getLogger(HttpClientBeanDefinitionRegistrar.class);

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) {
        var packages = AutoConfigurationPackages.get(beanFactory);
        var scanner = buildScanner();

        for (var basePackage : packages) {
            for (var candidate : scanner.findCandidateComponents(basePackage)) {
                var clientType = resolveClass(candidate.getBeanClassName());
                var annotation = clientType.getAnnotation(HttpClient.class);
                var beanName = AnnotationBeanNameGenerator.INSTANCE.generateBeanName(candidate, registry);
                registerFactoryBean(registry, clientType, annotation.value(), beanName);
                log.debug("HttpClient 등록: {} -> clientName={}", clientType.getSimpleName(), annotation.value());
            }
        }
    }

    private ClassPathScanningCandidateComponentProvider buildScanner() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface();
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(HttpClient.class));
        return scanner;
    }

    private void registerFactoryBean(BeanDefinitionRegistry registry, Class<?> clientType, String clientName, String beanName) {
        var beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(HttpClientFactoryBean.class)
                .addConstructorArgValue(clientType)
                .addConstructorArgValue(clientName)
                .getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private Class<?> resolveClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("@HttpInterface 인터페이스 로드 실패: " + className, e);
        }
    }
}
