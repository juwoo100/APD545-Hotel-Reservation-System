package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.viewmodel.BookingDraft;
import ca.seneca.application.viewmodel.BookingSession;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class KioskAddonsController {

    @FXML private CheckBox wifiCheckBox;
    @FXML private CheckBox breakfastCheckBox;
    @FXML private CheckBox parkingCheckBox;
    @FXML private CheckBox spaCheckBox;
    @FXML private Label addOnTotalLabel;

    @FXML
    public void initialize() {
        // Restore previous selections
        BookingDraft draft = BookingSession.getCurrentDraft();
        if (wifiCheckBox      != null) wifiCheckBox.setSelected(draft.getAddOns().contains("WiFi"));
        if (breakfastCheckBox != null) breakfastCheckBox.setSelected(draft.getAddOns().contains("Breakfast"));
        if (parkingCheckBox   != null) parkingCheckBox.setSelected(draft.getAddOns().contains("Parking"));
        if (spaCheckBox       != null) spaCheckBox.setSelected(draft.getAddOns().contains("Spa"));
        updateTotal();
    }

    @FXML private void onWifiToggled()      { updateTotal(); }
    @FXML private void onBreakfastToggled() { updateTotal(); }
    @FXML private void onParkingToggled()   { updateTotal(); }
    @FXML private void onSpaToggled()       { updateTotal(); }

    private void updateTotal() {
        double total = 0;
        if (wifiCheckBox      != null && wifiCheckBox.isSelected())      total += 15;
        if (breakfastCheckBox != null && breakfastCheckBox.isSelected()) total += 20;
        if (parkingCheckBox   != null && parkingCheckBox.isSelected())   total += 25;
        if (spaCheckBox       != null && spaCheckBox.isSelected())       total += 60;
        if (addOnTotalLabel   != null)
            addOnTotalLabel.setText(String.format("Add-ons subtotal: $%.2f (excl. per-night charges)", total));
    }

    @FXML
    private void handleNext() {

        BookingDraft draft = BookingSession.getCurrentDraft();
        draft.getAddOns().clear();
        if (wifiCheckBox      != null && wifiCheckBox.isSelected())      draft.getAddOns().add("WiFi");
        if (breakfastCheckBox != null && breakfastCheckBox.isSelected()) draft.getAddOns().add("Breakfast");
        if (parkingCheckBox   != null && parkingCheckBox.isSelected())   draft.getAddOns().add("Parking");
        if (spaCheckBox       != null && spaCheckBox.isSelected())       draft.getAddOns().add("Spa");
        System.out.println("Review button clicked");
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_review.fxml", "Step 6 — Review Booking");
    }

    @FXML
    private void handleBack() {
        AppContext.getSceneManager().switchScene(
                "/views/kiosk_guest_details.fxml", "Step 4 — Guest Details");
    }
}
