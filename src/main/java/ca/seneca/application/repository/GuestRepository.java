package com.hotel.repository;

import com.hotel.db.Database;
import com.hotel.model.GuestEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class GuestRepository {
    private final Database db = Database.getInstance();

    public GuestEntity save(GuestEntity g) {
        EntityManager em = db.createEntityManager();
        try { em.getTransaction().begin();
              GuestEntity result = g.getId() == null ? g : em.merge(g);
              if (g.getId() == null) em.persist(result);
              em.getTransaction().commit(); return result; }
        catch (Exception e) { em.getTransaction().rollback(); throw e; }
        finally { em.close(); }
    }

    public GuestEntity persist(GuestEntity g) {
        EntityManager em = db.createEntityManager();
        try { em.getTransaction().begin(); em.persist(g);
              em.getTransaction().commit(); return g; }
        catch (Exception e) { em.getTransaction().rollback(); throw e; }
        finally { em.close(); }
    }

    public Optional<GuestEntity> findByPhone(String phone) {
        EntityManager em = db.createEntityManager();
        try { List<GuestEntity> r = em.createQuery(
            "SELECT g FROM GuestEntity g WHERE g.phone=:p", GuestEntity.class)
            .setParameter("p", phone).getResultList();
              return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0)); }
        finally { em.close(); }
    }

    public List<GuestEntity> findAll() {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery("SELECT g FROM GuestEntity g ORDER BY g.lastName", GuestEntity.class).getResultList(); }
        finally { em.close(); }
    }
}
