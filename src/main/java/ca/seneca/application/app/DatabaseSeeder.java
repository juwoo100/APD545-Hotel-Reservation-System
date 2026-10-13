package ca.seneca.application.app;

import ca.seneca.application.model.AddOn;
import ca.seneca.application.model.Admin;
import ca.seneca.application.model.enums.RoomAvailabilityStatus;
import ca.seneca.application.model.enums.RoomType;
import ca.seneca.application.util.JpaUtil;
import jakarta.persistence.EntityManager;

import ca.seneca.application.logging.AppLogger;
import ca.seneca.application.model.RoomEntity;
import ca.seneca.application.model.enums.AdminRole;
import ca.seneca.application.repository.RoomRepository;

import java.util.logging.Logger;

/**
 * Seeds the H2 database on first run with default rooms and admin accounts.
 * Idempotent — safe to call on every startup.
 */
public class DatabaseSeeder {
    private static final Logger log = AppLogger.get(DatabaseSeeder.class);

    private DatabaseSeeder() {}

    public static void seedAll() {
        seedAddOns();
        seedRooms();
        seedAdmins();
    }

    private static void seedAddOns() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(a) FROM AddOn a", Long.class).getSingleResult();
            if (count > 0) {
                log.info("AddOns already seeded - skipping.");
                return;
            }

            em.getTransaction().begin();

            AddOn wifi = new AddOn();
            wifi.setAddOnName("WiFi");
            wifi.setBasePrice(15.0);
            wifi.setDescription("High-speed internet");

            AddOn breakfast = new AddOn();
            breakfast.setAddOnName("Breakfast");
            breakfast.setBasePrice(20.0);
            breakfast.setDescription("Buffet breakfast");

            AddOn parking = new AddOn();
            parking.setAddOnName("Parking");
            parking.setBasePrice(25.0);
            parking.setDescription("Underground parking");

            AddOn spa = new AddOn();
            spa.setAddOnName("Spa");
            spa.setBasePrice(60.0);
            spa.setDescription("Spa access");

            em.persist(wifi);
            em.persist(breakfast);
            em.persist(parking);
            em.persist(spa);

            em.getTransaction().commit();
            log.info("AddOns seeded.");
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
            log.info("Rooms already seeded - skipping.");
            return;
        }

        log.info("Seeding rooms...");

        for (int i = 1; i <= 6; i++) {
            RoomEntity room = new RoomEntity("10" + i, RoomType.SINGLE, 2, 120.0);
            room.setStatus(RoomAvailabilityStatus.AVAILABLE);
            repo.save(room);
        }

        for (int i = 1; i <= 6; i++) {
            RoomEntity room = new RoomEntity("20" + i, RoomType.DOUBLE, 4, 200.0);
            room.setStatus(RoomAvailabilityStatus.AVAILABLE);
            repo.save(room);
        }

        for (int i = 1; i <= 4; i++) {
            RoomEntity room = new RoomEntity("30" + i, RoomType.DELUXE, 2, 260.0);
            room.setStatus(RoomAvailabilityStatus.AVAILABLE);
            repo.save(room);
        }

        RoomEntity penthouse1 = new RoomEntity("401", RoomType.PENTHOUSE, 2, 450.0);
        penthouse1.setStatus(RoomAvailabilityStatus.AVAILABLE);
        repo.save(penthouse1);

        RoomEntity penthouse2 = new RoomEntity("402", RoomType.PENTHOUSE, 2, 450.0);
        penthouse2.setStatus(RoomAvailabilityStatus.AVAILABLE);
        repo.save(penthouse2);

        log.info("Rooms seeded.");
    }

    private static void seedAdmins() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(a) FROM Admin a", Long.class).getSingleResult();
            if (count > 0) {
                log.info("Admins already seeded - skipping.");
                return;
            }

            em.getTransaction().begin();

            Admin manager = new Admin();
            manager.setName("Hotel Manager");
            manager.setEmail("manager@hotel.com");
            manager.setRole(AdminRole.MANAGER);
            em.persist(manager);

            Admin frontDesk = new Admin();
            frontDesk.setName("Front Desk");
            frontDesk.setEmail("frontdesk@hotel.com");
            frontDesk.setRole(AdminRole.FRONT_DESK);
            em.persist(frontDesk);

            Admin accountant = new Admin();
            accountant.setName("Accountant");
            accountant.setEmail("accountant@hotel.com");
            accountant.setRole(AdminRole.ACCOUNTANT);
            em.persist(accountant);

            Admin systemAdmin = new Admin();
            systemAdmin.setName("System Admin");
            systemAdmin.setEmail("sysadmin@hotel.com");
            systemAdmin.setRole(AdminRole.SYSTEM_ADMIN);
            em.persist(systemAdmin);

            em.getTransaction().commit();
            log.info("Admins seeded.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
