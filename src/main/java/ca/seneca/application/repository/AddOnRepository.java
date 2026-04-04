package ca.seneca.application.repository;

import ca.seneca.application.model.AddOn;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AddOnRepository {


    public AddOn save(EntityManager em, AddOn addOn) {
        if (addOn.getAddOnId() == null) {
            em.persist(addOn);
            return addOn;
        }
        return em.merge(addOn);
    }

    public AddOn findById(EntityManager em, Integer id) {
        return em.find(AddOn.class, id);
    }

    public List<AddOn> findAll(EntityManager em) {
        return em.createQuery("SELECT a FROM AddOn a", AddOn.class)
                .getResultList();
    }

    public AddOn findByName(EntityManager em, String name) {
        List<AddOn> results = em.createQuery("""
                SELECT a
                FROM AddOn a
                WHERE LOWER(a.name) = :name
                """, AddOn.class)
                .setParameter("name", name.toLowerCase())
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }
}