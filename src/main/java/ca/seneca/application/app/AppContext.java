package ca.seneca.application.app;

import ca.seneca.application.logging.AppLogger;
import ca.seneca.application.repository.*;
import ca.seneca.application.service.*;

import java.util.logging.Logger;

/**
 * Central application context — single source of truth for shared services.
 * Kiosk booking still uses Mock impls; your group member will swap in real ones.
 */
public class AppContext {

    private static final Logger log = AppLogger.get(AppContext.class);

    private static SceneManager sceneManager;

    private static final GuestRepository guestRepository = new GuestRepository();
    private static final ReservationRepository reservationRepository = new ReservationRepository();
    private static final RoomRepository roomRepository = new RoomRepository();
    private static final AddOnRepository addOnRepository = new AddOnRepository();
    private static final PaymentRepository paymentRepository = new PaymentRepository();
    private static final LoyaltyAccountRepository loyaltyAccountRepository = new LoyaltyAccountRepository();
    private static final LoyaltyTransactionRepository loyaltyTransactionRepository = new LoyaltyTransactionRepository();
    private static final WaitlistRepository waitlistRepository = new WaitlistRepository();

    private static final BookingService bookingService =
            new BookingService(guestRepository, reservationRepository, roomRepository, addOnRepository);

    private static final DiscountService discountService = new DiscountService();

    private static final PaymentService paymentService =
            new PaymentService(paymentRepository);

    private static final LoyaltyService loyaltyService =
            new LoyaltyService(loyaltyAccountRepository, loyaltyTransactionRepository);

    private static final ReservationAdminService reservationAdminService =
            new ReservationAdminService(
                    reservationRepository,
                    roomRepository,
                    discountService,
                    paymentService,
                    loyaltyService,
                    waitlistRepository
            );

    private AppContext() {}

    public static void init(SceneManager manager) {
        sceneManager = manager;
        log.info("AppContext initialized.");
    }

    public static SceneManager getSceneManager() {
        return sceneManager;
    }

    public static BookingService getBookingService() {
        return bookingService;
    }

    public static DiscountService getDiscountService() {
        return discountService;
    }

    public static PaymentService getPaymentService() {
        return paymentService;
    }

    public static LoyaltyService getLoyaltyService() {
        return loyaltyService;
    }

    public static ReservationAdminService getReservationAdminService() {
        return reservationAdminService;
    }
}
