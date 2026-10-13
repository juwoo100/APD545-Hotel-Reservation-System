package ca.seneca.application.repository;

import ca.seneca.application.db.Database;
import ca.seneca.application.model.AdminUser;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class AdminUserRepository {
    private final Database db = Database.getInstance();

    public AdminUser persist(AdminUser u) {
        EntityManager em = db.createEntityManager();
        try { em.getTransaction().begin(); em.persist(u);
              em.getTransaction().commit(); return u; }
        catch (Exception e) { em.getTransaction().rollback(); throw e; }
        finally { em.close(); }
    }

    public Optional<AdminUser> findByUsername(String username) {
        EntityManager em = db.createEntityManager();
        try { List<AdminUser> r = em.createQuery(
            "SELECT a FROM AdminUser a WHERE a.username=:u", AdminUser.class)
            .setParameter("u", username).getResultList();
              return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0)); }
        finally { em.close(); }
    }

    public long count() {
        EntityManager em = db.createEntityManager();
        try { return em.createQuery("SELECT COUNT(a) FROM AdminUser a", Long.class).getSingleResult(); }
        finally { em.close(); }
    }
}
