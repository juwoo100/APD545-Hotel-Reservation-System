package ca.seneca.application.controller;

import ca.seneca.application.app.AppContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class AdminPaymentsController {

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCloseWindow() {
        // fallback for buttons without ActionEvent
        Stage stage = Stage.getWindows().stream()
                .filter(w -> w instanceof Stage && ((Stage) w).isShowing())
                .map(w -> (Stage) w)
                .filter(s -> !s.equals(AppContext.getSceneManager().getPrimaryStage()))
                .findFirst().orElse(null);
        if (stage != null) stage.close();
    }
}
