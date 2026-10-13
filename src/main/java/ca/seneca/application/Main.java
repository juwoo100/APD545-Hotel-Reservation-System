package ca.seneca.application;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.app.DatabaseSeeder;
import ca.seneca.application.app.SceneManager;
import ca.seneca.application.logging.AppLogger;
import ca.seneca.application.util.JpaUtil;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger log = AppLogger.get(Main.class);

    @Override
    public void start(Stage stage) {
        AppLogger.init();
        log.info("=== Sheraton Hotel System starting ===");

        DatabaseSeeder.seedAll();

        SceneManager sceneManager = new SceneManager(stage);
        AppContext.init(sceneManager);

        stage.setTitle("Sheraton Hotel — Reservation System");
        stage.setMinWidth(1200);
        stage.setMinHeight(800);

        sceneManager.switchScene("/views/kiosk_welcome.fxml", "Welcome — Sheraton Hotel");
    }

    @Override
    public void stop() {
        log.info("Application shutting down.");
        JpaUtil.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}