package ca.seneca.application.service;

import ca.seneca.application.enums.PaymentStatus;
import ca.seneca.application.enums.ReservationStatus;
import ca.seneca.application.enums.RoomAvailabilityStatus;
import ca.seneca.application.model.AddOn;
import ca.seneca.application.model.Guest;
import ca.seneca.application.model.Reservation;
import ca.seneca.application.model.ReservationAddOn;
import ca.seneca.application.model.ReservationRoom;
import ca.seneca.application.model.Room;
import ca.seneca.application.repository.AddOnRepository;
import ca.seneca.application.repository.GuestRepository;
import ca.seneca.application.repository.ReservationRepository;
import ca.seneca.application.repository.RoomRepository;
import ca.seneca.application.util.JpaUtil;
import ca.seneca.application.viewmodel.BookingDraft;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingService {

    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final AddOnRepository addOnRepository;

    public BookingService(GuestRepository guestRepository, ReservationRepository reservationRepository, RoomRepository roomRepository, AddOnRepository addOnRepository) {
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.addOnRepository = addOnRepository;
    }

    public Reservation createBooking(
            Guest guest,
            List<Room> rooms,
            List<AddOn> addOns,
            LocalDate checkIn,
            LocalDate checkOut,
            int adults,
            int children,
            String specialRequest,
            PricingStrategy pricingStrategy
    ) {
        if (guest == null) {
            throw new IllegalArgumentException("Guest information is required.");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
        if (adults < 0 || children < 0) {
            throw new IllegalArgumentException("Guest counts cannot be negative.");
        }

        int totalGuests = adults + children;
        if (totalGuests <= 0) {
            throw new IllegalArgumentException("At least one guest is required.");
        }

        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("At least one room must be selected.");
        }

        if (pricingStrategy == null) {
            throw new IllegalArgumentException("Pricing strategy is required.");
        }

        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            if (guest.getGuestId() == null) {
                guest = guestRepository.save(em, guest);
            } else {
                guest = em.merge(guest);
            }

            Reservation reservation = new Reservation();
            reservation.setGuest(guest);
            reservation.setCheckInDate(checkIn);
            reservation.setCheckOutDate(checkOut);
            reservation.setAdults(adults);
            reservation.setChildren(children);
            reservation.setStatus(ReservationStatus.PENDING);
            reservation.setPaymentStatus(PaymentStatus.PENDING);
            reservation.setCreatedAt(LocalDateTime.now());
            reservation.setSpecialRequest(specialRequest);

            double subtotal = 0.0;
            int totalCapacity = 0;

            for (Room room : rooms) {
                Room managedRoom = em.find(Room.class, room.getRoomId());
                if (managedRoom == null) {
                    throw new IllegalArgumentException("Room not found: " + room.getRoomId());
                }

                if (managedRoom.getAvailabilityStatus() != RoomAvailabilityStatus.AVAILABLE) {
                    throw new IllegalArgumentException("Room is not available: " + managedRoom.getRoomNumber());
                }

                totalCapacity += managedRoom.getRoomType().getCapacity();

                ReservationRoom rr = new ReservationRoom();
                rr.setRoom(managedRoom);

                double nightlyRate = pricingStrategy.calculateNightlyRate(managedRoom.getRoomType().getBasePrice(), checkIn);

                double roomTotal = 0.0;
                LocalDate currentDate = checkIn;
                while (currentDate.isBefore(checkOut)) {
                    roomTotal += pricingStrategy.calculateNightlyRate(
                            managedRoom.getRoomType().getBasePrice(),
                            currentDate
                    );
                    currentDate = currentDate.plusDays(1);
                }

                rr.setNightlyRate(nightlyRate);
                rr.setLineTotal(roomTotal);
                rr.setGuestsAssigned(0);
                reservation.addReservationRoom(rr);

                subtotal += roomTotal;
            }

            if (totalCapacity < totalGuests) {
                throw new IllegalArgumentException("Selected rooms do not have enough capacity for all guests.");
            }

            if (addOns != null) {
                for (AddOn addOn : addOns) {
                    AddOn managedAddOn = em.find(AddOn.class, addOn.getAddOnId());
                    if (managedAddOn == null) {
                        throw new IllegalArgumentException("Add-on not found: " + addOn.getAddOnId());
                    }

                    ReservationAddOn ra = new ReservationAddOn();
                    ra.setAddOn(managedAddOn);
                    ra.setQuantity(1);
                    ra.setUnitPrice(managedAddOn.getBasePrice());
                    ra.setLineTotal(managedAddOn.getBasePrice());

                    reservation.addReservationAddOn(ra);
                    subtotal += ra.getLineTotal();
                }
            }

            double discountAmount = 0.0;
            double taxRate = 0.13;
            double taxAmount = subtotal * taxRate;
            double totalAmount = subtotal - discountAmount + taxAmount;

            reservation.setSubTotal(subtotal);
            reservation.setDiscountAmount(discountAmount);
            // reservation.setTaxAmount(taxAmount); // add this if your entity has the field
            reservation.setTotalAmount(totalAmount);

            reservation = reservationRepository.save(em, reservation);

            em.getTransaction().commit();
            return reservation;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public String completeBooking(BookingDraft draft) {
        if (draft == null) {
            throw new IllegalArgumentException("Booking draft cannot be null.");
        }

        Guest guest = new Guest();
        guest.setFirstName(draft.getFirstName());
        guest.setLastName(draft.getLastName());
        guest.setPhone(draft.getPhone());
        guest.setEmail(draft.getEmail());
        guest.setAddress(draft.getAddress());
        guest.setCity(draft.getCity());
        guest.setCountry(draft.getCountry());

        List<Room> rooms = resolveRoomsFromDraft(draft);
        List<AddOn> addOns = resolveAddOnsFromDraft(draft);

        PricingStrategy pricingStrategy = new StandardPricingStrategy();

        Reservation reservation = createBooking(
                guest,
                rooms,
                addOns,
                draft.getCheckInDate(),
                draft.getCheckOutDate(),
                draft.getAdults(),
                draft.getChildren(),
                null,
                pricingStrategy
        );

        return "RES-" + reservation.getReservationId();
    }

    private List<Room> resolveRoomsFromDraft(BookingDraft draft) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            List<Room> selectedRooms = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : draft.getRoomSelections().entrySet()) {
                String roomTypeName = entry.getKey();
                int quantity = entry.getValue();

                List<Room> availableRooms = roomRepository.findAvailableRoomsByTypeName(em, roomTypeName);

                if (availableRooms.size() < quantity) {
                    throw new IllegalArgumentException("Not enough available rooms for type: " + roomTypeName);
                }

                selectedRooms.addAll(availableRooms.subList(0, quantity));
            }

            return selectedRooms;
        } finally {
            em.close();
        }
    }

    private List<AddOn> resolveAddOnsFromDraft(BookingDraft draft) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            List<AddOn> result = new ArrayList<>();

            for (String addOnName : draft.getAddOns()) {
                AddOn addOn = addOnRepository.findByName(em, addOnName);
                if (addOn == null) {
                    throw new IllegalArgumentException("Add-on not found: " + addOnName);
                }
                result.add(addOn);
            }

            return result;
        } finally {
            em.close();
        }
    }
}