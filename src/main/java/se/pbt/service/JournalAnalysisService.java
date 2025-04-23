package se.pbt.service;

import se.pbt.model.ExecutedSale;
import se.pbt.model.JournalEntry;
import se.pbt.model.TradeSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

/**
 * Provides analysis utilities for interpreting journal entries and associated trade snapshots.
 * This service separates business logic from domain models and supports various calculations.
 *
 * @see JournalEntry
 * @see TradeSnapshot
 * @see ExecutedSale
 */
public class JournalAnalysisService {

    /**
     * Calculates the total gain/loss (SEK) for all snapshots in a journal entry.
     * Based on price change × quantity for held assets.
     */
    public BigDecimal calculateTotalChangeKr(JournalEntry entry) {
        return entry.getTradeSnapshots().stream()
                .map(this::calculateChangeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the average percentage change across all snapshots in a journal entry.
     */
    public BigDecimal calculateAverageChangePercentage(JournalEntry entry) {
        List<TradeSnapshot> valid = entry.getTradeSnapshots().stream()
                .filter(s -> s.getOpenPrice() != null && s.getClosePrice() != null)
                .toList();

        if (valid.isEmpty()) return BigDecimal.ZERO;

        return valid.stream()
                .map(this::calculateChangePercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(valid.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Counts how many snapshots contain executed sales.
     */
    public int countClosedSnapshots(JournalEntry entry) {
        return (int) entry.getTradeSnapshots().stream()
                .filter(s -> !s.getExecutedSales().isEmpty())
                .count();
    }

    /**
     * Counts how many snapshots are still open (no sales recorded).
     */
    public int countOpenSnapshots(JournalEntry entry) {
        return (int) entry.getTradeSnapshots().stream()
                .filter(s -> s.getExecutedSales().isEmpty())
                .count();
    }

    /**
     * Counts how many trades were bought before 11:00 (based on Trade's entry time).
     */
    public int getMorningBuyCount(JournalEntry entry) {
        return (int) entry.getTradeSnapshots().stream()
                .filter(s -> s.getTrade().getEntryTime() != null &&
                        s.getTrade().getEntryTime().isBefore(LocalTime.of(11, 0)))
                .count();
    }

    /**
     * Counts how many trades were sold (via ExecutedSale) at or after 15:00.
     */
    public int getEveningSellCount(JournalEntry entry) {
        return (int) entry.getTradeSnapshots().stream()
                .flatMap(s -> s.getExecutedSales().stream())
                .filter(sale -> sale.getSellTime() != null && !sale.getSellTime().isBefore(LocalTime.of(15, 0)))
                .count();
    }

    /**
     * Checks if any snapshot’s trade spans over a weekend.
     */
    public boolean containsHeldOverWeekendTrades(JournalEntry entry) {
        return entry.getTradeSnapshots().stream()
                .map(TradeSnapshot::getTrade)
                .distinct()
                .anyMatch(this::crossesWeekend);
    }

    /**
     * Checks if a trade spans over a weekend using earliest/latest snapshot dates.
     */
    public boolean crossesWeekend(se.pbt.model.Trade trade) {
        List<TradeSnapshot> snapshots = trade.getTradeSnapshots().stream().sorted(
                Comparator.comparing(a -> a.getJournalEntry().getDate())
        ).toList();

        if (snapshots.size() < 2) return false;

        LocalDate start = snapshots.get(0).getJournalEntry().getDate();
        LocalDate end = snapshots.get(snapshots.size() - 1).getJournalEntry().getDate();

        LocalDate current = start.plusDays(1);
        while (!current.isAfter(end)) {
            DayOfWeek dow = current.getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) return true;
            current = current.plusDays(1);
        }

        return false;
    }

    /**
     * Calculates the absolute change in SEK of a trade snapshot (excluding fees).
     */
    public BigDecimal calculateChangeAmount(TradeSnapshot snapshot) {
        if (snapshot.getOpenPrice() == null || snapshot.getClosePrice() == null) return BigDecimal.ZERO;

        return snapshot.getClosePrice()
                .subtract(snapshot.getOpenPrice())
                .multiply(BigDecimal.valueOf(snapshot.getRemainingQuantity()));
    }

    /**
     * Calculates percentage change for a snapshot.
     */
    public BigDecimal calculateChangePercentage(TradeSnapshot snapshot) {
        if (snapshot.getOpenPrice() == null || snapshot.getOpenPrice().compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;

        return snapshot.getClosePrice()
                .subtract(snapshot.getOpenPrice())
                .divide(snapshot.getOpenPrice(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
