package se.pbt.repository;

import se.pbt.model.JournalEntry;

import java.util.List;
import java.util.Optional;

/**
 * Interface for basic CRUD operations related to {@link JournalEntry}.
 */
public interface JournalEntryRepository {

    /**
     * Saves the given {@link JournalEntry} to the database.
     */
    void save(JournalEntry entry);

    boolean remove(JournalEntry entry);
    /**
     * Retrieves a {@link JournalEntry} by its ID.
     */
    Optional<JournalEntry> findById(Long id);

    /**
     * Retrieves all {@link JournalEntry} records stored in the database.
     */
    List<JournalEntry> findAll();

    /**
     * Retrieves the most recently created {@link JournalEntry} based on date.
     */
    Optional<JournalEntry> findLatestEntry();

}

