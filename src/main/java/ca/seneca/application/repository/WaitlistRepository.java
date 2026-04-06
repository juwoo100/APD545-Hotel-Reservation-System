package com.hotel.repository;

import com.hotel.db.Database;
import com.hotel.model.WaitlistEntry;
import com.hotel.model.enums.RoomType;
import jakarta.persistence.EntityManager;
import java.util.List;

public class WaitlistRepository {
    private final Database db = Database.getInstance();

    public WaitlistEntry persist(WaitlistEntry e) {
        EntityManager em = db.createEntityManager();
        try { em.getTransaction().begin(); em.persist(e);
              em.getTransaction().commit(); return e; }
        catch (Exception ex) { em.getTransaction().rollback(); throw ex; }
        finally { em.close(); }
    }

    public WaitlistEntry merge(WaitlistEntry e) {
        EntityManager em = db.createEntityManager();
        try { em.getTransaction().begin(); WaitlistEntry r = em.merge(e);
              em.getTransaction().commit(); return r; }
        catch (Exception ex) { em.getTransaction().rollback(); throw ex; }
        finally { em.close(); }
    }

    public List<WaitlistEntry> findAll() {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery(
            "SELECT w FROM WaitlistEntry w JOIN FETCH w.guest ORDER BY w.addedAt",
            WaitlistEntry.class).getResultList(); }
        finally { em.close(); }
    }

    public List<WaitlistEntry> findByType(RoomType type) {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery(
            "SELECT w FROM WaitlistEntry w JOIN FETCH w.guest WHERE w.requestedType=:t ORDER BY w.addedAt",
            WaitlistEntry.class).setParameter("t", type).getResultList(); }
        finally { em.close(); }
    }
}
