package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.viewmodel.BookingDraft;
import ca.seneca.application.viewmodel.BookingSession;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class KioskDatesController {

    @FXML private DatePicker checkInDatePicker;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private Label validationLabel;

    @FXML
    public void initialize() {
        if (validationLabel != null) validationLabel.setText("");
        // Restore any previously selected dates
        BookingDraft draft = BookingSession.getCurrentDraft();
        if (draft.getCheckInDate()  != null) checkInDatePicker.setValue(draft.getCheckInDate());
        if (draft.getCheckOutDate() != null) checkOutDatePicker.setValue(draft.getCheckOutDate());
    }

    @FXML
    private void handleNext() {
        LocalDate checkIn  = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (checkIn == null || checkOut == null) {
            validationLabel.setText("Please select both check-in and check-out dates.");
            return;
        }
        if (checkIn.isBefore(LocalDate.now())) {
            validationLabel.setText("Check-in date cannot be in the past.");
            return;
        }
        if (!checkOut.isAfter(checkIn)) {
            validationLabel.setText("Check-out must be at least 1 night after check-in.");
            return;
        }

        BookingDraft draft = BookingSession.getCurrentDraft();
        draft.setCheckInDate(checkIn);
        draft.setCheckOutDate(checkOut);

        AppContext.getSceneManager().switchScene(
                "/views/kiosk_rooms.fxml", "Step 3 — Select Rooms");
    }

    @FXML
    private void handleBack() {
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_guests.fxml", "Step 1 — Number of Guests");
    }
}
