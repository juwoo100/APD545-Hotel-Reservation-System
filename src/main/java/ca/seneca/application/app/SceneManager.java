package ca.seneca.application.app;

import ca.seneca.application.logging.AppLogger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Person B owns this class — all scene switching goes through here.
 *
 * switchScene()  — replaces the primary stage scene (kiosk flow)
 * openWindow()   — opens a modal child window (admin sub-screens)
 * closeWindow()  — closes the window containing a given node
 */
public class SceneManager {

    private static final Logger log = AppLogger.get(SceneManager.class);

    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /** Replace the primary stage content. */
    public void switchScene(String fxmlPath, String title) {
        try {
            Parent root = load(fxmlPath);
            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 1400, 900));
            } else {
                primaryStage.getScene().setRoot(root);
            }
            primaryStage.setTitle(title);
            primaryStage.show();
            log.fine("switchScene → " + fxmlPath);
        } catch (IOException e) {
            log.severe("switchScene failed: " + fxmlPath + " | " + e.getMessage());
        }
    }

    /** Open a modal window on top of the primary stage. */
    public void openWindow(String fxmlPath, String title) {
        openWindow(fxmlPath, title, 1400, 900);
    }

    public void openWindow(String fxmlPath, String title, double width, double height) {
        try {
            Parent root = load(fxmlPath);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(primaryStage);
            stage.showAndWait();
            log.fine("openWindow → " + fxmlPath);
        } catch (IOException e) {
            log.severe("openWindow failed: " + fxmlPath + " | " + e.getMessage());
        }
    }

    /** Close whichever Stage contains the given node. */
    public static void closeWindow(Node node) {
        if (node != null && node.getScene() != null) {
            ((Stage) node.getScene().getWindow()).close();
        }
    }

    public Stage getPrimaryStage() { return primaryStage; }

    private Parent load(String fxmlPath) throws IOException {
        URL url = getClass().getResource(fxmlPath);
        if (url == null) throw new IOException("FXML not found on classpath: " + fxmlPath);
        return new FXMLLoader(url).load();
    }
}
