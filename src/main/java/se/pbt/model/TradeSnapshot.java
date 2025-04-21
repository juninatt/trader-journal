package se.pbt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Represents a daily snapshot of an asset held within a trade.
 * <p>
 * A {@code TradeSnapshot} captures the state of a trade on a particular day, including price values, quantity,
 * transaction fees, and optional buy/sell timestamps. Multiple snapshots can be linked to a single {@link Trade}
 * to track its performance over time. Snapshots are indirectly linked to a {@link JournalEntry} through their parent trade.
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

    @NotBlank(message = "Asset name is required")
    private String assetName;

    @NotBlank(message = "Asset type is required, e.g. STOCK, ETF, FOND")
    private String assetType;

    /**
     * The time of day the asset was purchased.
     * Combined with the journal entry date to determine the full buy timestamp.
     */
    private LocalTime buyTime;

    /**
     * The time of day the asset was sold.
     * Used to calculate trade duration and detect weekend spans.
     */
    private LocalTime sellTime;

    /**
     * The asset's value at the time of purchase.
     * Used as the trade’s buy value if this snapshot is the first in the trade.
     */
    @NotNull(message = "Start value is required")
    @Positive(message = "Start value must be positive")
    private BigDecimal startValue;

    /**
     * The asset's value at the time of sale.
     * Used as the trade’s sell value if this snapshot is the last in the trade.
     */
    @PositiveOrZero(message = "End value must be positive")
    private BigDecimal endValue;

    @NotNull
    @Positive(message = "Quantity must be at least 1")
    private int quantity;

    @NotNull
    @PositiveOrZero(message = "Buy fee cannot be negative")
    private double buyFee; // TODO: Move to Trade?

    @NotNull
    @PositiveOrZero(message = "Sell fee cannot be negative")
    private double sellFee; // TODO: Move to Trade?

    /**
     * Any notes related to this specific snapshot, e.g. strategy or emotions.
     */
    private String notes;

    /**
     * The trade this snapshot belongs to. Required.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "trade_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Trade trade;
}
