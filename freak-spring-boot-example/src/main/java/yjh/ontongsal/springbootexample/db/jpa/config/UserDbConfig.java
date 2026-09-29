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
        basePackages = UserDbConfig.REPOSITORY_PACKAGE,
        entityManagerFactoryRef = "userEntityManagerFactory",
        transactionManagerRef = "userTransactionManager"
)
@EnableTransactionManagement
@Configuration
public class UserDbConfig {

    private static final String ENTITY_PACKAGE = "yjh.ontongsal.springbootexample.db.jpa.repository.user";
    private static final String PERSISTENCE_UNIT = "user-db";

    static final String REPOSITORY_PACKAGE = "yjh.ontongsal.springbootexample.db.jpa.repository.user";

    @ConfigurationProperties(prefix = "spring.datasource.user-db")
    @Bean
    public CommonDataSourceProperties userDataSourceProperties() {
        return new CommonDataSourceProperties();
    }

    @Bean
    public DataSource userDataSource(
            @Qualifier("userDataSourceProperties") CommonDataSourceProperties dataSourceProperties
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
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(
            @Qualifier("userDataSource") DataSource dataSource,
            @Qualifier("userDataSourceProperties") CommonDataSourceProperties dataSourceProperties,
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
    public PlatformTransactionManager userTransactionManager(
            @Qualifier("userEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public TransactionProvider userTransactionProvider(
            @Qualifier("userTransactionManager") PlatformTransactionManager transactionManager
    ) {
        return new TransactionProvider(transactionManager);
    }
}