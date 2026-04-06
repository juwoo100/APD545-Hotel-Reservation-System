package com.hotel.app;

import com.hotel.logging.AppLogger;
import com.hotel.service.AdminAuthService;
import com.hotel.service.BookingService;
import com.hotel.service.PricingService;
import com.hotel.service.impl.MockBookingService;
import com.hotel.service.impl.MockPricingService;

import java.util.logging.Logger;

/**
 * Central application context — single source of truth for shared services.
 * Kiosk booking still uses Mock impls; your group member will swap in real ones.
 */
public class AppContext {

    private static final Logger log = AppLogger.get(AppContext.class);

    private static SceneManager sceneManager;

    // Kiosk flow services (Mock now — your teammate replaces with real impls)
    private static final BookingService bookingService = new MockBookingService();
    private static final PricingService pricingService = new MockPricingService();

    // Admin auth — real BCrypt, role-based discount caps
    private static final AdminAuthService adminAuthService = AdminAuthService.getInstance();

    private AppContext() {}

    public static void init(SceneManager manager) {
        sceneManager = manager;
        log.info("AppContext initialised.");
    }

    public static SceneManager      getSceneManager()    { return sceneManager; }
    public static BookingService     getBookingService()  { return bookingService; }
    public static PricingService     getPricingService()  { return pricingService; }
    public static AdminAuthService   getAdminAuth()       { return adminAuthService; }
}
