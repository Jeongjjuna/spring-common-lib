package yjh.ontongsal.springbootexample.db;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yjh.ontongsal.springbootexample.db.jpa.repository.root.MaintenanceRepository;
import yjh.ontongsal.springbootexample.db.jpa.repository.user.UserEntity;
import yjh.ontongsal.springbootexample.db.jpa.repository.user.UserRepository;

import java.util.List;

@Service
public class UserServiceCase1 {

    private final MaintenanceRepository maintenanceRepository;
    private final UserRepository userRepository;

    public UserServiceCase1(MaintenanceRepository maintenanceRepository, UserRepository userRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.userRepository = userRepository;
    }

    @Transactional(transactionManager = "userTransactionManager")
    public UserEntity create() {
        return userRepository.save(
                UserEntity.create("test-user", "test@example.com")
        );
    }

    @Transactional(transactionManager = "userTransactionManager")
    public UserEntity get(Long id) {
        return userRepository.findById(id)
                .orElseThrow();
    }

    @Transactional(transactionManager = "userTransactionManager")
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * read only (read only DB 조회)
     */
    @Transactional(readOnly = true, transactionManager = "userTransactionManager")
    public UserEntity getReadOnly(Long id) {
        return userRepository.findById(id)
                .orElseThrow();
    }

    @Transactional(readOnly = true, transactionManager = "userTransactionManager")
    public List<UserEntity> getReadOnly() {
        return userRepository.findAll();
    }
}
