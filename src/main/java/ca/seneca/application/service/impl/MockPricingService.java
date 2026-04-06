package com.hotel.service.impl;

import com.hotel.factory.RoomFactory;
import com.hotel.model.Room;
import com.hotel.service.PricingService;
import com.hotel.service.strategy.PricingStrategy;
import com.hotel.service.strategy.WeekendPricingStrategy;
import com.hotel.viewmodel.BookingDraft;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

/**
 * Pricing implementation using the Strategy Pattern.
 * Uses WeekendPricingStrategy by default (swappable).
 */
public class MockPricingService implements PricingService {

    private final PricingStrategy strategy;

    public MockPricingService() {
        this.strategy = new WeekendPricingStrategy();
    }

    public MockPricingService(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public double calculateSubtotal(BookingDraft draft) {
        double roomTotal = 0;

        // Multi-type selection
        if (!draft.getRoomSelections().isEmpty()) {
            for (Map.Entry<String, Integer> entry : draft.getRoomSelections().entrySet()) {
                Room room = RoomFactory.createRoom(entry.getKey());
                int qty = entry.getValue();
                roomTotal += strategy.calculateNightlyTotal(
                    room.getBasePrice(), qty, draft.getCheckInDate(), draft.getCheckOutDate());
            }
        } else if (draft.getRoomType() != null) {
            // Fallback: legacy single-type field
            Room room = RoomFactory.createRoom(draft.getRoomType());
            roomTotal += strategy.calculateNightlyTotal(
                room.getBasePrice(), draft.getRoomQuantity(),
                draft.getCheckInDate(), draft.getCheckOutDate());
        }

        // Add-ons
        long nights = draft.getNights();
        double addOnTotal = 0;
        for (String addOn : draft.getAddOns()) {
            addOnTotal += switch (addOn) {
                case "WiFi"      -> 15.0;
                case "Breakfast" -> 20.0 * nights;
                case "Parking"   -> 25.0 * nights;
                case "Spa"       -> 60.0;
                default          -> 0.0;
            };
        }

        return roomTotal + addOnTotal;
    }

    @Override
    public double calculateTax(double subtotal) {
        return subtotal * 0.13; // 13% HST
    }

    @Override
    public double calculateTotal(BookingDraft draft) {
        double subtotal = calculateSubtotal(draft);
        return subtotal + calculateTax(subtotal);
    }
}
