package ca.seneca.application.repository;

import ca.seneca.application.enums.RoomAvailabilityStatus;
import ca.seneca.application.model.Room;
import jakarta.persistence.EntityManager;

import java.util.List;

public class RoomRepository {

    public Room save(EntityManager em, Room room) {
        if (room.getRoomId() == null) {
            em.persist(room);
            return room;
        }
        return em.merge(room);
    }

    public Room findById(EntityManager em, Integer id) {
        return em.find(Room.class, id);
    }

    public List<Room> findAll(EntityManager em) {
        return em.createQuery("SELECT r FROM Room r", Room.class)
                .getResultList();
    }

    public List<Room> findAvailableRoomsByTypeName(EntityManager em, String typeName) {
        return em.createQuery("""
                SELECT r
                FROM Room r
                WHERE LOWER(r.roomType.typeName) = :typeName
                  AND r.availabilityStatus = :status
                ORDER BY r.roomNumber
                """, Room.class)
                .setParameter("typeName", typeName.toLowerCase())
                .setParameter("status", RoomAvailabilityStatus.AVAILABLE)
                .getResultList();
    }
}