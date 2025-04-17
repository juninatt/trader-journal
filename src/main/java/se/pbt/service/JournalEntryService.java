package se.pbt.service;

import se.pbt.model.JournalEntry;
import se.pbt.repository.JournalEntryRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Handles interaction with the persistence layer for journal entries.
 * Acts as a facade between UI/business layers and the database.
 * <p>
 * Also delegates analysis tasks to {@link JournalAnalysisService} for snapshot-related calculations.
 * </p>
 */
public class JournalEntryService {

    private final JournalEntryRepository journalRepo;
    private final JournalAnalysisService analysisService;

    /**
     * Constructs a JournalService with the given repositories and analysis service.
     */
    public JournalEntryService(JournalEntryRepository journalRepo, JournalAnalysisService analysisService) {
        this.journalRepo = journalRepo;
        this.analysisService = analysisService;
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
     * Calculates the total change in currency for a specific journal entry.
     */
    public BigDecimal getTotalChangeForEntry(Long id) {
        return journalRepo.findById(id)
                .map(analysisService::calculateTotalChangeKr)
                .orElse(BigDecimal.ZERO);
    }
}
