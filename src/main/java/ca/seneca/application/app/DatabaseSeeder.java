package ca.seneca.application.app;

import ca.seneca.application.model.AddOn;
import ca.seneca.application.model.RoomType;
import ca.seneca.application.util.JpaUtil;
import jakarta.persistence.EntityManager;

public class DatabaseSeeder {
    public static void seed() {
        EntityManager em = JpaUtil.getEntityManager();

        try {

            Long roomTypeCount = em.createQuery("SELECT COUNT(rt) FROM RoomType rt", Long.class)
                    .getSingleResult();

            if (roomTypeCount > 0) {
                return;
            }

            em.getTransaction().begin();
            System.out.println("DatabaseSeeder is running...");
            RoomType singleRoom = new RoomType();
            singleRoom.setTypeName("Single");
            singleRoom.setCapacity(2);
            singleRoom.setBasePrice(120.0);
            em.persist(singleRoom);

            RoomType doubleRoom = new RoomType();
            doubleRoom.setTypeName("Double");
            doubleRoom.setCapacity(4);
            doubleRoom.setBasePrice(200.0);
            em.persist(doubleRoom);

            RoomType deluxeRoom = new RoomType();
            deluxeRoom.setTypeName("Deluxe");
            deluxeRoom.setCapacity(2);
            deluxeRoom.setBasePrice(260.0);
            em.persist(deluxeRoom);

            RoomType penthouse = new RoomType();
            penthouse.setTypeName("Penthouse");
            penthouse.setCapacity(2);
            penthouse.setBasePrice(450.0);
            em.persist(penthouse);

            AddOn wifi = new AddOn();
            wifi.setAddOnName("Wifi");
            wifi.setBasePrice(15.0);
            wifi.setDescription("High-speed internet throughout your stay");
            wifi.setPricingModel("PER_RESERVATION");
            wifi.setActive("Y");
            em.persist(wifi);

            AddOn breakfast = new AddOn();
            breakfast.setAddOnName("Breakfast");
            breakfast.setBasePrice(20.0);
            breakfast.setDescription("Full buffet breakfast each morning");
            breakfast.setPricingModel("PER_NIGHT");
            breakfast.setActive("Y");
            em.persist(breakfast);

            AddOn parking = new AddOn();
            parking.setAddOnName("Parking");
            parking.setBasePrice(25.0);
            parking.setDescription("Secure underground parking");
            parking.setPricingModel("PER_NIGHT");
            parking.setActive("Y");
            em.persist(parking);

            AddOn spa = new AddOn();
            spa.setAddOnName("Spa");
            spa.setBasePrice(60.0);
            spa.setDescription("Full-day access to pools, sauna & treatments");
            spa.setPricingModel("PER_RESERVATION");
            spa.setActive("Y");
            em.persist(spa);

//            Room room1 = new Room();
//            room1.setRoomNumber("101");
//            room1.setFloor(1);
//            room1.setRoomType(singleRoom);
//            em.persist(room1);
//
//            Room room2 = new Room();
//            room2.setRoomNumber("201");
//            room2.setFloor(2);
//            room2.setRoomType(doubleRoom);
//            em.persist(room2);
//
//            Room room3 = new Room();
//            room3.setRoomNumber("301");
//            room3.setFloor(3);
//            room3.setRoomType(deluxeRoom);
//            em.persist(room3);
//
//            Room room4 = new Room();
//            room4.setRoomNumber("401");
//            room4.setFloor(4);
//            room4.setRoomType(penthouse);
//            em.persist(room4);

            Long count = em.createQuery("SELECT COUNT(rt) FROM RoomType rt", Long.class)
                    .getSingleResult();
            System.out.println("RoomType count = " + count);
            System.out.println("Seed complete.");
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }
}
