package be.bitbox.traindelay.tracker.core.statistic;

import java.time.LocalDate;
import java.util.Objects;

public class MissingDailyStatisticEvent {
    private final LocalDate date;

    MissingDailyStatisticEvent(LocalDate localDate) {
        this.date = localDate;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissingDailyStatisticEvent that = (MissingDailyStatisticEvent) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return "MissingDailyStatisticEvent{" +
                "date=" + date +
                '}';
    }
}
