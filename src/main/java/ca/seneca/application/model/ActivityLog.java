package ca.seneca.application.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String action;
    @Column private String performedBy;
    @Column(length = 2000) private String details;
    @Column(nullable = false) private LocalDateTime timestamp = LocalDateTime.now();

    public ActivityLog() {}
    public ActivityLog(String action, String performedBy, String details) {
        this.action = action; this.performedBy = performedBy; this.details = details;
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public String getPerformedBy() { return performedBy; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
