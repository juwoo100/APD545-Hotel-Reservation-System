package ca.seneca.application.app;


import ca.seneca.application.service.BookingService;
import ca.seneca.application.service.PricingService;

public class AppContext {
    private static SceneManager sceneManager;
    private static BookingService bookingService;
    private static PricingService pricingService;

    private AppContext() {}

    public static void init(SceneManager manager,
                            BookingService bookingSvc,
                            PricingService pricingSvc) {
        sceneManager = manager;
        bookingService = bookingSvc;
        pricingService = pricingSvc;
    }

    public static SceneManager getSceneManager() {
        return sceneManager;
    }

    public static BookingService getBookingService() {
        return bookingService;
    }

    public static PricingService getPricingService() {
        return pricingService;
    }
}
