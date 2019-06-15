package be.bitbox.traindelay.tracker.persistance.messaging.statistic;

import be.bitbox.traindelay.tracker.core.statistic.MissingDailyStatisticEvent;

import java.time.LocalDate;
import java.util.Objects;

public class MissingDailyStatisticMessage {
    private String date;

    public MissingDailyStatisticMessage() { }

    MissingDailyStatisticMessage(MissingDailyStatisticEvent missingDailyStatisticEvent) {
        this.date = missingDailyStatisticEvent.getDate().toString();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    MissingDailyStatisticEvent asMissingDailyStaticEvent() {
        return new MissingDailyStatisticEvent(LocalDate.parse(date));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissingDailyStatisticMessage that = (MissingDailyStatisticMessage) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return "MissingDailyStatisticMessage{" +
                "date=" + date +
                '}';
    }
}
