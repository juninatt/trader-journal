package se.pbt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Represents a partial or full sale of a trade’s asset on a specific day.
 * <p>
 * An {@code ExecutedSale} is tied to a {@link TradeSnapshot}, recording quantity sold, price per unit,
 * fee, and calculated gross/net gain for that specific execution.
 * </p>
 * <p>
 * Sales are recorded within snapshots and contribute to the trade’s overall performance.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutedSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The number of units sold in this transaction.
     * Decreases the remaining quantity of the parent {@link TradeSnapshot}.
     */
    @NotNull(message = "Sold quantity is required")
    @Positive(message = "Quantity sold must be greater than 0")
    private int quantitySold;

    /**
     * The price per unit at which the asset was sold.
     * Used to calculate gross revenue before deducting any fees.
     */
    @NotNull(message = "Sell price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Sell price must be greater than 0")
    @Column(precision = 10, scale = 4)
    private BigDecimal sellPrice;

    /**
     * The fee paid for this specific sale transaction.
     * Used to calculate the net gain of the sale.
     */
    @NotNull(message = "Sell fee is required")
    @DecimalMin(value = "0.0", message = "Sell fee cannot be negative")
    @Column(precision = 6, scale = 2)
    private BigDecimal sellFee;

    /**
     * Gross gain from the sale, calculated as {@code quantitySold × pricePerUnit}.
     */
    @NotNull(message = "Gross gain is required")
    @DecimalMin(value = "0.0", message = "Gross gain cannot be negative")
    @Column(precision = 12, scale = 4)
    private BigDecimal grossGain;

    /**
     * Net gain from the sale, calculated as {@code grossGain - sellFee}.
     * Can be negative if sold at a loss or fees exceed proceeds.
     */
    @NotNull(message = "Net gain is required")
    @Column(precision = 12, scale = 4)
    private BigDecimal netGain;

    /**
     * The time of day the sale was executed.
     * Combined with the JournalEntry date (via snapshot) to determine the full timestamp.
     */
    @NotNull
    private LocalTime sellTime;


    /**
     * The snapshot this sale belongs to.
     * Connects the sale to its corresponding day and trade.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "snapshot_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TradeSnapshot tradeSnapshot;
}
