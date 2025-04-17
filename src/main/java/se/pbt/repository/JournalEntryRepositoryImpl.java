package se.pbt.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import se.pbt.model.JournalEntry;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link JournalEntryRepository} interface using JPA and an {@link EntityManagerFactory}.
 * <p>
 * Provides CRUD operations for {@link JournalEntry} entities, ensuring that a new {@link EntityManager}
 * is created and closed for each database interaction.
 * </p>
 */
public class JournalEntryRepositoryImpl implements JournalEntryRepository {

    private final EntityManagerFactory emf;

    public JournalEntryRepositoryImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Persists a new {@link JournalEntry} to the database.
     */
    @Override
    public void save(JournalEntry entry) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(entry);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Removes the given {@link JournalEntry} from the database if it exists.
     */
    @Override
    public boolean remove(JournalEntry entry) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            JournalEntry managed = em.find(JournalEntry.class, entry.getId());
            if (managed != null) {
                em.remove(managed);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // TODO: Add logging
        } finally {
            em.close();
        }
    }


    /**
     * Retrieves a {@link JournalEntry} by its ID, if it exists.
     */
    @Override
    public Optional<JournalEntry> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        JournalEntry found = em.find(JournalEntry.class, id);
        em.close();
        return Optional.ofNullable(found);
    }

    /**
     * Retrieves all {@link JournalEntry} records from the database.
     */
    @Override
    public List<JournalEntry> findAll() {
        EntityManager em = emf.createEntityManager();
        List<JournalEntry> result = em
                .createQuery("SELECT j FROM JournalEntry j", JournalEntry.class)
                .getResultList();
        em.close();
        return result;
    }
}
