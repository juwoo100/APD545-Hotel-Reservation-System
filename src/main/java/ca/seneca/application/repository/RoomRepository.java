package com.hotel.repository;

import com.hotel.db.Database;
import com.hotel.model.RoomEntity;
import com.hotel.model.enums.RoomStatus;
import com.hotel.model.enums.RoomType;
import jakarta.persistence.EntityManager;
import java.util.List;

public class RoomRepository {
    private final Database db = Database.getInstance();

    public void save(RoomEntity r) {
        EntityManager em = db.createEntityManager();
        try { em.getTransaction().begin();
              if (r.getId() == null) em.persist(r); else em.merge(r);
              em.getTransaction().commit(); }
        catch (Exception e) { em.getTransaction().rollback(); throw e; }
        finally { em.close(); }
    }

    public List<RoomEntity> findAll() {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery("SELECT r FROM RoomEntity r ORDER BY r.roomNumber", RoomEntity.class).getResultList(); }
        finally { em.close(); }
    }

    public List<RoomEntity> findAvailableByType(RoomType type) {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery(
            "SELECT r FROM RoomEntity r WHERE r.roomType=:t AND r.status='AVAILABLE'", RoomEntity.class)
            .setParameter("t", type).getResultList(); }
        finally { em.close(); }
    }

    public long countByType(RoomType type) {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery("SELECT COUNT(r) FROM RoomEntity r WHERE r.roomType=:t", Long.class)
            .setParameter("t", type).getSingleResult(); }
        finally { em.close(); }
    }

    public RoomEntity findById(Long id) {
        EntityManager em = db.createEntityManager();
        try { return em.find(RoomEntity.class, id); } finally { em.close(); }
    }
}
