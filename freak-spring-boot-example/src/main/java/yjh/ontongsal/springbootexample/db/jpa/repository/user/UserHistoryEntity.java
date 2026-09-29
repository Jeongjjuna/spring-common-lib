package yjh.ontongsal.springbootexample.db.jpa.repository.user;

import jakarta.persistence.*;

@Entity
@Table(name = "user_history")
public class UserHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String action;

    protected UserHistoryEntity() {
    }

    public static UserHistoryEntity create(Long userId, String action) {
        var entity = new UserHistoryEntity();
        entity.userId = userId;
        entity.action = action;
        return entity;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }
}
