package se.pbt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a daily journal entry that logs all trading activity for a specific date.
 * <p>
 * It encapsulates commentary, available cash, invested capital, and all {@link TradeSnapshot}s created on that day.
 * </p>
 * <p>
 * TradeSnapshots within this entry describe the daily state of trades being tracked or updated.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Optional commentary or reflection tied to this journal entry.
     * Can include trade rationale, market observations, or emotional state.
     */
    @Column(length = 5000)
    private String entryText;

    /**
     * The amount of cash available in the account at the end of the day.
     * Can be negative due to fees, leverage, or foreign exchange effects.
     */
    @NotNull(message = "Cash amount is required")
    @Column(precision = 15, scale = 2)
    private BigDecimal availableCash;

    /**
     * The total amount of capital invested in trades as of this journal entry.
     * Can be negative when using leveraged or inverse instruments.
     * <p>
     * Calculated from all {@link TradeSnapshot}s associated with the entry.
     * </p>
     * TODO: Implement logic to calculate based on trade snapshots.
     */
    @NotNull(message = "Invested amount required")
    @Column(precision = 15, scale = 2)
    private BigDecimal investedCapital;

    /**
     * The calendar date this journal entry corresponds to.
     * Each entry must be unique per date.
     */
    @NotNull(message = "Journal date is required")
    private LocalDate date;


    /**
     * All snapshots of trades recorded for this day.
     * Each snapshot represents a trade’s state on this specific date.
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TradeSnapshot> tradeSnapshots = new HashSet<>();



    /**
     * Adds a {@link TradeSnapshot} to this journal entry and sets the back-reference.
     * TODO: Improve error handling
     */
    public void addTradeSnapshot(TradeSnapshot snapshot) {
        if (snapshot != null) {
            if (snapshot.getJournalEntry() != null && snapshot.getJournalEntry() != this) {
                throw new IllegalStateException("Snapshot already belongs to another JournalEntry");
            }
            snapshot.setJournalEntry(this);
            tradeSnapshots.add(snapshot);
        }
    }


    /**
     * Returns a distinct set of {@link Trade} instances associated with this journal entry.
     * <p>
     * Since trades are not stored directly on the entry, this method extracts them
     * by traversing the associated {@link TradeSnapshot}s and collecting their {@link Trade} references.
     * </p>
     */
    public Set<Trade> getTrades() {
        return tradeSnapshots.stream()
                .map(TradeSnapshot::getTrade)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}