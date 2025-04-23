package se.pbt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the full lifecycle of a trade, from purchase to final sale.
 * <p>
 * A {@code Trade} tracks the traded asset, quantity, entry/exit times, and aggregated results.
 * It consists of one or more {@link TradeSnapshot}s, each capturing a daily view of the trade.
 * </p>
 * <p>
 * {@link ExecutedSale}s are tied to snapshots and represent partial sales occurring on specific days,
 * contributing to this trade’s cumulative performance.
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

    /**
     * The name of the traded asset, e.g., "Apple", "Tesla", or "SP500 ETF".
     * Used for display and grouping purposes.
     */
    @NotBlank(message = "Asset name is required")
    private String asset;

    /**
     * The total number of units initially purchased in this trade.
     * This value remains constant and is referenced by all snapshots.
     */
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private int quantity;

    /**
     * The transaction fee paid at the time of purchase.
     * Used in net gain calculations.
     */
    @NotNull(message = "Buy fee is required")
    @DecimalMin(value = "0.0", message = "Buy fee cannot be negative")
    @Column(precision = 6, scale = 2)
    private BigDecimal buyFee;

    /**
     * The asset's entry price per unit at the time of purchase.
     * Derived from the first snapshot.
     */
    @NotNull(message = "Entry price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Entry price must be greater than 0")
    @Column(precision = 10, scale = 4)
    private BigDecimal entryPrice;

    /**
     * The asset's exit price per unit at the time of full sale.
     * Derived from the final snapshot or last executed sale.
     */
    @DecimalMin(value = "0.0", message = "Exit price cannot be negative")
    @Column(precision = 10, scale = 4)
    private BigDecimal exitPrice;

    /**
     * The time of day when the trade was initiated (i.e., when the asset was purchased).
     * Used together with the journal date to determine the full entry timestamp.
     */
    private LocalTime entryTime;

    /**
     * The time of day when the trade was fully exited (i.e., all units were sold).
     * Derived from the final {@link ExecutedSale}.
     */
    private LocalTime exitTime;


    /**
     * All snapshots associated with this trade.
     * Each snapshot represents the state of the trade on a particular day.
     */
    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TradeSnapshot> tradeSnapshots = new HashSet<>();

    /**
     * Adds a {@link TradeSnapshot} to this trade and sets the back-reference.
     * TODO: Improve error handling
     */
    public void addSnapshot(TradeSnapshot snapshot) {
        if (snapshot != null) {
            if (snapshot.getTrade() != null && snapshot.getTrade() != this) {
                throw new IllegalStateException("Snapshot already belongs to another trade");
            }
            snapshot.setTrade(this);
            tradeSnapshots.add(snapshot);
        }
    }
}
