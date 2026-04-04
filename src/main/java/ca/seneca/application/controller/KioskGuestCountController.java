package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.viewmodel.BookingDraft;
import ca.seneca.application.viewmodel.BookingSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class KioskGuestCountController {

    @FXML private Spinner<Integer> adultsSpinner;
    @FXML private Spinner<Integer> childrenSpinner;
    @FXML private Label validationLabel;

    @FXML
    public void initialize() {
        adultsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        childrenSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0));
        if (validationLabel != null) validationLabel.setText("");
    }

    @FXML
    private void handleNext() {
        int adults   = adultsSpinner.getValue();
        int children = childrenSpinner.getValue();

        if (adults < 1) {
            validationLabel.setText("At least one adult is required.");
            return;
        }

        BookingDraft draft = BookingSession.getCurrentDraft();
        draft.setAdults(adults);
        draft.setChildren(children);

        AppContext.getSceneManager().switchScene(
                "/views/kiosk_dates.fxml", "Step 2 — Select Dates");
    }

    @FXML
    private void handleBack() {
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_welcome.fxml", "Welcome");
    }
}
