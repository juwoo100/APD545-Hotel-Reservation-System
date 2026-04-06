package com.hotel.service;

import com.hotel.logging.AppLogger;
import com.hotel.model.*;
import com.hotel.model.enums.*;
import com.hotel.observer.AdminNotificationObserver;
import com.hotel.observer.RoomAvailabilitySubject;
import com.hotel.repository.*;
import com.hotel.service.decorator.*;
import com.hotel.service.strategy.WeekendPricingStrategy;
import com.hotel.viewmodel.BookingDraft;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Core booking / CRUD service.
 * Uses: Decorator (add-ons), Strategy (pricing), Observer (waitlist notification).
 */
public class ReservationService {

    private static final Logger log = AppLogger.get(ReservationService.class);
    private static final double TAX_RATE = 0.13;

    private static ReservationService instance;

    private final ReservationRepository resRepo       = new ReservationRepository();
    private final GuestRepository guestRepo           = new GuestRepository();
    private final RoomRepository roomRepo             = new RoomRepository();
    private final WaitlistRepository waitlistRepo     = new WaitlistRepository();
    private final ActivityLogRepository actLogRepo    = new ActivityLogRepository();
    private final WeekendPricingStrategy pricing      = new WeekendPricingStrategy();
    private final RoomAvailabilitySubject subject     = new RoomAvailabilitySubject();

    private ReservationService() {
        subject.addObserver(new AdminNotificationObserver());
    }

    public static synchronized ReservationService getInstance() {
        if (instance == null) instance = new ReservationService();
        return instance;
    }

    // ── CREATE from kiosk BookingDraft ─────────────────────────────────────

    public ReservationEntity createFromDraft(BookingDraft draft) {
        // Find or create guest
        GuestEntity guest = guestRepo.findByPhone(draft.getPhone())
            .orElseGet(() -> {
                GuestEntity g = new GuestEntity();
                g.setFirstName(draft.getFirstName()); g.setLastName(draft.getLastName());
                g.setPhone(draft.getPhone()); g.setEmail(draft.getEmail());
                g.setAddress(draft.getAddress()); g.setCity(draft.getCity()); g.setCountry(draft.getCountry());
                return guestRepo.persist(g);
            });

        // Pick a room of the chosen type
        List<RoomEntity> available = roomRepo.findAvailableByType(
            com.hotel.model.enums.RoomType.valueOf(draft.getRoomType().toUpperCase()));
        if (available.isEmpty()) throw new IllegalStateException("No rooms of type " + draft.getRoomType() + " available.");

        RoomEntity room = available.get(0);

        // Build bill using Decorator pattern
        long nights = draft.getNights();
        BillComponent bill = new BaseBill(
            pricing.calculateNightlyTotal(room.getBasePrice(), draft.getRoomQuantity(),
                draft.getCheckInDate(), draft.getCheckOutDate()),
            draft.getRoomQuantity() + "x " + room.getRoomType() + " × " + nights + " nights");

        if (draft.getAddOns().contains("WiFi"))      bill = new WifiDecorator(bill);
        if (draft.getAddOns().contains("Breakfast")) bill = new BreakfastDecorator(bill, nights);
        if (draft.getAddOns().contains("Parking"))   bill = new ParkingDecorator(bill, nights);
        if (draft.getAddOns().contains("Spa"))       bill = new SpaDecorator(bill);

        double subtotal = bill.getTotal();
        double tax      = subtotal * TAX_RATE;
        double total    = subtotal + tax;

        ReservationEntity res = new ReservationEntity();
        res.setGuest(guest);
        res.setRoom(room);
        res.setCheckInDate(draft.getCheckInDate());
        res.setCheckOutDate(draft.getCheckOutDate());
        res.setRoomSubtotal(subtotal);
        res.setAddOnTotal(draft.getAddOns().isEmpty() ? 0 : subtotal - pricing.calculateNightlyTotal(room.getBasePrice(), draft.getRoomQuantity(), draft.getCheckInDate(), draft.getCheckOutDate()));
        res.setTax(tax);
        res.setTotalAmount(total);
        res.setAddOns(draft.getAddOns().isEmpty() ? null : String.join(",", draft.getAddOns()));
        res.setStatus(ReservationStatus.CONFIRMED);

        room.setStatus(RoomStatus.OCCUPIED);
        roomRepo.save(room);

        ReservationEntity saved = resRepo.persist(res);
        actLogRepo.log("CREATE_RESERVATION", "KIOSK", "Res#" + saved.getId() + " Guest=" + guest.getFullName());
        log.info("Reservation created: #" + saved.getId());
        return saved;
    }

