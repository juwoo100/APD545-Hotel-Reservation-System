package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.viewmodel.BookingSession;
import javafx.fxml.FXML;

public class KioskWelcomeController {

    @FXML
    private void handleStartReservation() {
        BookingSession.reset();
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_guests.fxml", "Step 1 — Number of Guests");
    }

    @FXML
    private void handleAdminLogin() {
        AppContext.getSceneManager().switchScene(
                "/views/admin_login.fxml", "Admin Login");
    }

    @FXML
    private void handleSubmitFeedback() {
        // Feedback navigates to guest details then submission
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_guest_details.fxml", "Feedback");
    }

    @FXML
    private void handleRules() {
        showRulesAlert();
    }

    @FXML
    private void handleNeedHelp() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Need Help?");
        alert.setContentText("Please speak to a front desk staff member for assistance.\n" +
                "Phone: (416) 555-0100");
        alert.showAndWait();
    }

    private void showRulesAlert() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Rules & Regulations");
        alert.setHeaderText("Sheraton Hotel — Rules & Regulations");
        alert.setContentText(
                "1. Check-in time: 3:00 PM  |  Check-out time: 11:00 AM\n" +
                        "2. No smoking anywhere on the premises.\n" +
                        "3. Pets are not permitted.\n" +
                        "4. Valid government-issued ID required at check-in.\n" +
                        "5. Cancellations must be made at least 24 hours before check-in.\n" +
                        "6. Quiet hours: 10:00 PM – 8:00 AM.\n" +
                        "7. Guests are responsible for any damages to hotel property."
        );
        alert.showAndWait();
    }
}
