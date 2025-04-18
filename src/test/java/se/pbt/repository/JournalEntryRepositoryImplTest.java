package se.pbt.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import se.pbt.model.JournalEntry;
import se.pbt.testutil.TestDataFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JournalEntryRepositoryImplTest {

    private EntityManagerFactory emf;
    private JournalEntryRepositoryImpl repository;

    @BeforeAll
    void setup() {
        emf = Persistence.createEntityManagerFactory("trader-journal-test-pu");
        repository = new JournalEntryRepositoryImpl(emf);
    }

    @AfterAll
    void teardown() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    @DisplayName("saves and finds JournalEntry by ID")
    void savesAndFindsById() {
        JournalEntry entry = TestDataFactory.defaultJournalEntry();
        repository.save(entry);

        Optional<JournalEntry> found = repository.findById(entry.getId());
        assertTrue(found.isPresent());
        assertEquals(entry.getNotes(), found.get().getNotes());
    }

    @Test
    @DisplayName("returns all saved entries")
    void returnsAllEntries() {
        JournalEntry entry1 = TestDataFactory.defaultJournalEntry();
        JournalEntry entry2 = TestDataFactory.defaultJournalEntry();
        entry2.setNotes("Second test entry");

        repository.save(entry1);
        repository.save(entry2);

        var all = repository.findAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    @DisplayName("removes saved JournalEntry and returns true")
    void removesSavedEntrySuccessfully() {
        JournalEntry entry = TestDataFactory.defaultJournalEntry();
        repository.save(entry);

        boolean removed = repository.remove(entry);
        Optional<JournalEntry> result = repository.findById(entry.getId());

        assertTrue(removed);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("returns false when attempting to remove unsaved JournalEntry")
    void returnsFalseWhenRemovingNonExistentEntry() {
        JournalEntry nonExistingEntry = TestDataFactory.defaultJournalEntry();
        nonExistingEntry.setId(9999L); // ID not managed or persisted

        boolean removed = repository.remove(nonExistingEntry);

        assertFalse(removed);
    }

    @Test
    @DisplayName("throws exception and rolls back if remove is called with null ID")
    void throwsAndRollsBackOnInvalidRemoveCall() {
        JournalEntry entry = TestDataFactory.defaultJournalEntry();
        entry.setId(null); // Triggers IllegalArgumentException inside EntityManager.find

        assertThrows(IllegalArgumentException.class, () -> repository.remove(entry));
    }


}
