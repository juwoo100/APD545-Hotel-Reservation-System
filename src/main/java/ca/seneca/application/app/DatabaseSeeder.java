package ca.seneca.application.app;

import ca.seneca.application.model.AddOn;
import ca.seneca.application.model.RoomType;
import ca.seneca.application.util.JpaUtil;
import jakarta.persistence.EntityManager;

import ca.seneca.application.logging.AppLogger;
import ca.seneca.application.model.AdminUser;
import ca.seneca.application.model.RoomEntity;
import ca.seneca.application.model.enums.AdminRole;
import ca.seneca.application.model.enums.RoomType;
import ca.seneca.application.repository.AdminUserRepository;
import ca.seneca.application.repository.RoomRepository;
import ca.seneca.application.security.PasswordHasher;

import java.util.logging.Logger;

/**
 * Seeds the H2 database on first run with default rooms and admin accounts.
 * Idempotent — safe to call on every startup.
 */
public class DatabaseSeeder {
    private static final Logger log = AppLogger.get(DatabaseSeeder.class);

    private DatabaseSeeder() {}

    public static void seedAll() {
        seedRoomTypesAndAddOns();
        seedRooms();
        seedAdmins();
    }

    private static void seedRoomTypesAndAddOns() {
        EntityManager em = JpaUtil.getEntityManager();

        try {

            Long roomTypeCount = em.createQuery("SELECT COUNT(rt) FROM RoomType rt", Long.class)
                    .getSingleResult();

            if (count > 0) {
                log.info("RoomTypes already seeded - skipping.");
                return;
            }
            em.getTransaction().begin();

            RoomType single = new RoomType("Single", 2, 120.0);
            RoomType doubleRoom = new RoomType("Double", 4, 200.0);
            RoomType deluxe = new RoomType("Deluxe", 2, 260.0);
            RoomType penthouse = new RoomType("Penthouse", 2, 450.0);

            em.persist(single);
            em.persist(doubleRoom);
            em.persist(deluxe);
            em.persist(penthouse);

            em.persist(new AddOn("Wifi", 15.0, "High-speed internet", "PER_RESERVATION", "Y"));
            em.persist(new AddOn("Breakfast", 20.0, "Buffet breakfast", "PER_NIGHT", "Y"));
            em.persist(new AddOn("Parking", 25.0, "Underground parking", "PER_NIGHT", "Y"));
            em.persist(new AddOn("Spa", 60.0, "Spa access", "PER_RESERVATION", "Y"));

            em.getTransaction().commit();
            log.info("RoomTypes & AddOns seeded.");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }

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
