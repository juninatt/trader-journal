package se.pbt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoldingSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Asset name is required")
    private String assetName;

    @NotBlank(message = "Asset type is required, e.g. STOCK, ETF, FOND")
    private String assetType;

    private LocalTime buyTime;

    private LocalTime sellTime;

    @NotNull(message = "Start value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Start value must be positive")
    private BigDecimal startValue; // TODO: Change field name

    private BigDecimal endValue; // TODO: Change field name

    @Positive(message = "Quantity must be at+ least 1")
    private int quantity;

    @PositiveOrZero(message = "Buy fee cannot be negative")
    private double buyFee;

    @PositiveOrZero(message = "Sell fee cannot be negative")
    private double sellFee;

    private String notes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private JournalEntry journalEntry;
}
