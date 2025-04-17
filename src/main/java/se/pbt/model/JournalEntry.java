package se.pbt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Comment is required")
    private String comment;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<HoldingSnapshot> snapshots = new ArrayList<>();

    private String notes;

    /**
     * The amount of cash held on the trading account for this journal day, not held up in assets.
     */
    private BigDecimal cashBalance;

    public void addSnapshot(HoldingSnapshot snapshot) {
        if (snapshot != null) {
            if (snapshot.getJournalEntry() != null && snapshot.getJournalEntry() != this) {
                throw new IllegalStateException("Snapshot already belongs to another JournalEntry");
            }
            snapshot.setJournalEntry(this);
            snapshots.add(snapshot);
        }
    }

    public void removeSnapshot(HoldingSnapshot snapshot) {
        if (snapshots.remove(snapshot)) {
            snapshot.setJournalEntry(null);
        }
    }
}