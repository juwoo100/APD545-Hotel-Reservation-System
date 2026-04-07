package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AdminLoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    // Hardcoded credentials for M2 (Person A will swap with DB auth in M3)
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    @FXML
    public void initialize() {
        if (errorLabel != null) errorLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            setError("Please enter both username and password.");
            return;
        }

        if (ADMIN_USER.equals(username) && ADMIN_PASS.equals(password)) {
            AppContext.getSceneManager().switchScene(
                    "/views/admin_dashboard.fxml", "Admin Dashboard");
        } else {
            setError("Invalid username or password. Try admin / admin123");
            passwordField.clear();
        }
    }

    @FXML
    private void handleBackToKiosk() {
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_welcome.fxml", "Welcome — Sheraton Hotel");
    }

    private void setError(String msg) {
        if (errorLabel != null) errorLabel.setText(msg);
    }
}
