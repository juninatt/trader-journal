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
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public Optional<JournalEntry> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            List<JournalEntry> results = em.createQuery("""
                    SELECT j
                    FROM JournalEntry j
                    LEFT JOIN FETCH j.tradeSnapshots ts
                    LEFT JOIN FETCH ts.trade t
                    LEFT JOIN FETCH t.asset
                    WHERE j.id = :id
                """, JournalEntry.class)
                    .setParameter("id", id)
                    .getResultList();
            return results.stream().findFirst();
        } finally {
            em.close();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<JournalEntry> findAll() {
        EntityManager em = emf.createEntityManager();
        List<JournalEntry> result = em.createQuery("""
                    SELECT DISTINCT j
                    FROM JournalEntry j
                    LEFT JOIN FETCH j.tradeSnapshots ts
                    LEFT JOIN FETCH ts.trade t
                    LEFT JOIN FETCH t.asset
                    ORDER BY j.date DESC
                """, JournalEntry.class)
                .getResultList();
        em.close();
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<JournalEntry> findLatestEntry() {
        EntityManager em = emf.createEntityManager();
        JournalEntry latest = em.createQuery("""
                    SELECT j
                    FROM JournalEntry j
                    LEFT JOIN FETCH j.tradeSnapshots ts
                    LEFT JOIN FETCH ts.trade t
                    LEFT JOIN FETCH t.asset
                    ORDER BY j.date DESC
                """, JournalEntry.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
        em.close();
        return Optional.ofNullable(latest);
    }
}
