package com.hotel.repository;

import com.hotel.db.Database;
import com.hotel.model.ReservationEntity;
import com.hotel.model.enums.ReservationStatus;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ReservationRepository {
    private final Database db = Database.getInstance();

    public ReservationEntity persist(ReservationEntity r) {
        EntityManager em = db.createEntityManager();
        try { em.getTransaction().begin(); em.persist(r);
              em.getTransaction().commit(); return r; }
        catch (Exception e) { em.getTransaction().rollback(); throw e; }
        finally { em.close(); }
    }

    public ReservationEntity merge(ReservationEntity r) {
        EntityManager em = db.createEntityManager();
        try { em.getTransaction().begin(); ReservationEntity result = em.merge(r);
              em.getTransaction().commit(); return result; }
        catch (Exception e) { em.getTransaction().rollback(); throw e; }
        finally { em.close(); }
    }

    public Optional<ReservationEntity> findById(Long id) {
        EntityManager em = db.createEntityManager();
        try { return Optional.ofNullable(em.find(ReservationEntity.class, id)); }
        finally { em.close(); }
    }

    public List<ReservationEntity> findAll() {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery(
            "SELECT r FROM ReservationEntity r JOIN FETCH r.guest ORDER BY r.checkInDate DESC",
            ReservationEntity.class).getResultList(); }
        finally { em.close(); }
    }

    public List<ReservationEntity> findByStatus(ReservationStatus status) {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery(
            "SELECT r FROM ReservationEntity r JOIN FETCH r.guest WHERE r.status=:s",
            ReservationEntity.class).setParameter("s", status).getResultList(); }
        finally { em.close(); }
    }

    public List<ReservationEntity> findByGuestName(String name) {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery(
            "SELECT r FROM ReservationEntity r JOIN FETCH r.guest g " +
            "WHERE LOWER(g.firstName) LIKE :n OR LOWER(g.lastName) LIKE :n ORDER BY r.checkInDate DESC",
            ReservationEntity.class).setParameter("n", "%" + name.toLowerCase() + "%").getResultList(); }
        finally { em.close(); }
    }

    public List<ReservationEntity> findByDateRange(LocalDate from, LocalDate to) {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery(
            "SELECT r FROM ReservationEntity r JOIN FETCH r.guest " +
            "WHERE r.checkInDate >= :from AND r.checkInDate <= :to ORDER BY r.checkInDate",
            ReservationEntity.class).setParameter("from", from).setParameter("to", to).getResultList(); }
        finally { em.close(); }
    }
}
