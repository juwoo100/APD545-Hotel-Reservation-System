package ca.seneca.application.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Singleton EntityManagerFactory — created once, shared across the entire app.
 */
public class Database {

    private static Database instance;
    private final EntityManagerFactory emf;

    private Database() {
        emf = Persistence.createEntityManagerFactory("hotel-reservation-pu");
    }

    public static synchronized Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }

    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    public void close() {
        if (emf != null && emf.isOpen()) emf.close();
    }
}
