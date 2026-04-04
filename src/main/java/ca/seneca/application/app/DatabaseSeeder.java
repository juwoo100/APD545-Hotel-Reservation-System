package ca.seneca.application.app;

import ca.seneca.application.model.Room;
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

            RoomType single = new RoomType();
            single.setTypeName("Single");
            single.setCapacity(2);
            single.setBasePrice(100.0);
            em.persist(single);

            RoomType doubleRoom = new RoomType();
            doubleRoom.setTypeName("Double");
            doubleRoom.setCapacity(4);
            doubleRoom.setBasePrice(180.0);
            em.persist(doubleRoom);

            RoomType penthouse = new RoomType();
            penthouse.setTypeName("Penthouse");
            penthouse.setCapacity(6);
            penthouse.setBasePrice(300.0);
            em.persist(penthouse);

            Room room1 = new Room();
            room1.setRoomNumber("101");
            room1.setFloor(1);
            room1.setRoomType(single);
            em.persist(room1);

            Room room2 = new Room();
            room2.setRoomNumber("201");
            room2.setFloor(2);
            room2.setRoomType(doubleRoom);
            em.persist(room2);

            Room room3 = new Room();
            room3.setRoomNumber("301");
            room3.setFloor(3);
            room3.setRoomType(penthouse);
            em.persist(room3);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }
}
