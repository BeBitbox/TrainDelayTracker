package be.bitbox.traindelay.tracker.core.stationstatistic;

import be.bitbox.traindelay.tracker.core.station.StationId;

import java.time.LocalDate;
import java.util.Objects;

public class StationStatistic {
    private StationId stationId;
    private LocalDate day;
    private int departures;
    private int delays;
    private int averageDelay;
    private int cancellations;
    private int platformChanges;

    public static final class StationStatisticBuilder {
        private StationId stationId;
        private LocalDate day;
        private int departures;
        private int delays;
        private int averageDelay;
        private int cancellations;
        private int platformChanges;

        private StationStatisticBuilder() {
        }

        public static StationStatisticBuilder aStationStatistic() {
            return new StationStatisticBuilder();
        }

        public StationStatisticBuilder withStationId(StationId stationId) {
            this.stationId = stationId;
            return this;
        }

        public StationStatisticBuilder withDay(LocalDate day) {
            this.day = day;
            return this;
        }

        public StationStatisticBuilder withDepartures(int departures) {
            this.departures = departures;
            return this;
        }

        public StationStatisticBuilder withDelays(int delays) {
            this.delays = delays;
            return this;
        }

        public StationStatisticBuilder withAverageDelay(int averageDelay) {
            this.averageDelay = averageDelay;
            return this;
        }

        public StationStatisticBuilder withCancellations(int cancellations) {
            this.cancellations = cancellations;
            return this;
        }

        public StationStatisticBuilder withPlatformChanges(int platformChanges) {
            this.platformChanges = platformChanges;
            return this;
        }

        public StationStatistic build() {
            StationStatistic stationStatistic = new StationStatistic();
            stationStatistic.cancellations = this.cancellations;
            stationStatistic.stationId = this.stationId;
            stationStatistic.averageDelay = this.averageDelay;
            stationStatistic.platformChanges = this.platformChanges;
            stationStatistic.day = this.day;
            stationStatistic.departures = this.departures;
            stationStatistic.delays = this.delays;
            return stationStatistic;
        }
    }

    public StationId getStationId() {
        return stationId;
    }

    public LocalDate getDay() {
        return day;
    }

    public int getDepartures() {
        return departures;
    }

    public int getDelays() {
        return delays;
    }

    public int getAverageDelay() {
        return averageDelay;
    }

    public int getCancellations() {
        return cancellations;
    }

    public int getPlatformChanges() {
        return platformChanges;
    }

    @Override
    public String toString() {
        return "StationStatistic{" +
                "stationId=" + stationId +
                ", day=" + day +
                ", departures=" + departures +
                ", delays=" + delays +
                ", averageDelay=" + averageDelay +
                ", cancellations=" + cancellations +
                ", platformChanges=" + platformChanges +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationStatistic that = (StationStatistic) o;
        return departures == that.departures &&
                delays == that.delays &&
                averageDelay == that.averageDelay &&
                cancellations == that.cancellations &&
                platformChanges == that.platformChanges &&
                Objects.equals(stationId, that.stationId) &&
                Objects.equals(day, that.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, day, departures, delays, averageDelay, cancellations, platformChanges);
    }
}
