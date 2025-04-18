package se.pbt.testutil;

import se.pbt.model.JournalEntry;
import se.pbt.model.Trade;
import se.pbt.model.TradeSnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Utility class for creating domain test objects with predefined default values.
 * <p>
 * Useful in unit tests to generate {@link JournalEntry}, {@link Trade} and {@link TradeSnapshot}
 * instances without duplicating initialization logic.
 * <p>
 * Default objects are fully populated and can be modified in test classes to suit different scenarios.
 */
public class TestDataFactory {

    /**
     * Returns a default {@link JournalEntry} with a predefined date and comment.
     * <ul>
     *   <li>Date: 2025-04-13</li>
     *   <li>Comment: "Default test journal entry"</li>
     * </ul>
     */
    public static JournalEntry defaultJournalEntry() {
        JournalEntry defaultEntry = JournalEntry.builder()
                .date(LocalDate.of(2025, 4, 13))
                .notes("Default test journal entry")
                .build();

        defaultEntry.addTrade(defaultTrade());

        return defaultEntry;
    }

    /**
     * Returns an empty {@link JournalEntry} with a predefined date and comment.
     * <ul>
     *   <li>Date: 2025-04-13</li>
     *   <li>Comment: "Default test journal entry"</li>
     * </ul>
     */
    public static JournalEntry emptytJournalEntry() {
        return JournalEntry.builder()
                .date(LocalDate.of(2025, 4, 13))
                .notes("Empty test journal entry")
                .build();
    }

    /**
     * Returns a default {@link Trade} with one associated {@link TradeSnapshot}.
     * <ul>
     *   <li>Label: "Default Trade"</li>
     *   <li>Contains a single default snapshot</li>
     * </ul>
     */
    public static Trade defaultTrade() {
        Trade trade = Trade.builder()
                .label("Default Trade")
                .build();

        trade.addSnapshot(defaultSnapshot());

        return trade;
    }

    /**
     * Returns a default {@link TradeSnapshot} representing a completed trade.
     * <ul>
     *   <li>Asset name: "Default Asset"</li>
     *   <li>Asset type: "STOCK"</li>
     *   <li>Buy time: 09:00</li>
     *   <li>Sell time: 15:00</li>
     *   <li>Start value: 100</li>
     *   <li>End value: 110</li>
     *   <li>Quantity: 1</li>
     *   <li>Buy fee: 0</li>
     *   <li>Sell fee: 0</li>
     * </ul>
     */
    public static TradeSnapshot defaultSnapshot() {
        return TradeSnapshot.builder()
                .assetName("Default Asset")
                .assetType("STOCK")
                .buyTime(LocalTime.of(9, 0))
                .sellTime(LocalTime.of(15, 0))
                .startValue(new BigDecimal("100"))
                .endValue(new BigDecimal("110"))
                .quantity(1)
                .buyFee(0)
                .sellFee(0)
                .notes("Default snapshot notes")
                .build();
    }
}
