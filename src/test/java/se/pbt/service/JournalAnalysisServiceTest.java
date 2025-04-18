package se.pbt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.pbt.model.JournalEntry;
import se.pbt.model.Trade;
import se.pbt.model.TradeSnapshot;
import se.pbt.testutil.TestDataFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

// TODO: Fix weekend related texts
class JournalAnalysisServiceTest {

    private JournalAnalysisService analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new JournalAnalysisService();
    }

    @Nested
    @DisplayName("calculateTotalChangeKr")
    class CalculateTotalChangeKr {

        @Test
        @DisplayName("returns positive change when endValue is greater than startValue")
        void returnsPositiveChange() {
            JournalEntry entry = withSingleSnapshot(new BigDecimal("120")); // default start = 100
            BigDecimal result = analyzer.calculateTotalChangeKr(entry);
            assertEquals(new BigDecimal("20.0"), result); // (120 - 100) * 1
        }

        @Test
        @DisplayName("returns zero change when endValue equals startValue")
        void returnsZeroChange() {
            JournalEntry entry = withSingleSnapshot(new BigDecimal("100")); // same as start
            BigDecimal result = analyzer.calculateTotalChangeKr(entry);
            assertEquals(new BigDecimal("0.0"), result);
        }

        @Test
        @DisplayName("returns negative change when endValue is less than startValue")
        void returnsNegativeChange() {
            JournalEntry entry = withSingleSnapshot(new BigDecimal("90")); // default start = 100
            BigDecimal result = analyzer.calculateTotalChangeKr(entry);
            assertEquals(new BigDecimal("-10.0"), result); // (90 - 100) * 1
        }
    }


    @Nested
    @DisplayName("getMorningBuyCount")
    class GetMorningBuyCount {

        @Test
        @DisplayName("counts 1 when buy time is before 11:00")
        void countsWhenBuyBeforeTen() {
            LocalTime buyTime = LocalTime.of(10, 30);
            JournalEntry entry = withBuyTime(buyTime);

            int result = analyzer.getMorningBuyCount(entry);
            assertEquals(1, result);
        }

        @Test
        @DisplayName("counts 0 when buy time is 11:00 or later")
        void doesNotCountWhenBuyAtOrAfterTen() {
            LocalTime buyTime = LocalTime.of(11, 0);
            JournalEntry entry = withBuyTime(buyTime);

            int result = analyzer.getMorningBuyCount(entry);
            assertEquals(0, result);
        }

        @Test
        @DisplayName("returns 0 when there are no holdings")
        void returnsZeroWhenNoHoldings() {
            JournalEntry entry = TestDataFactory.emptytJournalEntry();

            int result = analyzer.getMorningBuyCount(entry);
            assertEquals(0, result);
        }
    }


    @Nested
    @DisplayName("getEveningSellCount")
    class GetEveningSellCount {

        @Test
        @DisplayName("counts 1 when sell time is 15:00 or later")
        void countsWhenSellAfterOrAtSixteen() {
            LocalTime sellTime = LocalTime.of(15, 30);
            JournalEntry entry = withSellTime(sellTime);

            int result = analyzer.getEveningSellCount(entry);
            assertEquals(1, result);
        }

        @Test
        @DisplayName("counts 0 when sell time is before 15:00")
        void doesNotCountWhenSellBeforeSixteen() {
            LocalTime sellTime = LocalTime.of(14, 45);
            JournalEntry entry = withSellTime(sellTime);

            int result = analyzer.getEveningSellCount(entry);
            assertEquals(0, result);
        }

        @Test
        @DisplayName("returns 0 when there are no holdings")
        void returnsZeroWhenNoHoldings() {
            JournalEntry entry = TestDataFactory.emptytJournalEntry();

            int result = analyzer.getEveningSellCount(entry);
            assertEquals(0, result);
        }
    }


    @Nested
    @DisplayName("containsWeekendTrades")
    class ContainsWeekendTrades {

        @Test
        @DisplayName("returns true when trade was held over a weekend after buying on a weekday")
        void returnsTrueIfTradeHeldOverWeekend() {
            LocalTime buy = LocalTime.of(14, 0);
            LocalTime sell = LocalTime.of(10, 0);

            JournalEntry entry = withSnapshotPeriod(buy, sell);

            entry.setDate(LocalDate.of(2025, 4, 11));

            assertTrue(analyzer.containsHeldOverWeekendTrades(entry));
        }

        @Test
        @DisplayName("returns false when trade occurs only on weekdays")
        void returnsFalseIfTradeIsWeekdaysOnly() {
            LocalTime buy = LocalTime.of(10, 0); // Monday
            LocalTime sell = LocalTime.of(10, 0); // Tuesday
            JournalEntry entry = withSnapshotPeriod(buy, sell);

            assertFalse(analyzer.containsHeldOverWeekendTrades(entry));
        }

        @Test
        @DisplayName("returns false when there are no trades")
        void returnsFalseWhenNoHoldings() {
            JournalEntry entry = TestDataFactory.emptytJournalEntry();

            assertFalse(analyzer.containsHeldOverWeekendTrades(entry));
        }
    }


    @Nested
    @DisplayName("countClosedSnapshots")
    class CountClosedSnapshots {

        @Test
        @DisplayName("returns 1 when sellDateTime is set")
        void returnsOneWhenSnapshotIsClosed() {
            JournalEntry entry = withSellTimeSet(true);
            int result = analyzer.countClosedSnapshots(entry);
            assertEquals(1, result);
        }

        @Test
        @DisplayName("returns 0 when sellDateTime is missing")
        void returnsZeroWhenSnapshotIsOpen() {
            JournalEntry entry = withSellTimeSet(false);
            int result = analyzer.countClosedSnapshots(entry);
            assertEquals(0, result);
        }

        @Test
        @DisplayName("returns 0 when there are no snapshots")
        void returnsZeroWhenNoHoldings() {
            JournalEntry entry = TestDataFactory.emptytJournalEntry();

            int result = analyzer.countClosedSnapshots(entry);
            assertEquals(0, result);
        }
    }


    @Nested
    @DisplayName("countOpenSnapshots")
    class CountOpenSnapshots {

        @Test
        @DisplayName("returns 1 when sellDateTime is missing")
        void returnsOneWhenSnapshotIsOpen() {
            JournalEntry entry = withSellTimeSet(false);
            int result = analyzer.countOpenSnapshots(entry);
            assertEquals(1, result);
        }

        @Test
        @DisplayName("returns 0 when sellDateTime is set")
        void returnsZeroWhenSnapshotIsClosed() {
            JournalEntry entry = withSellTimeSet(true);
            int result = analyzer.countOpenSnapshots(entry);
            assertEquals(0, result);
        }

        @Test
        @DisplayName("returns 0 when there are no snapshots")
        void returnsZeroWhenNoHoldings() {
            JournalEntry entry = TestDataFactory.emptytJournalEntry();

            int result = analyzer.countOpenSnapshots(entry);
            assertEquals(0, result);
        }
    }


    @Nested
    @DisplayName("calculateAverageChangePct")
    class CalculateAverageChangePct {

        @Test
        @DisplayName("returns 0 when no active holdings exist")
        void returnsZeroWhenNoActiveSnapshots() {
            JournalEntry entry = TestDataFactory.emptytJournalEntry();

            BigDecimal result = analyzer.calculateAverageChangePercentage(entry);
            assertEquals(BigDecimal.ZERO, result);
        }

        @Test
        @DisplayName("returns correct percentage for one active holding")
        void returnsCorrectChangeForSingleSnapshot() {
            JournalEntry entry = withSnapshots(new BigDecimal("110")); // startValue = 100 → +10%
            BigDecimal result = analyzer.calculateAverageChangePercentage(entry);
            assertEquals(new BigDecimal("10.00"), result);
        }

        @Test
        @DisplayName("returns correct average when all snapshots have same percentage")
        void returnsSameChangeAsEachSnapshot() {
            JournalEntry entry = withSnapshots(
                    new BigDecimal("110"),
                    new BigDecimal("110"),
                    new BigDecimal("110")
            );
            BigDecimal result = analyzer.calculateAverageChangePercentage(entry);
            assertEquals(new BigDecimal("10.00"), result);
        }

        @Test
        @DisplayName("returns average of mixed positive and negative changes")
        void returnsAverageFromMixedChanges() {
            JournalEntry entry = withSnapshots(
                    new BigDecimal("110"),  // +10%
                    new BigDecimal("90")    // -10%
            );
            BigDecimal result = analyzer.calculateAverageChangePercentage(entry);
            assertEquals(new BigDecimal("0.00"), result);
        }

        @Test
        @DisplayName("ignores snapshot with null endValue")
        void ignoresSnapshotWithNullEndValue() {
            JournalEntry entry = withSnapshots(
                    new BigDecimal("110"),
                    null
            );
            BigDecimal result = analyzer.calculateAverageChangePercentage(entry);
            assertEquals(new BigDecimal("10.00"), result);
        }
    }


    /**
     * Creates a JournalEntry with a single HoldingSnapshot, where only the end value is overridden.
     */
    private JournalEntry withSingleSnapshot(BigDecimal endValue) {
        Trade trade = TestDataFactory.defaultTrade();
        TradeSnapshot snapshot = trade.getSnapshots().iterator().next();
        snapshot.setEndValue(endValue);

        JournalEntry entry = TestDataFactory.emptytJournalEntry();
        entry.addTrade(trade);

        return entry;
    }

    /**
     * Creates a JournalEntry with a single HoldingSnapshot, where only the buy time is overridden.
     * The JournalEntry date is fixed for test consistency.
     */
    private JournalEntry withBuyTime(LocalTime buyTime) {
        Trade trade = TestDataFactory.defaultTrade();
        TradeSnapshot snapshot = trade.getSnapshots().iterator().next();
        snapshot.setBuyTime(buyTime);

        JournalEntry entry = TestDataFactory.emptytJournalEntry();
        entry.addTrade(trade);

        return entry;
    }

    /**
     * Creates a JournalEntry with a single HoldingSnapshot, where sell time is overridden.
     */
    private JournalEntry withSellTime(LocalTime sellTime) {
        Trade trade = TestDataFactory.defaultTrade();
        TradeSnapshot snapshot = trade.getSnapshots().iterator().next();
        snapshot.setSellTime(sellTime);

        JournalEntry entry = TestDataFactory.emptytJournalEntry();
        entry.addTrade(trade);

        return entry;
    }

    /**
     * Creates a JournalEntry with a single HoldingSnapshot spanning from the given buy time to sell time.
     */
    private JournalEntry withSnapshotPeriod(LocalTime buy, LocalTime sell) {
        Trade trade = TestDataFactory.defaultTrade();
        TradeSnapshot snapshot = trade.getSnapshots().iterator().next();
        snapshot.setBuyTime(buy);
        snapshot.setSellTime(sell);

        JournalEntry entry = TestDataFactory.emptytJournalEntry();
        entry.addTrade(trade);

        return entry;
    }

    /**
     * Creates a JournalEntry with one HoldingSnapshot where sell time is either set or null.
     */
    private JournalEntry withSellTimeSet(boolean includeSellTime) {
        Trade trade = TestDataFactory.defaultTrade();
        TradeSnapshot snapshot = trade.getSnapshots().iterator().next();
        snapshot.setSellTime(includeSellTime ? LocalTime.of(14, 0) : null);

        JournalEntry entry = TestDataFactory.emptytJournalEntry();
        entry.addTrade(trade);

        return entry;
    }

    /**
     * Creates a JournalEntry with a list of HoldingSnapshots based on provided start/end values.
     * Pass null as endValue to simulate an incomplete (open) trade.
     */
    private JournalEntry withSnapshots(BigDecimal... endValues) {
        Trade trade = Trade.builder().label("Multi-snapshot trade").build();

        for (BigDecimal endValue : endValues) {
            TradeSnapshot snapshot = TestDataFactory.defaultSnapshot();
            snapshot.setEndValue(endValue);
            trade.addSnapshot(snapshot);
        }

        JournalEntry entry = TestDataFactory.emptytJournalEntry();
        entry.addTrade(trade);

        return entry;
    }
}