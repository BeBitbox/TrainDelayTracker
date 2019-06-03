package be.bitbox.traindelay.tracker.core.statistic;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class DailyStatistic implements Statistic {
    private LocalDate day;
    private int departures;
    private int delays;
    private int averageDelay;
    private int cancellations;
    private int platformChanges;

    private DailyStatistic() {}

    DailyStatistic(List<StationStatistic> stationStatistics) {
        AtomicInteger departures = new AtomicInteger();
        AtomicInteger delays = new AtomicInteger();
        AtomicInteger platformChanges = new AtomicInteger();
        AtomicInteger cancellations = new AtomicInteger();
        AtomicInteger totalDelay = new AtomicInteger();

        stationStatistics.forEach(stationStatistic -> {
            departures.getAndAdd(stationStatistic.getDepartures());
            delays.getAndAdd(stationStatistic.getDelays());
            platformChanges.getAndAdd(stationStatistic.getPlatformChanges());
            cancellations.getAndAdd(stationStatistic.getCancellations());
            totalDelay.getAndAdd(stationStatistic.getAverageDelay() * stationStatistic.getDepartures());
        });

        this.day = stationStatistics.isEmpty() ? null : stationStatistics.get(0).getDay();
        this.departures = departures.get();
        this.delays = delays.get();
        this.averageDelay = totalDelay.get() / this.departures;
        this.cancellations = cancellations.get();
        this.platformChanges = platformChanges.get();
    }

    public static final class DayStatisticBuilder {
        private LocalDate day;
        private int departures;
        private int delays;
        private int averageDelay;
        private int cancellations;
        private int platformChanges;

        private DayStatisticBuilder() {
        }

        public static DayStatisticBuilder aDayStatistic() {
            return new DayStatisticBuilder();
        }

        public DayStatisticBuilder withDay(LocalDate day) {
            this.day = day;
            return this;
        }

        public DayStatisticBuilder withDepartures(int departures) {
            this.departures = departures;
            return this;
        }

        public DayStatisticBuilder withDelays(int delays) {
            this.delays = delays;
            return this;
        }

        public DayStatisticBuilder withAverageDelay(int averageDelay) {
            this.averageDelay = averageDelay;
            return this;
        }

        public DayStatisticBuilder withCancellations(int cancellations) {
            this.cancellations = cancellations;
            return this;
        }

        public DayStatisticBuilder withPlatformChanges(int platformChanges) {
            this.platformChanges = platformChanges;
            return this;
        }

        public DailyStatistic build() {
            DailyStatistic dailyStatistic = new DailyStatistic();
            dailyStatistic.averageDelay = this.averageDelay;
            dailyStatistic.cancellations = this.cancellations;
            dailyStatistic.day = this.day;
            dailyStatistic.departures = this.departures;
            dailyStatistic.platformChanges = this.platformChanges;
            dailyStatistic.delays = this.delays;
            return dailyStatistic;
        }
    }

    public LocalDate getDay() {
        return day;
    }

    @Override
    public int getDepartures() {
        return departures;
    }

    @Override
    public int getDelays() {
        return delays;
    }

    @Override
    public int getAverageDelay() {
        return averageDelay;
    }

    @Override
    public int getCancellations() {
        return cancellations;
    }

    @Override
    public int getPlatformChanges() {
        return platformChanges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyStatistic that = (DailyStatistic) o;
        return departures == that.departures &&
                delays == that.delays &&
                averageDelay == that.averageDelay &&
                cancellations == that.cancellations &&
                platformChanges == that.platformChanges &&
                Objects.equals(day, that.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, departures, delays, averageDelay, cancellations, platformChanges);
    }

    @Override
    public String toString() {
        return "DailyStatistic{" +
                "day=" + day +
                ", departures=" + departures +
                ", delays=" + delays +
                ", averageDelay=" + averageDelay +
                ", cancellations=" + cancellations +
                ", platformChanges=" + platformChanges +
                '}';
    }
}
