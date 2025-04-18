package se.pbt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single journal entry for a specific trading day.
 * <p>
 * A {@code JournalEntry} encapsulates the full trading context of a single date, including optional notes
 * and the cash balance not invested in assets. It aggregates one or more {@link Trade} instances,
 * each of which may contain multiple {@link TradeSnapshot}s representing daily views of an individual trade.
 * This class serves as the root entity for tracking and analyzing trades on a given day.
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

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Notes are required")
    private String notes;

    /**
     * The set of trades associated with this journal entry.
     * Each trade can contain one or more snapshots representing the progression of an asset.
     */
    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Trade> trades = new HashSet<>();

    /**
     * The amount of cash held on the trading account for this journal day, not held up in assets.
     */
    private BigDecimal cashBalance;

    /**
     * Adds a trade to the journal entry and sets the back-reference.
     */
    public void addTrade(Trade trade) {
        if (trade != null) {
            if (trade.getJournalEntry() != null && trade.getJournalEntry() != this) {
                throw new IllegalStateException("Trade already belongs to another JournalEntry");
            }
            trade.setJournalEntry(this);
            trades.add(trade);
        }
    }

    /**
     * Removes a trade from the journal entry and clears the back-reference.
     */
    public void removeTrade(Trade trade) {
        if (trades.remove(trade)) {
            trade.setJournalEntry(null);
        }
    }
}