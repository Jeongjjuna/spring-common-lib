package yjh.ontongsal.common.db.jpa;

import org.jspecify.annotations.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.function.Supplier;

public class TransactionProvider {
    private final TransactionTemplate write;
    private final TransactionTemplate read;

    public TransactionProvider(PlatformTransactionManager transactionManager) {
        this.write = new TransactionTemplate(transactionManager);
        this.read = new TransactionTemplate(transactionManager);
        this.read.setReadOnly(true);
    }

    public void run(Runnable action) {
        write.executeWithoutResult(_ -> action.run());
    }

    public <T extends @NonNull Object> T run(
            Supplier<T> action
    ) {
        return Objects.requireNonNull(
                write.execute(_ -> action.get()),
                "Transaction action must not return null"
        );
    }

    public void readOnly(Runnable action) {
        read.executeWithoutResult(_ -> action.run());
    }

    public <T extends @NonNull Object> T readOnly(Supplier<T> action) {
        return Objects.requireNonNull(
                read.execute(_ -> action.get()),
                "Read-only transaction action must not return null"
        );
    }
}
