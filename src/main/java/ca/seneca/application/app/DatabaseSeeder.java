package com.hotel.app;

import com.hotel.logging.AppLogger;
import com.hotel.model.AdminUser;
import com.hotel.model.RoomEntity;
import com.hotel.model.enums.AdminRole;
import com.hotel.model.enums.RoomType;
import com.hotel.repository.AdminUserRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.security.PasswordHasher;

import java.util.logging.Logger;

/**
 * Seeds the H2 database on first run with default rooms and admin accounts.
 * Idempotent — safe to call on every startup.
 */
public class DatabaseSeeder {

    private static final Logger log = AppLogger.get(DatabaseSeeder.class);

    private DatabaseSeeder() {}

    public static void seedAll() {
        seedRooms();
        seedAdmins();
    }

    private static void seedRooms() {
        RoomRepository repo = new RoomRepository();
        if (repo.countByType(RoomType.SINGLE) > 0) {
            log.info("Rooms already seeded — skipping.");
            return;
        }
        log.info("Seeding rooms...");
        // Singles: 101-106
        for (int i = 1; i <= 6; i++)
            repo.save(new RoomEntity("10" + i, RoomType.SINGLE, 2, 120.0));
        // Doubles: 201-206
        for (int i = 1; i <= 6; i++)
            repo.save(new RoomEntity("20" + i, RoomType.DOUBLE, 4, 200.0));
        // Deluxe: 301-304
        for (int i = 1; i <= 4; i++)
            repo.save(new RoomEntity("30" + i, RoomType.DELUXE, 2, 260.0));
        // Penthouse: 401-402
        repo.save(new RoomEntity("401", RoomType.PENTHOUSE, 2, 450.0));
        repo.save(new RoomEntity("402", RoomType.PENTHOUSE, 2, 450.0));
        log.info("Seeded 18 rooms.");
    }

    private static void seedAdmins() {
        AdminUserRepository repo = new AdminUserRepository();
        if (repo.count() > 0) {
            log.info("Admin accounts already seeded — skipping.");
            return;
        }
        log.info("Seeding admin accounts...");
        repo.persist(new AdminUser("admin",   PasswordHasher.hash("admin123"),   AdminRole.ADMIN,   "Hotel Administrator"));
        repo.persist(new AdminUser("manager", PasswordHasher.hash("manager123"), AdminRole.MANAGER, "Hotel Manager"));
        log.info("Seeded: admin/admin123  |  manager/manager123");
    }
}
