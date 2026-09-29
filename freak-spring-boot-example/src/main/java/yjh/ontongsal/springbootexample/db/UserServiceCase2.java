package yjh.ontongsal.springbootexample.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import yjh.ontongsal.common.db.jpa.TransactionProvider;
import yjh.ontongsal.common.domain.exception.BusinessException;
import yjh.ontongsal.common.domain.exception.CommonResultCode;
import yjh.ontongsal.springbootexample.db.jpa.repository.root.MaintenanceRepository;
import yjh.ontongsal.springbootexample.db.jpa.repository.user.UserEntity;
import yjh.ontongsal.springbootexample.db.jpa.repository.user.UserHistoryEntity;
import yjh.ontongsal.springbootexample.db.jpa.repository.user.UserHistoryRepository;
import yjh.ontongsal.springbootexample.db.jpa.repository.user.UserRepository;

import java.util.List;

@Service
public class UserServiceCase2 {

    private static final Logger log = LoggerFactory.getLogger(UserServiceCase2.class);

    private final MaintenanceRepository maintenanceRepository;
    private final TransactionProvider rootTransaction;

    private final UserRepository userRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final TransactionProvider userTransaction;

    public UserServiceCase2(
            MaintenanceRepository maintenanceRepository,
            @Qualifier("rootTransactionProvider") TransactionProvider rootTransaction,

            UserRepository userRepository,
            UserHistoryRepository userHistoryRepository,
            @Qualifier("userTransactionProvider") TransactionProvider userTransaction
    ) {
        this.maintenanceRepository = maintenanceRepository;
        this.rootTransaction = rootTransaction;

        this.userRepository = userRepository;
        this.userHistoryRepository = userHistoryRepository;
        this.userTransaction = userTransaction;
    }

    public UserEntity create() {
        // 1. DB1
        var maintenance = maintenanceRepository.findById(999L)
                .orElseThrow(() -> new BusinessException(CommonResultCode.INTERNAL_SERVER_ERROR));
        if (maintenance.isActive()) {
            throw new BusinessException(CommonResultCode.INTERNAL_SERVER_ERROR, "점검 진행중...");
        }

        // 2. DB2
        UserEntity createdUser = userTransaction.run(() -> {
            var user = userRepository.save(UserEntity.create("test-user", "test@example.com"));
            userHistoryRepository.save(UserHistoryEntity.create(user.getId(), "USER_CREATED"));
            return user;
        });
        log.info("created user : {}", createdUser.getEmail());

        // 3. Event 퍼블리시
        // var userCreatedEvent = UserCreatedEvent.of(createdUser)
        // eventPublisher.publish(userCreatedEvent);

        return createdUser;
    }

    // 단일 연산의 경우, JpaRepository 구현체에 @Transactioinal 이 적용되어있기때문에 사용하지 않아도 무관.
    public UserEntity get(Long id) {
        // return userRepository.findById(id).orElseThrow();
        return userTransaction.run(() ->
                userRepository.findById(id).orElseThrow()
        );
    }

    public void delete(Long id) {
        userTransaction.run(() ->
                userRepository.deleteById(id)
        );
    }

    /**
     * read only (read only DB 조회)
     */
    public UserEntity getReadOnly(Long id) {
        return userTransaction.readOnly(() -> userRepository.findById(id).orElseThrow());
    }

    public List<UserEntity> getReadOnly() {
        return userTransaction.readOnly(() -> userRepository.findAll());
    }
}
