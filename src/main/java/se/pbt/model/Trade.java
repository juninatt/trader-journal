package se.pbt.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a complete trade lifecycle, from initial purchase to eventual sale.
 * <p>
 * A {@code Trade} models a logical unit of a buy-and-sell process and may span across multiple days.
 * It is composed of one or more {@link TradeSnapshot} instances that represent daily snapshots of the trade's progress.
 * Each Trade is associated with a specific {@link JournalEntry} that defines the date context for its snapshots.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    /**
     * The snapshots that make up this trade. Each snapshot captures a day's state of the asset.
     */
    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<TradeSnapshot> snapshots = new HashSet<>();

    /**
     * The journal entry that this trade belongs to.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private JournalEntry journalEntry;

    /**
     * Adds a snapshot to the trade and sets the back-reference.
     */
    public void addSnapshot(TradeSnapshot snapshot) {
        if (snapshot != null) {
            if (snapshot.getTrade() != null && snapshot.getTrade() != this) {
                throw new IllegalStateException("Snapshot already belongs to another trade");
            }
            snapshot.setTrade(this);
            snapshots.add(snapshot);
        }
    }

    /**
     * Removes a snapshot from the trade and clears the back-reference.
     */
    public void removeSnapshot(TradeSnapshot snapshot) {
        if (snapshots.remove(snapshot)) {
            snapshot.setTrade(null);
        }
    }
}