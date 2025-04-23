package se.pbt.model.asset;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import se.pbt.model.Trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Describes a financial instrument used in trading, such as a stock, ETF, fund, or certificate.
 * <p>
 * An {@code Asset} defines key attributes of a tradable security — including its classification,
 * market, currency, and metadata — and serves as the foundational reference for any {@link Trade}.
 * </p>
 * <p>
 * While each trade is built around a single asset, an asset has no independent role in the system
 * outside the context of a trade.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The full name of the asset (e.g., Saab B, Latour).
     */
    @NotBlank(message = "Asset name is required")
    private String name;

    /**
     * The trading symbol or ticker (e.g., SAAB-B.ST, LATO-B.ST).
     */
    @NotBlank(message = "Ticker is required")
    private String ticker;

    /**
     * International Securities Identification Number (ISIN).
     * Used for global uniqueness.
     */
    @NotBlank(message = "ISIN is required")
    @Column(length = 20)
    private String isin;

    /**
     * Classification of the asset (e.g., STOCK, ETF, FUND).
     */
    @NotNull(message = "Asset class is required")
    @Enumerated(EnumType.STRING)
    private AssetClass assetClass;

    /**
     * The currency in which the asset is traded, following the ISO 4217 standard (e.g., SEK, USD, EUR).
     */
    @NotNull(message = "Currency is required")
    private Currency currency;

    /**
     * The exchange where the asset is listed (e.g., NASDAQ, NYSE, Stockholm)
     */
    @NotNull(message = "Exchange is required")
    @Enumerated(EnumType.STRING)
    private Exchange exchange;


    /**
     * Indicates whether the asset uses leverage (e.g., Bull/Bear certificates).
     */
    private boolean isLeveraged;

    /**
     * Indicates whether the asset represents an investment company.
     * Used to differentiate from ordinary stocks.
     */
    @Column(nullable = false)
    private boolean isInvestmentCompany;


    /**
     * The leverage multiplier, if applicable. (e.g., 2.0 = 2x leverage).
     */
    @DecimalMin(value = "1.0", message = "Leverage ratio must be at least 1.0")
    @Column(precision = 4, scale = 2)
    private BigDecimal leverageRatio;

    /**
     * Annual dividend yield in percentage (e.g., 4.25 means 4.25%).
     */
    @DecimalMin(value = "0.0", message = "Dividend yield cannot be negative")
    @Column(precision = 5, scale = 2)
    private BigDecimal dividendYield;

    /**
     * Sectors the asset belongs to (e.g., Technology, Financials).
     */
    @ElementCollection
    private List<@NotBlank Sector> sectors;

    /**
     * Industries the asset is associated with (e.g., Semiconductors).
     */
    @ElementCollection
    private List<@NotNull Industry> industries;

    /**
     * Timestamp of the last update to the asset information.
     */
    @NotNull(message = "Last updated time is required")
    private LocalDateTime lastUpdated;

    /**
     * All trades in which this asset has been used.
     * Assets may be shared across multiple trades but are never used standalone.
     */
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Trade> trades = new HashSet<>();

    /**
     * Automatically sets or updates the {@code lastUpdated} timestamp
     * before the entity is persisted or updated in the database.
     */
    @PrePersist
    @PreUpdate
    private void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }
}