    // ── ADMIN CRUD ─────────────────────────────────────────────────────────

    public List<ReservationEntity> getAll() { return resRepo.findAll(); }
    public List<ReservationEntity> searchByName(String name) { return resRepo.findByGuestName(name); }
    public List<ReservationEntity> getByStatus(ReservationStatus status) { return resRepo.findByStatus(status); }
    public List<ReservationEntity> getByDateRange(LocalDate from, LocalDate to) { return resRepo.findByDateRange(from, to); }

    public ReservationEntity applyDiscount(Long resId, double pct, String adminUser) {
        ReservationEntity res = resRepo.findById(resId).orElseThrow();
        double discountAmt = res.getRoomSubtotal() * (pct / 100.0);
        double newTotal    = (res.getRoomSubtotal() - discountAmt + res.getAddOnTotal()) * (1 + TAX_RATE);
        res.setDiscountAmount(discountAmt);
        res.setTax((res.getRoomSubtotal() - discountAmt + res.getAddOnTotal()) * TAX_RATE);
        res.setTotalAmount(newTotal);
        ReservationEntity updated = resRepo.merge(res);
        actLogRepo.log("APPLY_DISCOUNT", adminUser, "Res#" + resId + " pct=" + pct + "% amt=$" + String.format("%.2f", discountAmt));
        return updated;
    }

    public ReservationEntity recordPayment(Long resId, double amount, PaymentMethod method, String adminUser) {
        ReservationEntity res = resRepo.findById(resId).orElseThrow();
        res.setPaidAmount(res.getPaidAmount() + amount);
        res.setPaymentMethod(method);
        ReservationEntity updated = resRepo.merge(res);
        actLogRepo.log("PAYMENT", adminUser, "Res#" + resId + " amount=$" + String.format("%.2f", amount) + " method=" + method);
        return updated;
    }

    public ReservationEntity checkIn(Long resId, String adminUser) {
        ReservationEntity res = resRepo.findById(resId).orElseThrow();
        res.setStatus(ReservationStatus.CHECKED_IN);
        res.getRoom().setStatus(RoomStatus.OCCUPIED);
        roomRepo.save(res.getRoom());
        actLogRepo.log("CHECK_IN", adminUser, "Res#" + resId);
        return resRepo.merge(res);
    }

    public ReservationEntity checkOut(Long resId, String adminUser) {
        ReservationEntity res = resRepo.findById(resId).orElseThrow();
        if (res.getBalance() > 0.01) throw new IllegalStateException("Outstanding balance: $" + String.format("%.2f", res.getBalance()));
        res.setStatus(ReservationStatus.CHECKED_OUT);
        // Free the room and fire waitlist observer
        RoomEntity room = res.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepo.save(room);
        // Award loyalty points (1 pt per dollar)
        GuestEntity guest = res.getGuest();
        guest.addLoyaltyPoints((int) res.getTotalAmount());
        if (!guest.isLoyaltyMember()) { guest.setLoyaltyMember(true); guest.setLoyaltyNumber("LYL-" + guest.getId()); }
        guestRepo.save(guest);
        actLogRepo.log("CHECK_OUT", adminUser, "Res#" + resId + " points+" + (int) res.getTotalAmount());
        // Notify observer
        List<com.hotel.model.WaitlistEntry> waitlist = new com.hotel.repository.WaitlistRepository().findByType(room.getRoomType());
        subject.notifyRoomAvailable(room, waitlist);
        return resRepo.merge(res);
    }

    public ReservationEntity cancel(Long resId, String adminUser) {
        ReservationEntity res = resRepo.findById(resId).orElseThrow();
        res.setStatus(ReservationStatus.CANCELLED);
        res.getRoom().setStatus(RoomStatus.AVAILABLE);
        roomRepo.save(res.getRoom());
        actLogRepo.log("CANCEL", adminUser, "Res#" + resId);
        List<com.hotel.model.WaitlistEntry> waitlist = new com.hotel.repository.WaitlistRepository().findByType(res.getRoom().getRoomType());
        subject.notifyRoomAvailable(res.getRoom(), waitlist);
        return resRepo.merge(res);
    }

    // ── LOYALTY ────────────────────────────────────────────────────────────

    public List<GuestEntity> getLoyaltyMembers() {
        return new GuestRepository().findAll().stream()
            .filter(GuestEntity::isLoyaltyMember).toList();
    }
}
