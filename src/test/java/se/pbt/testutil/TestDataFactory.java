package se.pbt.testutil;

import se.pbt.model.HoldingSnapshot;
import se.pbt.model.JournalEntry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Utility class for creating domain test objects with predefined default values.
 * <p>
 * Useful in unit tests to generate {@link JournalEntry} and {@link HoldingSnapshot} instances
 * without duplicating initialization logic.
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
        return JournalEntry.builder()
                .date(LocalDate.of(2025, 4, 13))
                .comment("Default test journal entry")
                .build();
    }


    /**
     * Returns a default {@link HoldingSnapshot} representing a completed trade.
     * <ul>
     *   <li>Asset name: "Default Asset"</li>
     *   <li>Asset type: "STOCK"</li>
     *   <li>Buy time: 2025-04-13 09:00</li>
     *   <li>Sell time: 2025-04-13 15:00</li>
     *   <li>Start value: 100</li>
     *   <li>End value: 110</li>
     *   <li>Quantity: 1</li>
     *   <li>Buy fee: 0</li>
     *   <li>Sell fee: 0</li>
     * </ul>
     */
    public static HoldingSnapshot defaultSnapshot() {
        HoldingSnapshot hs = new HoldingSnapshot();
        hs.setAssetName("Default Asset");
        hs.setAssetType("STOCK");
        hs.setBuyTime(LocalTime.of(9, 0));
        hs.setSellTime(LocalTime.of(15, 0));
        hs.setStartValue(new BigDecimal("100"));
        hs.setEndValue(new BigDecimal("110"));
        hs.setQuantity(1);
        hs.setBuyFee(0);
        hs.setSellFee(0);
        return hs;
    }
}
