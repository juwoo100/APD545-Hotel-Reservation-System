package ca.seneca.application.service;

import ca.seneca.application.enums.AdminRole;
import ca.seneca.application.enums.ReservationStatus;
import ca.seneca.application.model.Reservation;
import ca.seneca.application.repository.GuestRepository;
import ca.seneca.application.repository.PaymentRepository;
import ca.seneca.application.repository.ReservationRepository;
import ca.seneca.application.repository.RoomRepository;
import ca.seneca.application.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class ReservationAdminService {
    private final ReservationRepository reservationRepository;
    private final GuestRepository  guestRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final DiscountService discountService;
    private final LoyaltyService  loyaltyService;

    public ReservationAdminService
            (ReservationRepository reservationRepository,
             GuestRepository guestRepository,
             RoomRepository roomRepository,
             PaymentRepository paymentRepository,
             DiscountService discountService,
             LoyaltyService  loyaltyService) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.paymentRepository = paymentRepository;
        this.discountService = discountService;
        this.loyaltyService = loyaltyService;
    }

    public Reservation findReservationById(Integer reservationId) {
        if(reservationId == null) {
            throw new IllegalArgumentException("Reservation id is required.");
        }
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Reservation reservation = reservationRepository.findById(em, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }
            return reservation;
        } finally {
            em.close();
        }
    }

    public List<Reservation> searchReservations(String guestName, String phone, ReservationStatus status) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return reservationRepository.search(em, guestName, phone, status);
        } finally {
            em.close();
        }
    }

    public void cancelReservation(Integer reservationId, String actor) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Reservation reservation = reservationRepository.findById(em, reservationId);

            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                throw new IllegalArgumentException("Reservation is already cancelled.");
            }

            if (reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
                throw new IllegalArgumentException("Checked-out reservation cannot be cancelled.");
            }
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.update(em, reservation);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Reservation updateReservationBasic(Integer reservationId, LocalDate checkIn, LocalDate checkOut, String specialRequest) {
        validateDateRange(checkIn, checkOut);

        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Reservation reservation = reservationRepository.findById(em, reservationId);

            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            boolean hasConflict = reservationRepository.hasConflict(em, reservationId, checkIn, checkOut);
            if (hasConflict) {
                throw new IllegalArgumentException("The selected dates conflict with an existing booking.");
            }

            reservation.setCheckInDate(checkIn);
            reservation.setCheckOutDate(checkOut);
            reservation.setSpecialRequest(specialRequest);

            Reservation updatedReservation = reservationRepository.update(em, reservation);

            em.getTransaction().commit();
            return updatedReservation;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private void validateDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null){
            throw new IllegalArgumentException("Check-in or check-out dates are required.");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
    }

    public Reservation applyDiscount(Integer reservationId, double discountPercent, AdminRole role) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Reservation reservation = reservationRepository.findById(em, reservationId);

            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            double originalTotal = reservation.getTotalAmount();
            double discountedTotal = discountService.applyDiscount(originalTotal, discountPercent, role);
            double discountAmount = originalTotal - discountedTotal;

            reservation.setDiscountPercent(discountPercent);
            reservation.setDiscountAmount(discountAmount);
            reservation.setTotalAmount(discountedTotal);

            Reservation updated = reservationRepository.update(em, reservation);

            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Reservation redeemLoyaltyPoints(Integer reservationId, int pointsToRedeem) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Reservation reservation = reservationRepository.findById(em, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            if (reservation.getGuest() == null) {
                throw new IllegalArgumentException("Guest not found.");
            }

            double redemptionAmount = loyaltyService.redeemPoints(reservation.getGuest(), pointsToRedeem, "Redeemed for reservation #" + reservationId);

            double currentTotal = reservation.getTotalAmount() == null ? 0.0 : reservation.getTotalAmount();

            if (redemptionAmount > currentTotal) {
                throw new IllegalArgumentException("Redemption amount cannot exceed current reservation total.");
            }
            double currentDiscountAmount = reservation.getDiscountAmount() == null ? 0.0 : reservation.getDiscountAmount();

            reservation.setDiscountAmount(currentDiscountAmount + redemptionAmount);
            reservation.setTotalAmount(currentTotal - redemptionAmount);

            Reservation updated = reservationRepository.update(em, reservation);

            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public int earnLoyaltyPointsForPayment(Integer reservationId, double paidAmount) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Reservation reservation = reservationRepository.findById(em, reservationId);

            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            if (reservation.getGuest() == null) {
                throw new IllegalArgumentException("Guest not found.");
            }

            return loyaltyService.earnPoints(reservation.getGuest(), paidAmount, "Earned from payment for reservation #" + reservationId);
        } finally {
            em.close();
        }
    }
}
