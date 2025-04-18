package se.pbt.service;

import se.pbt.model.JournalEntry;
import se.pbt.model.Trade;
import se.pbt.model.TradeSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Provides analysis utilities for interpreting journal entries and associated trades.
 * This service separates business logic from domain models and supports various calculations.
 *
 * @see JournalEntry
 * @see Trade
 * @see TradeSnapshot
 */
public class JournalAnalysisService {

    /**
     * Calculates the total currency gain/loss (in SEK) for all snapshots in a journal entry.
     * Includes both sold and held trades, factoring in quantity and fees.
     */
    public BigDecimal calculateTotalChangeKr(JournalEntry entry) {
        return entry.getTrades().stream()
                .flatMap(trade -> trade.getSnapshots().stream())
                .map(this::calculateChangeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the average percentage development across all snapshots in a journal entry.
     * Snapshots missing start or end value are ignored.
     */
    public BigDecimal calculateAverageChangePercentage(JournalEntry entry) {
        List<TradeSnapshot> valid = entry.getTrades().stream()
                .flatMap(trade -> trade.getSnapshots().stream())
                .filter(s -> s.getStartValue() != null && s.getEndValue() != null)
                .toList();

        if (valid.isEmpty()) return BigDecimal.ZERO;

        return valid.stream()
                .map(this::calculateChangePercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(valid.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Counts how many trades in the journal entry are closed (i.e., has a sell time).
     */
    public int countClosedSnapshots(JournalEntry entry) {
        return (int) entry.getTrades().stream()
                .flatMap(t -> t.getSnapshots().stream())
                .filter(s -> s.getSellTime() != null)
                .count();
    }

    /**
     * Counts how many trades are still open (i.e., have no sell time).
     */
    public int countOpenSnapshots(JournalEntry entry) {
        return (int) entry.getTrades().stream()
                .flatMap(t -> t.getSnapshots().stream())
                .filter(s -> s.getSellTime() == null)
                .count();
    }

    /**
     * Counts how many snapshots in the journal entry were bought before 11:00.
     */
    public int getMorningBuyCount(JournalEntry entry) {
        return (int) entry.getTrades().stream()
                .flatMap(t -> t.getSnapshots().stream())
                .filter(s -> s.getBuyTime() != null && s.getBuyTime().isBefore(LocalTime.of(11, 0)))
                .count();
    }

    /**
     * Counts how many snapshots were sold at or after 15:00 in the journal entry.
     */
    public int getEveningSellCount(JournalEntry entry) {
        return (int) entry.getTrades().stream()
                .flatMap(t -> t.getSnapshots().stream())
                .filter(s -> s.getSellTime() != null && !s.getSellTime().isBefore(LocalTime.of(15, 0)))
                .count();
    }

    /**
     * Checks if any trade in the journal entry spans over a weekend period.
     */
    public boolean containsHeldOverWeekendTrades(JournalEntry entry) {
        return entry.getTrades().stream()
                .flatMap(t -> t.getSnapshots().stream())
                .anyMatch(this::crossesWeekend);
    }

    /**
     * Checks if a given trade spans across a weekend, using its earliest and latest snapshot.
     */
    public boolean crossesWeekend(TradeSnapshot snapshot) {
        if (snapshot.getBuyTime() == null || snapshot.getSellTime() == null) return false;

        LocalDate entryDate = snapshot.getTrade().getJournalEntry().getDate();
        LocalDateTime buyDateTime = entryDate.atTime(snapshot.getBuyTime());
        LocalDateTime sellDateTime = entryDate.atTime(snapshot.getSellTime());

        if (sellDateTime.isBefore(buyDateTime)) {
            sellDateTime = sellDateTime.plusDays(1);
        }

        LocalDate current = buyDateTime.toLocalDate().plusDays(1);
        LocalDate sellDate = sellDateTime.toLocalDate();

        while (!current.isAfter(sellDate)) {
            DayOfWeek dow = current.getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
                return true;
            }
            current = current.plusDays(1);
        }

        return false;
    }



    /**
     * Calculates the absolute change in SEK of a trade snapshot, after subtracting fees.
     */
    public BigDecimal calculateChangeAmount(TradeSnapshot snapshot) {
        if (snapshot.getEndValue() == null || snapshot.getStartValue() == null) return BigDecimal.ZERO;

        return snapshot.getEndValue()
                .subtract(snapshot.getStartValue())
                .multiply(BigDecimal.valueOf(snapshot.getQuantity()))
                .subtract(BigDecimal.valueOf(snapshot.getBuyFee() + snapshot.getSellFee()));
    }

    /**
     * Calculates the percentage change in value for a given trade snapshot.
     */
    public BigDecimal calculateChangePercentage(TradeSnapshot snapshot) {
        if (snapshot.getStartValue() == null || snapshot.getStartValue().compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;

        return snapshot.getEndValue()
                .subtract(snapshot.getStartValue())
                .divide(snapshot.getStartValue(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
