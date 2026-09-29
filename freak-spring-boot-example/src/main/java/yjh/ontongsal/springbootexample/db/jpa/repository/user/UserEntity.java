package yjh.ontongsal.springbootexample.db.jpa.repository.user;

import jakarta.persistence.*;

@Entity
@Table(name = "user_accounts")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    protected UserEntity() {
    }

    private UserEntity(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public static UserEntity create(String username, String email) {
        return new UserEntity(username, email);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
