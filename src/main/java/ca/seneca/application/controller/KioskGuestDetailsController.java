package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.viewmodel.BookingDraft;
import ca.seneca.application.viewmodel.BookingSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class KioskGuestDetailsController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField countryField;
    @FXML private Label validationLabel;

    @FXML
    public void initialize() {
        if (validationLabel != null) validationLabel.setText("");
        // Restore previously entered data
        BookingDraft draft = BookingSession.getCurrentDraft();
        if (draft.getFirstName() != null) firstNameField.setText(draft.getFirstName());
        if (draft.getLastName()  != null) lastNameField.setText(draft.getLastName());
        if (draft.getPhone()     != null) phoneField.setText(draft.getPhone());
        if (draft.getEmail()     != null) emailField.setText(draft.getEmail());
        if (draft.getAddress()   != null && addressField != null) addressField.setText(draft.getAddress());
        if (draft.getCity()      != null && cityField    != null) cityField.setText(draft.getCity());
        if (draft.getCountry()   != null && countryField != null) countryField.setText(draft.getCountry());
    }

    @FXML
    private void handleNext() {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String phone     = phoneField.getText().trim();
        String email     = emailField.getText().trim();

        if (firstName.isBlank()) { validationLabel.setText("First name is required."); return; }
        if (lastName.isBlank())  { validationLabel.setText("Last name is required.");  return; }
        if (phone.isBlank())     { validationLabel.setText("Phone number is required."); return; }
        if (!phone.matches("\\d{7,15}")) {
            validationLabel.setText("Phone must be 7–15 digits (numbers only).");
            return;
        }
        if (!email.isBlank() && !email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            validationLabel.setText("Please enter a valid email address, or leave it blank.");
            return;
        }

        BookingDraft draft = BookingSession.getCurrentDraft();
        draft.setFirstName(firstName);
        draft.setLastName(lastName);
        draft.setPhone(phone);
        draft.setEmail(email);
        if (addressField != null) draft.setAddress(addressField.getText().trim());
        if (cityField    != null) draft.setCity(cityField.getText().trim());
        if (countryField != null) draft.setCountry(countryField.getText().trim());

        AppContext.getSceneManager().switchScene(
                "/views/kiosk_addons.fxml", "Step 5 — Add-Ons");
    }

    @FXML
    private void handleBack() {
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_rooms.fxml", "Step 3 — Select Rooms");
    }
}
