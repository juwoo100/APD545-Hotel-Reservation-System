package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.viewmodel.BookingDraft;
import ca.seneca.application.viewmodel.BookingSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class KioskRoomSelectionController {

    @FXML private Spinner<Integer> singleSpinner;
    @FXML private Spinner<Integer> doubleSpinner;
    @FXML private Spinner<Integer> deluxeSpinner;
    @FXML private Spinner<Integer> penthouseSpinner;
    @FXML private Label suggestionLabel;
    @FXML private Label validationLabel;

    @FXML
    public void initialize() {
        singleSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        doubleSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        deluxeSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 0));
        penthouseSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 2, 0));

        if (validationLabel != null) validationLabel.setText("");

        // Show suggestion based on guest count
        BookingDraft draft = BookingSession.getCurrentDraft();
        if (suggestionLabel != null) {
            int total = draft.getTotalGuests();
            String suggestion;
            if (total <= 2)      suggestion = "Suggested: 1 Single Room (max 2 guests)";
            else if (total <= 4) suggestion = "Suggested: 1 Double Room (max 4 guests)";
            else                 suggestion = "Suggested: " + (int)Math.ceil(total / 4.0) + " Double Room(s)";
            suggestionLabel.setText(suggestion + "  —  " + total + " guest(s) total");
        }
    }

    @FXML
    private void handleAcceptSuggestion() {
        BookingDraft draft = BookingSession.getCurrentDraft();
        int total = draft.getTotalGuests();
        draft.getRoomSelections().clear();

        if (total <= 2) {
            draft.setRoomSelection("Single", 1);
        } else if (total <= 4) {
            draft.setRoomSelection("Double", 1);
        } else {
            draft.setRoomSelection("Double", (int) Math.ceil(total / 4.0));
        }
        draft.syncLegacyRoomFields();

        AppContext.getSceneManager().switchScene(
                "/views/kiosk_guest_details.fxml", "Step 4 — Guest Details");
    }

    @FXML
    private void handleNext() {
        int singleQty     = singleSpinner.getValue();
        int doubleQty     = doubleSpinner.getValue();
        int deluxeQty     = deluxeSpinner.getValue();
        int penthouseQty  = penthouseSpinner.getValue();

        if (singleQty + doubleQty + deluxeQty + penthouseQty == 0) {
            validationLabel.setText("Please select at least one room, or accept the suggested plan.");
            return;
        }

        BookingDraft draft = BookingSession.getCurrentDraft();
        int totalGuests = draft.getTotalGuests();

        // Calculate capacity using RoomFactory
        int capacity =
                (singleQty * 2) +
                        (doubleQty * 4) +
                        (deluxeQty * 2) +
                        (penthouseQty * 2);

        if (capacity < totalGuests) {
            validationLabel.setText(
                    "Selected rooms hold " + capacity + " guests but you have " +
                            totalGuests + ". Please add more rooms.");
            return;
        }

        // Save selections
        draft.getRoomSelections().clear();
        if (singleQty    > 0) draft.setRoomSelection("Single",    singleQty);
        if (doubleQty    > 0) draft.setRoomSelection("Double",    doubleQty);
        if (deluxeQty    > 0) draft.setRoomSelection("Deluxe",    deluxeQty);
        if (penthouseQty > 0) draft.setRoomSelection("Penthouse", penthouseQty);
        draft.syncLegacyRoomFields();

        AppContext.getSceneManager().switchScene(
                "/views/kiosk_guest_details.fxml", "Step 4 — Guest Details");
    }

    @FXML
    private void handleBack() {
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_dates.fxml", "Step 2 — Select Dates");
    }
}
