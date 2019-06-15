package be.bitbox.traindelay.tracker.core.statistic;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class YearlyStatistic implements Statistic {
    private int departures;
    private int delays;
    private int averageDelay;
    private int cancellations;
    private int platformChanges;

    YearlyStatistic(List<DailyStatistic> dailyStatistics) {
        AtomicInteger departures = new AtomicInteger();
        AtomicInteger delays = new AtomicInteger();
        AtomicInteger platformChanges = new AtomicInteger();
        AtomicInteger cancellations = new AtomicInteger();
        AtomicInteger totalDelay = new AtomicInteger();

        dailyStatistics.forEach(dailyStatistic -> {
            departures.getAndAdd(dailyStatistic.getDepartures());
            delays.getAndAdd(dailyStatistic.getDelays());
            platformChanges.getAndAdd(dailyStatistic.getPlatformChanges());
            cancellations.getAndAdd(dailyStatistic.getCancellations());
            totalDelay.getAndAdd(dailyStatistic.getAverageDelay() * dailyStatistic.getDepartures());
        });

        this.departures = departures.get();
        this.delays = delays.get();
        this.averageDelay = this.departures > 0 ? totalDelay.get() / this.departures : 0 ;
        this.cancellations = cancellations.get();
        this.platformChanges = platformChanges.get();
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
        YearlyStatistic that = (YearlyStatistic) o;
        return departures == that.departures &&
                delays == that.delays &&
                averageDelay == that.averageDelay &&
                cancellations == that.cancellations &&
                platformChanges == that.platformChanges;
    }

    @Override
    public int hashCode() {
        return Objects.hash(departures, delays, averageDelay, cancellations, platformChanges);
    }

    @Override
    public String toString() {
        return "YearlyStatistic{" +
                "departures=" + departures +
                ", delays=" + delays +
                ", averageDelay=" + averageDelay +
                ", cancellations=" + cancellations +
                ", platformChanges=" + platformChanges +
                '}';
    }
}
