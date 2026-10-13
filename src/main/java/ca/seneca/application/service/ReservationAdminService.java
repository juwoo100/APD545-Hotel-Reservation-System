package ca.seneca.application.service;

import ca.seneca.application.model.*;
import ca.seneca.application.model.enums.*;
import ca.seneca.application.repository.ReservationRepository;
import ca.seneca.application.repository.RoomRepository;
import ca.seneca.application.repository.WaitlistRepository;
import ca.seneca.application.util.JpaUtil;
import jakarta.persistence.EntityManager;
import ca.seneca.application.observer.RoomAvailabilitySubject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationAdminService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final DiscountService discountService;
    private final PaymentService paymentService;
    private final LoyaltyService loyaltyService;
    private final WaitlistRepository  waitlistRepository;
    private final RoomAvailabilitySubject roomAvailabilitySubject = new  RoomAvailabilitySubject();

    public ReservationAdminService(
            ReservationRepository reservationRepository,
            RoomRepository roomRepository,
            DiscountService discountService,
            PaymentService paymentService,
            LoyaltyService loyaltyService,
            WaitlistRepository waitlistRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.discountService = discountService;
        this.paymentService = paymentService;
        this.loyaltyService = loyaltyService;
        this.waitlistRepository = waitlistRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> searchByGuestName(String name) {
        return reservationRepository.findByGuestName(name);
    }

    public List<Reservation> searchByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    public List<Reservation> searchByDateRange(LocalDate from, LocalDate to) {
        return reservationRepository.findByDateRange(from, to);
    }

    public Reservation applyDiscount(Integer reservationId, double discountPercent, Admin admin) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Reservation reservation = em.find(Reservation.class, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }
            if (admin == null || admin.getRole() == null) {
                throw new IllegalArgumentException("Admin role is required.");
            }

            discountService.applyDiscountToReservation(reservation, discountPercent, admin.getRole());
            em.merge(reservation);

            em.getTransaction().commit();
            return reservation;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Payment recordPayment(Integer reservationId, Admin admin, double amount, PaymentMethod method, String note) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));
        return paymentService.processPayment(reservation, admin, amount, method, note);
    }

    public double redeemLoyaltyPoints(Integer reservationId, int points, String note) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));
        return loyaltyService.redeemPointsForReservation(reservation, points, note);
    }

    public int earnLoyaltyPoints(Integer reservationId, double paidAmount, String note) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));
        return loyaltyService.earnPointsFromReservation(reservation, paidAmount, note);
    }

    public Reservation cancelReservation(Integer reservationId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Reservation reservation = em.find(Reservation.class, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            reservation.setStatus(ReservationStatus.CANCELLED);

            for (var rr : reservation.getReservationRooms()) {
                RoomEntity room = rr.getRoom();
                room.setStatus(RoomAvailabilityStatus.AVAILABLE);
                em.merge(room);

                // observer trigger
                List<WaitlistEntry> waitlistEntries = waitlistRepository.findByType(room.getRoomType());
                roomAvailabilitySubject.notifyRoomAvailable(room, waitlistEntries);
            }

            em.merge(reservation);
            em.getTransaction().commit();
            return reservation;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Reservation checkOutReservation(Integer reservationId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Reservation reservation = em.find(Reservation.class, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            double paid = 0.0;
            for (var payment : reservation.getPayments()) {
                if (payment.getAmount() != null) {
                    paid += payment.getAmount();
                }
            }

            if (reservation.getTotalAmount() == null || paid < reservation.getTotalAmount()) {
                throw new IllegalStateException("Cannot check out with outstanding balance.");
            }

            reservation.setStatus(ReservationStatus.CHECKED_OUT);
            reservation.setPaymentStatus(PaymentStatus.PAID);

            for (var rr : reservation.getReservationRooms()) {
                RoomEntity room = rr.getRoom();
                room.setStatus(RoomAvailabilityStatus.AVAILABLE);
                em.merge(room);

                // observer trigger
                List<WaitlistEntry> waitlistEntries = waitlistRepository.findByType(room.getRoomType());
                roomAvailabilitySubject.notifyRoomAvailable(room, waitlistEntries);
            }

            em.merge(reservation);
            em.getTransaction().commit();
            return reservation;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
