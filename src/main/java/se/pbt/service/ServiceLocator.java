package se.pbt.service;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import se.pbt.repository.JournalEntryRepositoryImpl;

/**
 * Centralized registry for shared service instances used throughout the application.
 * <p>
 * This class provides a lightweight alternative to full dependency injection frameworks
 * and ensures services are initialized lazily and reused across the application.
 * <\p>
 */
public class ServiceLocator {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("trader-journal-pu");

    private static JournalEntryService journalEntryService;

    /**
     * Returns a shared instance of {@link JournalEntryService}, initialized lazily.
     */
    public static JournalEntryService getJournalEntryService() {
        if (journalEntryService == null) {
            journalEntryService = new JournalEntryService(
                    new JournalEntryRepositoryImpl(emf),
                    new JournalAnalysisService()
            );
        }
        return journalEntryService;
    }

    /**
     * Closes the {@link EntityManagerFactory} when the application shuts down.
     */
    public static void shutdown() {
        if (emf.isOpen()) {
            emf.close(); // TODO: Implement safe shutdown
        }
    }

    // Prevent instantiation
    private ServiceLocator() {}
}
