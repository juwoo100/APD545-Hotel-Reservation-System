package ca.seneca.application.service;

import ca.seneca.application.service.decorator.*;
import ca.seneca.application.service.strategy.PricingStrategy;
import ca.seneca.application.service.strategy.StandardPricingStrategy;
import ca.seneca.application.viewmodel.BookingDraft;

import java.time.LocalDate;
import java.util.Map;

public class StandardPricingService implements PricingService {
    private final PricingStrategy pricingStrategy;

    public StandardPricingService() {
        this.pricingStrategy = new StandardPricingStrategy();
    }

    @Override
    public double calculateSubtotal(BookingDraft draft) {
        if (draft == null) {
            throw new IllegalArgumentException("Booking draft cannot be null.");
        }

        if (draft.getCheckInDate() == null || draft.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }

        if (!draft.getCheckOutDate().isAfter(draft.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        double roomSubtotal = 0.0;

            // Calculation for room subtotal
            for (Map.Entry<String, Integer> entry : draft.getRoomSelections().entrySet()) {
                String roomTypeName = entry.getKey();
                int quantity = entry.getValue();

                double basePrice = getBasePrice(roomTypeName);

                LocalDate current = draft.getCheckInDate();
                while (current.isBefore(draft.getCheckOutDate())) {
                    double nightlyRate = pricingStrategy.calculateNightlyRate(basePrice, current);
                    roomSubtotal += nightlyRate * quantity;
                    current = current.plusDays(1);
                }
            }

            BillComponent bill = new BaseBill(roomSubtotal, "Room Charges");
            long nights = draft.getNights();

            for (String addOnName : draft.getAddOns()) {
                switch (addOnName) {
                    case "WiFi" -> bill = new WifiDecorator(bill);
                    case "Breakfast" -> bill = new BreakfastDecorator(bill, nights);
                    case "Parking" -> bill = new ParkingDecorator(bill, nights);
                    case "Spa" -> bill = new SpaDecorator(bill);
                    default -> {
                        // unknown add-on: ignore or throw
                        throw new IllegalArgumentException("Add-on not found: " + addOnName);
                    }
                }
            }

            return bill.getTotal();
    }

    @Override
    public double calculateTax(double subtotal) {
        return subtotal * 0.13;
    }

    @Override
    public double calculateTotal(BookingDraft draft) {
        double subtotal = calculateSubtotal(draft);
        return subtotal + calculateTax(subtotal);
    }

    private double getBasePrice(String roomTypeName) {
        if (roomTypeName == null) {
            throw new IllegalArgumentException("Room type name cannot be null.");
        }

        return switch (roomTypeName.trim().toLowerCase()) {
            case "single" -> 120.0;
            case "double" -> 200.0;
            case "deluxe" -> 260.0;
            case "penthouse" -> 450.0;
            default -> throw new IllegalArgumentException("Unknown room type: " + roomTypeName);
        };
    }
}
