package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.service.BookingService;
import ca.seneca.application.service.PricingService;
import ca.seneca.application.viewmodel.BookingDraft;
import ca.seneca.application.viewmodel.BookingSession;
import ca.seneca.application.viewmodel.ReservationConfirmationHolder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
        PricingService pricing = AppContext.getPricingService();

        // Guard: if pricing fails (e.g. null room), show error gracefully
        double subtotal, tax, total;
        try {
            subtotal = pricing.calculateSubtotal(draft);
            tax      = pricing.calculateTax(subtotal);
            total    = subtotal + tax;
        } catch (Exception e) {
            subtotal = tax = total = 0;
            e.printStackTrace();
            // System.err.println("[KioskReview] Pricing error: " + e.getMessage());
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
