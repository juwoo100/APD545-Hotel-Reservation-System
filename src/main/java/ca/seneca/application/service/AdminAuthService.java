package com.hotel.service;

import com.hotel.logging.AppLogger;
import com.hotel.model.AdminUser;
import com.hotel.model.enums.AdminRole;
import com.hotel.repository.AdminUserRepository;
import com.hotel.security.PasswordHasher;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Admin authentication with BCrypt verification.
 * Role-based discount caps: ADMIN → 15%, MANAGER → 30%.
 */
public class AdminAuthService {

    private static final Logger log = AppLogger.get(AdminAuthService.class);
    private static AdminAuthService instance;

    private final AdminUserRepository repo = new AdminUserRepository();
    private AdminUser currentUser;

    private AdminAuthService() {}

    public static synchronized AdminAuthService getInstance() {
        if (instance == null) instance = new AdminAuthService();
        return instance;
    }

    public boolean login(String username, String password) {
        Optional<AdminUser> opt = repo.findByUsername(username);
        if (opt.isEmpty()) { log.warning("Login failed — user not found: " + username); return false; }
        AdminUser user = opt.get();
        if (PasswordHasher.verify(password, user.getPasswordHash())) {
            currentUser = user;
            log.info("Admin login: " + username + " role=" + user.getRole());
            return true;
        }
        log.warning("Login failed — bad password for: " + username);
        return false;
    }

    public void logout() { log.info("Admin logout: " + (currentUser != null ? currentUser.getUsername() : "?")); currentUser = null; }
    public AdminUser getCurrentUser() { return currentUser; }
    public boolean isLoggedIn() { return currentUser != null; }
    public boolean isManager() { return currentUser != null && currentUser.getRole() == AdminRole.MANAGER; }

    /** Role-based discount cap. */
    public double getMaxDiscountPercent() {
        if (currentUser == null) return 0;
        return currentUser.getRole() == AdminRole.MANAGER ? 30.0 : 15.0;
    }
}
