package ca.seneca.application;

import ca.seneca.application.app.AppContext;
import ca.seneca.application.app.DatabaseSeeder;
import ca.seneca.application.app.SceneManager;
import ca.seneca.application.repository.AddOnRepository;
import ca.seneca.application.repository.GuestRepository;
import ca.seneca.application.repository.ReservationRepository;
import ca.seneca.application.repository.RoomRepository;
import ca.seneca.application.service.BookingService;
import ca.seneca.application.service.PricingService;
import ca.seneca.application.service.StandardPricingService;
import ca.seneca.application.util.JpaUtil;
import javafx.application.Application;
import javafx.stage.Stage;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        DatabaseSeeder.seed();

        SceneManager sceneManager = new SceneManager(stage);

        GuestRepository guestRepository = new GuestRepository();
        ReservationRepository reservationRepository = new ReservationRepository();
        RoomRepository roomRepository = new RoomRepository();
        AddOnRepository addOnRepository = new AddOnRepository();

        BookingService bookingService = new BookingService(
                guestRepository,
                reservationRepository,
                roomRepository,
                addOnRepository
        );

        PricingService pricingService = new StandardPricingService();

        AppContext.init(sceneManager, bookingService, pricingService);

        sceneManager.switchScene("/views/kiosk_welcome.fxml", "Welcome — Sheraton Hotel");
    }

    @Override
    public void stop() {
        JpaUtil.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}