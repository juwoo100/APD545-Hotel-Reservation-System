package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.viewmodel.BookingSession;
import ca.seneca.application.viewmodel.ReservationConfirmationHolder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class KioskConfirmationController {

    @FXML private Label reservationNumberLabel;
    @FXML private Label guestNameLabel;
    @FXML private Label thankYouLabel;

    @FXML
    public void initialize() {
        String resNum = ReservationConfirmationHolder.getReservationNumber();
        setText(reservationNumberLabel, resNum != null ? resNum : "N/A");
        setText(guestNameLabel,
                BookingSession.getCurrentDraft().getFullName().trim());
        setText(thankYouLabel,
                "Thank you for choosing Sheraton Hotel! Please proceed to the front desk for check-in.");
    }

    private void setText(Label label, String value) {
        if (label != null) label.setText(value);
    }

    @FXML
    private void handleReturnHome() {
        BookingSession.reset();
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_welcome.fxml", "Welcome — Sheraton Hotel");
    }
}
