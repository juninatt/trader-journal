package se.pbt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a daily snapshot of a trade’s state.
 * <p>
 * A {@code TradeSnapshot} belongs to a {@link Trade}, capturing market data, remaining quantity,
 * and any {@link ExecutedSale}s made on that day.
 * </p>
 * <p>
 * It is also part of a {@link JournalEntry}, linking the trade’s daily evolution to a specific date.
 * </p>
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The number of units still held at the time of this snapshot.
     * Decreases as {@link ExecutedSale}s are performed.
     */
    @NotNull(message = "Remaining quantity is required")
    @PositiveOrZero(message = "Remaining quantity cannot be negative")
    private int remainingQuantity;

    /**
     * The market opening price per unit of the asset on this day.
     */
    @NotNull(message = "Opening price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Opening price must be greater than 0")
    @Column(precision = 10, scale = 4)
    private BigDecimal openPrice;

    /**
     * The market closing price per unit of the asset on this day.
     */
    @NotNull(message = "Closing price is required")
    @DecimalMin(value = "0.0", message = "Closing price cannot be negative")
    @Column(precision = 10, scale = 4)
    private BigDecimal closePrice;

    /**
     * Optional notes tied to this specific day of the trade.
     * Can include reflections, strategies, or external market factors.
     */
    private String notes;

    /**
     * The trade this snapshot belongs to.
     * Provides access to original quantity and aggregate trade data.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "trade_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Trade trade;

    /**
     * The journal entry (date) this snapshot is associated with.
     * Links the snapshot to the specific day it was recorded.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;

    /**
     * Executed sales that occurred on this day as part of the ongoing trade.
     * Each sale is tied to this snapshot and reflects actual sell transactions.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "snapshot_id")
    @ToString.Exclude
    @Builder.Default
    private Set<ExecutedSale> executedSales = new HashSet<>();
}
