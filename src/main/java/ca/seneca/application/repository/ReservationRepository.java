package ca.seneca.application.repository;

import ca.seneca.application.enums.ReservationStatus;
import ca.seneca.application.model.Reservation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class ReservationRepository {

    public Reservation save(EntityManager em, Reservation reservation) {
        if (reservation.getReservationId() == null) {
            em.persist(reservation);
            return reservation;
        }
        return em.merge(reservation);
    }

    public Reservation findById(EntityManager em, Integer id) {
        return em.find(Reservation.class, id);
    }

    public List<Reservation> search(EntityManager em, String guestName, String phone, ReservationStatus status) {
        StringBuilder query = getStringBuilder(guestName, phone, status);

        TypedQuery<Reservation> typedQuery = em.createQuery(query.toString(), Reservation.class);

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
    }

    private static StringBuilder getStringBuilder(String guestName, String phone, ReservationStatus status) {
        StringBuilder query = new StringBuilder("""
                SELECT r FROM Reservation r
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
        return query;
    }

    public Reservation update(EntityManager em, Reservation reservation) {
        return em.merge(reservation);
    }

    public boolean hasConflict(EntityManager em, Integer reservationId, LocalDate checkIn, LocalDate checkOut) {
        Long count = em.createQuery("""
                SELECT COUNT(r) FROM Reservation r
                WHERE r.reservationId <> :reservationId
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
    }
}