package se.pbt.testutil;

import se.pbt.model.JournalEntry;
import se.pbt.model.Trade;
import se.pbt.model.TradeSnapshot;
import se.pbt.model.asset.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;

/**
 * Utility class for creating domain test objects with predefined default values.
 * <p>
 * Useful in unit tests to generate {@link JournalEntry}, {@link Trade}, {@link TradeSnapshot}, and {@link Asset}
 * instances without duplicating initialization logic.
 */
public class TestDataFactory {

    public static JournalEntry defaultJournalEntry() {
        Trade trade = defaultTrade();
        JournalEntry entry = JournalEntry.builder()
                .date(LocalDate.of(2025, 4, 13))
                .entryText("Default test journal entry")
                .availableCash(new BigDecimal("10000.00"))
                .investedCapital(new BigDecimal("5000.00"))
                .build();

        TradeSnapshot snapshot = defaultTradeSnapshot(trade, entry);
        entry.addTradeSnapshot(snapshot);

        return entry;
    }

    public static JournalEntry emptyJournalEntry() {
        return JournalEntry.builder()
                .date(LocalDate.of(2025, 4, 13))
                .entryText("Empty test journal entry")
                .availableCash(new BigDecimal("0.00"))
                .investedCapital(new BigDecimal("0.00"))
                .build();
    }

    public static TradeSnapshot defaultTradeSnapshot(Trade trade, JournalEntry entry) {
        TradeSnapshot snapshot = TradeSnapshot.builder()
                .remainingQuantity(1)
                .openPrice(new BigDecimal("100.00"))
                .closePrice(new BigDecimal("110.00"))
                .notes("Default snapshot notes")
                .journalEntry(entry)
                .trade(trade)
                .build();

        trade.addSnapshot(snapshot);
        return snapshot;
    }

    /**
     * Returns a minimal {@link TradeSnapshot} with no executed sales.
     * Useful for tests where only a raw snapshot structure is needed.
     */
    public static TradeSnapshot emptyTradeSnapshot() {
        return TradeSnapshot.builder()
                .remainingQuantity(1)
                .openPrice(new BigDecimal("100.00"))
                .closePrice(new BigDecimal("100.00"))
                .notes("Empty snapshot")
                .build();
    }

    public static Asset defaultAsset() {
        return Asset.builder()
                .name("Default Asset")
                .ticker("DEF.ST")
                .assetClass(AssetClass.STOCK)
                .isin("SE0000000001")
                .currency(Currency.getInstance("SEK"))
                .exchange(Exchange.STOCKHOLM)
                .isLeveraged(false)
                .isInvestmentCompany(false)
                .leverageRatio(new BigDecimal("1.0"))
                .dividendYield(new BigDecimal("2.5"))
                .sectors(List.of(Sector.TECHNOLOGY))
                .industries(List.of(Industry.SOFTWARE))
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    public static Trade defaultTrade() {
        return Trade.builder()
                .quantity(1)
                .buyFee(new BigDecimal("0.00"))
                .entryPrice(new BigDecimal("100.00"))
                .exitPrice(new BigDecimal("110.00"))
                .entryTime(LocalTime.of(9, 0))
                .exitTime(LocalTime.of(15, 0))
                .asset(defaultAsset())
                .tradeSnapshots(new HashSet<>())
                .build();
    }
}
