package se.pbt.service;

import se.pbt.model.HoldingSnapshot;
import se.pbt.model.JournalEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Provides analysis utilities for interpreting journal entries and associated holdings.
 * This service separates business logic from domain models and supports various calculations.
 *
 * @see JournalEntry
 * @see HoldingSnapshot
 */
public class JournalAnalysisService {

    /**
     * Calculates the total change in currency for all snapshots in a journal entry.
     */
    public BigDecimal calculateTotalChangeKr(JournalEntry entry) {
        return entry.getSnapshots().stream()
                .map(this::calculateChangeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the average percentage change across all snapshots in a journal entry.
     * Snapshots missing endValue or startValue are ignored.
     */
    public BigDecimal calculateAverageChangePercentage(JournalEntry entry) {
        List<HoldingSnapshot> valid = entry.getSnapshots().stream()
                .filter(s -> s.getStartValue() != null && s.getEndValue() != null)
                .toList();

        if (valid.isEmpty()) return BigDecimal.ZERO;

        return valid.stream()
                .map(this::calculateChangePercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(valid.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the number of snapshots that were closed (sold) in this journal entry.
     */
    public int countClosedSnapshots(JournalEntry entry) {
        return (int) entry.getSnapshots().stream()
                .filter(s -> s.getSellTime() != null)
                .count();
    }

    /**
     * Returns the number of snapshots that remain open (not sold) in this journal entry.
     */
    public int countOpenSnapshots(JournalEntry entry) {
        return (int) entry.getSnapshots().stream()
                .filter(s -> s.getSellTime() == null)
                .count();
    }

    /**
     * Returns the number of morning buys (before 11:00) in this journal entry.
     */
    public int getMorningBuyCount(JournalEntry entry) {
        return (int) entry.getSnapshots().stream()
                .filter(s -> s.getBuyTime() != null && s.getBuyTime().isBefore(LocalTime.of(11, 0)))
                .count();
    }


    /**
     * Returns the number of evening sells (at or after 15:00) in this journal entry.
     */
    public int getEveningSellCount(JournalEntry entry) {
        return (int) entry.getSnapshots().stream()
                .filter(s -> s.getSellTime() != null && !s.getSellTime().isBefore(LocalTime.of(15, 0)))
                .count();
    }


    /**
     * Returns true if any snapshot was held over a weekend, based on buy and sell dates.
     * Assumes snapshots in this context can still reflect multi-day trades historically.
     */
    public boolean containsHeldOverWeekendTrades(JournalEntry entry) {
        return entry.getSnapshots().stream()
                .anyMatch(this::crossesWeekend);
    }

    /**
     * Returns true if the trade started on a weekday and was sold after at least one weekend passed.
     */
    public boolean crossesWeekend(HoldingSnapshot snapshot) {
        if (snapshot.getBuyTime() == null || snapshot.getSellTime() == null) return false;

        LocalDate buyDate = LocalDate.from(snapshot.getBuyTime());
        LocalDate sellDate = LocalDate.from(snapshot.getSellTime());

        DayOfWeek buyDay = buyDate.getDayOfWeek();
        if (buyDay == DayOfWeek.SATURDAY || buyDay == DayOfWeek.SUNDAY) return false;

        LocalDate checkDate = buyDate.plusDays(1);
        while (!checkDate.isAfter(sellDate)) {
            DayOfWeek day = checkDate.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                return true;
            }
            checkDate = checkDate.plusDays(1);
        }

        return false;
    }

    /**
     * Calculates the currency change of a holding after subtracting fees.
     */
    public BigDecimal calculateChangeAmount(HoldingSnapshot snapshot) {
        if (snapshot.getEndValue() == null || snapshot.getStartValue() == null) return BigDecimal.ZERO;

        return snapshot.getEndValue()
                .subtract(snapshot.getStartValue())
                .multiply(BigDecimal.valueOf(snapshot.getQuantity()))
                .subtract(BigDecimal.valueOf(snapshot.getBuyFee() + snapshot.getSellFee()));
    }

    /**
     * Calculates the percentage change of a holding based on buy and sell values.
     */
    public BigDecimal calculateChangePercentage(HoldingSnapshot snapshot) {
        if (snapshot.getStartValue() == null || snapshot.getStartValue().compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;

        return snapshot.getEndValue()
                .subtract(snapshot.getStartValue())
                .divide(snapshot.getStartValue(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
