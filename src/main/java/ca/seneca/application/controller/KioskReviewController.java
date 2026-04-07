package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.service.BookingService;
import ca.seneca.application.service.decorator.*;
import ca.seneca.application.service.strategy.PricingStrategy;
import ca.seneca.application.service.strategy.StandardPricingStrategy;
import ca.seneca.application.viewmodel.BookingDraft;
import ca.seneca.application.viewmodel.BookingSession;
import ca.seneca.application.viewmodel.ReservationConfirmationHolder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Map;

public class KioskReviewController {

    @FXML private Label guestNameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label roomSummaryLabel;
    @FXML private Label stayDatesLabel;
    @FXML private Label nightsLabel;
    @FXML private Label addOnsLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    // Legacy field names (still present in original FXML)
    @FXML private Label roomTypeLabel;
    @FXML private Label roomQuantityLabel;

    @FXML
    public void initialize() {
        BookingDraft draft = BookingSession.getCurrentDraft();

        double subtotal;
        double tax;
        double total;

        // Guard: if pricing fails (e.g. null room), show error gracefully
        try {
            subtotal = calculateSubtotal(draft);
            tax      = subtotal * 0.13;
            total    = subtotal + tax;
        } catch (Exception e) {
            subtotal = tax = total = 0.0;
            e.printStackTrace();
            System.err.println("[KioskReview] Pricing error: " + e.getMessage());
        }

        setText(guestNameLabel,    draft.getFullName().trim());
        setText(phoneLabel,        draft.getPhone());
        setText(emailLabel,        draft.getEmail() != null && !draft.getEmail().isBlank()
                ? draft.getEmail() : "Not provided");
        setText(stayDatesLabel,    draft.getCheckInDate() + "  →  " + draft.getCheckOutDate());
        setText(nightsLabel,       draft.getNights() + " night(s)");
        setText(roomSummaryLabel,  draft.getRoomsSummary());
        setText(roomTypeLabel,     draft.getRoomType());
        setText(roomQuantityLabel, String.valueOf(draft.getRoomQuantity()));
        setText(addOnsLabel,       draft.getAddOns().isEmpty()
                ? "None" : String.join(", ", draft.getAddOns()));
        setText(subtotalLabel,     String.format("$%.2f", subtotal));
        setText(taxLabel,          String.format("$%.2f  (13%% HST)", tax));
        setText(totalLabel,        String.format("$%.2f", total));
    }
    private double calculateSubtotal(BookingDraft draft) {
        PricingStrategy strategy = new StandardPricingStrategy();
        double roomSubtotal = 0.0;

        for (Map.Entry<String, Integer> entry : draft.getRoomSelections().entrySet()) {
            String roomType = entry.getKey();
            int quantity = entry.getValue();

            double basePrice = getBasePrice(roomType);
            roomSubtotal += calculateRoomTotal(basePrice, quantity, draft, strategy);
        }

        BillComponent bill = new BaseBill(roomSubtotal, "Room Charges");
        long nights = draft.getNights();

        for (String addOn : draft.getAddOns()) {
            switch (addOn) {
                case "WiFi" -> bill = new WifiDecorator(bill);
                case "Breakfast" -> bill = new BreakfastDecorator(bill, nights);
                case "Parking" -> bill = new ParkingDecorator(bill, nights);
                case "Spa" -> bill = new SpaDecorator(bill);
            }
        }

        return bill.getTotal();
    }

    private double calculateRoomTotal(double basePrice, int quantity, BookingDraft draft, PricingStrategy strategy) {
        double total = 0.0;
        var current = draft.getCheckInDate();

        while (current.isBefore(draft.getCheckOutDate())) {
            total += strategy.calculateNightlyRate(basePrice, current) * quantity;
            current = current.plusDays(1);
        }

        return total;
    }

    private double getBasePrice(String roomType) {
        return switch (roomType.toLowerCase()) {
            case "single" -> 120.0;
            case "double" -> 200.0;
            case "deluxe" -> 260.0;
            case "penthouse" -> 450.0;
            default -> throw new IllegalArgumentException("Unknown room type: " + roomType);
        };
    }

    private void setText(Label label, String value) {
        if (label != null) label.setText(value != null ? value : "—");
    }

    @FXML
    private void handleConfirmReservation() {
        BookingService bookingService = AppContext.getBookingService();
        String reservationNumber = bookingService.completeBooking(BookingSession.getCurrentDraft());
        ReservationConfirmationHolder.setReservationNumber(reservationNumber);

        AppContext.getSceneManager().switchScene(
                "/views/kiosk_confirmation.fxml", "Booking Confirmed!");
    }

    @FXML
    private void handleBack() {
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_addons.fxml", "Step 5 — Add-Ons");
    }
}
