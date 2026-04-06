package com.hotel.repository;

import com.hotel.db.Database;
import com.hotel.model.ReservationEntity;
import com.hotel.model.enums.ReservationStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

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

    public List<ReservationEntity> search(String guestName, String phone, ReservationStatus status) {
        EntityManager em = db.createEntityManager();
        try {
            StringBuilder query = new StringBuilder("""
                SELECT r FROM ReservationEntity r
                JOIN r.guest g
                WHERE 1=1
                """);

            if (guestName != null && !guestName.isBlank()) {
                query.append(" AND LOWER(CONCAT(g.firstName, ' ', g.lastName)) LIKE :guestName");
            }
            if (phone != null && !phone.isBlank()) {
                query.append(" AND g.phone LIKE :phone");
            }
            if (status != null) {
                query.append(" AND r.status = :status");
            }
            query.append(" ORDER BY r.checkInDate DESC");

            TypedQuery<ReservationEntity> typedQuery =
                    em.createQuery(query.toString(), ReservationEntity.class);

            if (guestName != null && !guestName.isBlank()) {
                typedQuery.setParameter("guestName", "%" + guestName.toLowerCase() + "%");
            }
            if (phone != null && !phone.isBlank()) {
                typedQuery.setParameter("phone", "%" + phone + "%");
            }
            if (status != null) {
                typedQuery.setParameter("status", status);
            }

            return typedQuery.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean hasConflict(Long reservationId, LocalDate checkIn, LocalDate checkOut) {
        EntityManager em = db.createEntityManager();
        try {
            Long count = em.createQuery("""
                SELECT COUNT(r) FROM ReservationEntity r
                WHERE r.id <> :reservationId
                  AND r.status <> :cancelledStatus
                  AND r.checkInDate < :checkOut
                  AND r.checkOutDate > :checkIn
                """, Long.class)
                    .setParameter("reservationId", reservationId)
                    .setParameter("cancelledStatus", ReservationStatus.CANCELLED)
                    .setParameter("checkIn", checkIn)
                    .setParameter("checkOut", checkOut)
                    .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }
}