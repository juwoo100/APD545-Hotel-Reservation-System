package ca.seneca.application.model;

import ca.seneca.application.model.enums.AdminRole;
import jakarta.persistence.*;

@Entity
@Table(name = "admin_users")
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) private String username;
    @Column(nullable = false) private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false) private AdminRole role;
    @Column(nullable = false) private String fullName;

    public AdminUser() {}
    public AdminUser(String username, String passwordHash, AdminRole role, String fullName) {
        this.username = username; this.passwordHash = passwordHash;
        this.role = role; this.fullName = fullName;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
    public AdminRole getRole() { return role; }
    public void setRole(AdminRole v) { this.role = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
}
