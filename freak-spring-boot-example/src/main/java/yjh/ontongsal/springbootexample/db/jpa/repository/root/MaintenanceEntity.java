package yjh.ontongsal.springbootexample.db.jpa.repository.root;

import jakarta.persistence.*;

@Entity
@Table(name = "maintenance")
public class MaintenanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private boolean active;

    protected MaintenanceEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isActive() {
        return active;
    }
}
