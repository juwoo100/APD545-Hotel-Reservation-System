package com.hotel.repository;

import com.hotel.db.Database;
import com.hotel.model.ActivityLog;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ActivityLogRepository {
    private final Database db = Database.getInstance();

    public void log(String action, String by, String details) {
        EntityManager em = db.createEntityManager();
        try { em.getTransaction().begin();
              em.persist(new ActivityLog(action, by, details));
              em.getTransaction().commit(); }
        catch (Exception e) { em.getTransaction().rollback(); throw e; }
        finally { em.close(); }
    }

    public List<ActivityLog> findAll() {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery(
            "SELECT l FROM ActivityLog l ORDER BY l.timestamp DESC", ActivityLog.class)
            .setMaxResults(500).getResultList(); }
        finally { em.close(); }
    }
}
