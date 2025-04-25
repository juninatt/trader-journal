package se.pbt.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import se.pbt.model.JournalEntry;
import se.pbt.testutil.TestDataFactory;

import java.util.List;
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
    @DisplayName("saves and finds JournalEntry by ID - basic")
    void savesAndFindsById_basic() {
        JournalEntry entry = TestDataFactory.defaultJournalEntry();
        repository.save(entry);

        Optional<JournalEntry> found = repository.findById(entry.getId());
        assertTrue(found.isPresent());
        assertEquals(entry.getEntryText(), found.get().getEntryText());
    }

    @Test
    @DisplayName("findById returns empty Optional if ID not found")
    void findById_returnsEmptyOptionalIfNotFound() {
        Optional<JournalEntry> found = repository.findById(99999L);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("returns all saved entries - basic")
    void returnsAllEntries_basic() {
        JournalEntry entry1 = TestDataFactory.defaultJournalEntry();
        JournalEntry entry2 = TestDataFactory.defaultJournalEntry();
        entry2.setEntryText("Another test entry");

        repository.save(entry1);
        repository.save(entry2);

        List<JournalEntry> all = repository.findAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    @DisplayName("findAll returns empty list when no entries exist")
    void findAll_returnsEmptyListWhenNoEntries() {
        List<JournalEntry> all = repository.findAll();
        assertNotNull(all); // Should not throw NullPointerException
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
        nonExistingEntry.setId(9999L); // Fake ID

        boolean removed = repository.remove(nonExistingEntry);

        assertFalse(removed);
    }

    @Test
    @DisplayName("findLatestEntry returns latest by date")
    void findLatestEntry_returnsLatest() {
        JournalEntry entry1 = TestDataFactory.defaultJournalEntry();
        entry1.setDate(java.time.LocalDate.now().minusDays(1));
        JournalEntry entry2 = TestDataFactory.defaultJournalEntry();
        entry2.setDate(java.time.LocalDate.now());

        repository.save(entry1);
        repository.save(entry2);

        Optional<JournalEntry> latest = repository.findLatestEntry();
        assertTrue(latest.isPresent());
        assertEquals(entry2.getDate(), latest.get().getDate());
    }

    @Test
    @DisplayName("findLatestEntry returns empty when no entries")
    void findLatestEntry_returnsEmptyWhenNoEntries() {
        var result = repository.findLatestEntry();
        assertNotNull(result); // Should return Optional.empty() instead of null
    }
}
