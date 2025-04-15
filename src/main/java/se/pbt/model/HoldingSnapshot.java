package se.pbt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @NotNull(message = "Buy datetime is required")
    private LocalDateTime buyDateTime;

    private LocalDateTime sellDateTime;

    @NotNull(message = "Start value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Start value must be positive")
    private BigDecimal startValue;

    private BigDecimal endValue;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @PositiveOrZero(message = "Buy fee cannot be negative")
    private double buyFee;

    @PositiveOrZero(message = "Sell fee cannot be negative")
    private double sellFee;

    @ManyToOne
    @JoinColumn(name = "journal_entry_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private JournalEntry journalEntry;
}
