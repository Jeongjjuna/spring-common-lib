package yjh.ontongsal.common.db.jpa;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(RoutingDataSource.class);

    public enum DataSourceType {
        WRITE, READ
    }

    @Override
    protected @Nullable Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        DataSourceType type = isReadOnly ? DataSourceType.READ : DataSourceType.WRITE;
        log.debug("[DataSource Routing] type={} (readOnly={})", type, isReadOnly);
        return type;
    }
}
