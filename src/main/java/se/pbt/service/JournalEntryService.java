package se.pbt.service;

import se.pbt.model.JournalEntry;
import se.pbt.repository.JournalEntryRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for working with journal entries and related trades.
 */
public class JournalEntryService {

    private final JournalEntryRepository journalRepo;

    /**
     * Constructs a JournalService with the given repositories and analysis service.
     */
    public JournalEntryService(JournalEntryRepository journalRepo) {
        this.journalRepo = journalRepo;
    }

    /**
     * Saves a journal entry to the database.
     */
    public void save(JournalEntry entry) {
        journalRepo.save(entry);
    }

    /**
     * Retrieves all journal entries from the database.
     */
    public List<JournalEntry> getAllEntries() {
        return journalRepo.findAll();
    }

    /**
     * Retrieves a single journal entry by ID.
     */
    public Optional<JournalEntry> findById(Long id) {
        return journalRepo.findById(id);
    }

    /**
     * Retrieves the most recent journal entry if one exists.
     */
    public Optional<JournalEntry> getLatestEntry() {
        return journalRepo.findLatestEntry();
    }
}
