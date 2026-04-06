package com.hotel.viewmodel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds all data collected across the kiosk booking flow steps.
 * Passed between controllers via BookingSession (static shared state).
 */
public class BookingDraft {

    // Step 1: Guests
    private int adults;
    private int children;

    // Step 2: Dates
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // Step 3: Rooms — supports multiple types simultaneously
    private final Map<String, Integer> roomSelections = new LinkedHashMap<>();
    // Legacy single-type fields (kept for MockPricingService compatibility)
    private String roomType;
    private int roomQuantity;

    // Step 4: Guest details
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String country;

    // Step 5: Add-ons
    private final List<String> addOns = new ArrayList<>();

    // ── Getters / Setters ────────────────────────────────────────────────────

    public int getAdults() { return adults; }
    public void setAdults(int adults) { this.adults = adults; }

    public int getChildren() { return children; }
    public void setChildren(int children) { this.children = children; }

    public int getTotalGuests() { return adults + children; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public long getNights() {
        if (checkInDate == null || checkOutDate == null) return 0;
        return checkOutDate.toEpochDay() - checkInDate.toEpochDay();
    }

    // Multi-room map: type → quantity
    public Map<String, Integer> getRoomSelections() { return roomSelections; }
    public void setRoomSelection(String type, int qty) {
        if (qty <= 0) roomSelections.remove(type);
        else roomSelections.put(type, qty);
    }
    public int getRoomSelectionQty(String type) {
        return roomSelections.getOrDefault(type, 0);
    }
    public int getTotalRooms() {
        return roomSelections.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Legacy single-type (used by MockPricingService)
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public int getRoomQuantity() { return roomQuantity; }
    public void setRoomQuantity(int roomQuantity) { this.roomQuantity = roomQuantity; }

    /** Sync multi-map → legacy fields (picks dominant type by quantity). */
    public void syncLegacyRoomFields() {
        roomSelections.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .ifPresent(e -> { roomType = e.getKey(); roomQuantity = e.getValue(); });
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public List<String> getAddOns() { return addOns; }

    /** Returns a readable rooms summary for the review screen. */
    public String getRoomsSummary() {
        if (roomSelections.isEmpty()) return roomType != null ? roomQuantity + "x " + roomType : "None";
        StringBuilder sb = new StringBuilder();
        roomSelections.forEach((t, q) -> {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(q).append("x ").append(t);
        });
        return sb.toString();
    }
}
