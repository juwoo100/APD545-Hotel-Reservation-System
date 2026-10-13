package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import javafx.fxml.FXML;

public class AdminDashboardController {

    // ── Sidebar navigation ───────────────────────────────────────────────────

    @FXML private void openReservations() {
        AppContext.getSceneManager().openWindow(
                "/views/admin_reservations.fxml", "Reservations");
    }

    @FXML private void openPayments() {
        AppContext.getSceneManager().openWindow(
                "/views/admin_payments.fxml", "Payments");
    }

    @FXML private void openCheckout() {
        AppContext.getSceneManager().openWindow(
                "/views/admin_checkout.fxml", "Checkout");
    }

    @FXML private void openWaitlist() {
        AppContext.getSceneManager().openWindow(
                "/views/admin_waitlist.fxml", "Waitlist");
    }

    @FXML private void openLoyalty() {
        AppContext.getSceneManager().openWindow(
                "/views/admin_loyalty.fxml", "Loyalty Program");
    }

    @FXML private void openFeedback() {
        AppContext.getSceneManager().openWindow(
                "/views/admin_feedback.fxml", "Guest Feedback");
    }

    @FXML private void openRevenueReport() {
        AppContext.getSceneManager().openWindow(
                "/views/admin_revenue_report.fxml", "Revenue Report");
    }

    @FXML private void openOccupancyReport() {
        AppContext.getSceneManager().openWindow(
                "/views/admin_occupancy_report.fxml", "Occupancy Report");
    }

    @FXML private void openActivityLogs() {
        AppContext.getSceneManager().openWindow(
                "/views/admin_activity_logs.fxml", "Activity Logs");
    }

    @FXML private void handleLogout() {
        AppContext.getSceneManager().switchScene(
                "/views/admin_login.fxml", "Admin Login");
    }

    @FXML private void handleBackToKiosk() {
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_welcome.fxml", "Welcome — Sheraton Hotel");
    }
}
