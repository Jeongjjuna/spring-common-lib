package yjh.ontongsal.springbootexample.db.jpa.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import yjh.ontongsal.common.db.jpa.CommonDataSourceProperties;
import yjh.ontongsal.common.db.jpa.RoutingDataSource;
import yjh.ontongsal.common.db.jpa.TransactionProvider;

import javax.sql.DataSource;
import java.util.Map;

@EnableJpaRepositories(
        basePackages = RootDbConfig.REPOSITORY_PACKAGE,
        entityManagerFactoryRef = "rootEntityManagerFactory",
        transactionManagerRef = "rootTransactionManager"
)
@EnableTransactionManagement
@Configuration
public class RootDbConfig {

    private static final String ENTITY_PACKAGE = "yjh.ontongsal.springbootexample.db.jpa.repository.root";
    private static final String PERSISTENCE_UNIT = "root-db";

    static final String REPOSITORY_PACKAGE = "yjh.ontongsal.springbootexample.db.jpa.repository.root";

    @ConfigurationProperties(prefix = "spring.datasource.root-db")
    @Bean
    public CommonDataSourceProperties rootDataSourceProperties() {
        return new CommonDataSourceProperties();
    }

    @Bean
    public DataSource rootDataSource(
            @Qualifier("rootDataSourceProperties") CommonDataSourceProperties dataSourceProperties
    ) {
        DataSource write = dataSourceProperties.getWrite().toDataSource();
        DataSource read = dataSourceProperties.getRead().toDataSource();

        var routing = new RoutingDataSource();
        routing.setTargetDataSources(Map.of(
                RoutingDataSource.DataSourceType.WRITE, write,
                RoutingDataSource.DataSourceType.READ, read
        ));
        routing.setDefaultTargetDataSource(write);
        routing.afterPropertiesSet();
        return new LazyConnectionDataSourceProxy(routing);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean rootEntityManagerFactory(
            @Qualifier("rootDataSource") DataSource dataSource,
            @Qualifier("rootDataSourceProperties") CommonDataSourceProperties dataSourceProperties,
            JpaProperties jpaProperties
    ) {
        var vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());

        var builder = new EntityManagerFactoryBuilder(
                vendorAdapter, _ -> jpaProperties.getProperties(), null);

        return builder.dataSource(dataSource)
                .packages(ENTITY_PACKAGE)
                .persistenceUnit(PERSISTENCE_UNIT)
                .build();
    }

    @Bean
    public PlatformTransactionManager rootTransactionManager(
            @Qualifier("rootEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public TransactionProvider rootTransactionProvider(
            @Qualifier("rootTransactionManager") PlatformTransactionManager transactionManager
    ) {
        return new TransactionProvider(transactionManager);
    }
}
