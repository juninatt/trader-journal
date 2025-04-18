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

    @NotNull(message = "Notes are required")
    private String notes;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Trade> trades = new ArrayList<>();

    /**
     * The amount of cash held on the trading account for this journal day, not held up in assets.
     */
    private BigDecimal cashBalance;

    public void addTrade(Trade trade) {
        if (trade != null) {
            if (trade.getJournalEntry() != null && trade.getJournalEntry() != this) {
                throw new IllegalStateException("Trade already belongs to another JournalEntry");
            }
            trade.setJournalEntry(this);
            trades.add(trade);
        }
    }

    public void removeTrade(Trade trade) {
        if (trades.remove(trade)) {
            trade.setJournalEntry(null);
        }
    }
}
