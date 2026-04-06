package com.hotel.app;

import com.hotel.db.Database;
import com.hotel.logging.AppLogger;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger log = AppLogger.get(MainApp.class);

    @Override
    public void start(Stage stage) {
        // 1. Init rotating file logger first
        AppLogger.init();
        log.info("=== Sheraton Hotel System starting ===");

        // 2. Seed DB (idempotent — safe every run)
        try {
            DatabaseSeeder.seedAll();
        } catch (Exception e) {
            log.severe("DB seed failed: " + e.getMessage());
        }

        // 3. Build scene manager and wire AppContext
        SceneManager sceneManager = new SceneManager(stage);
        AppContext.init(sceneManager);

        // 4. Launch to welcome screen
        stage.setTitle("Sheraton Hotel — Reservation System");
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        sceneManager.switchScene("/com/hotel/view/kiosk_welcome.fxml", "Sheraton Hotel");

        log.info("Application started.");
    }

    @Override
    public void stop() {
        log.info("Application shutting down — closing DB connection.");
        Database.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
