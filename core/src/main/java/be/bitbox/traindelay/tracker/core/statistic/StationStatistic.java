package be.bitbox.traindelay.tracker.core.statistic;

import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class StationStatistic implements Statistic {
    private StationId stationId;
    private LocalDate day;
    private int departures;
    private int delays;
    private int averageDelay;
    private int cancellations;
    private int platformChanges;

    public StationStatistic(List<TrainDepartureEvent> trainDepartureEvents, MissingStationStatisticEvent missingStationStatisticEvent) {
        departures = trainDepartureEvents.size();
        stationId = missingStationStatisticEvent.getStationId();
        day = missingStationStatisticEvent.getLocalDate();

        for (TrainDepartureEvent trainDepartureEvent : trainDepartureEvents) {
            averageDelay += trainDepartureEvent.getDelay();
            if (trainDepartureEvent.getDelay() > 0) {
                delays++;
            }

            if (trainDepartureEvent.isCanceled()) {
                cancellations++;
            }
            if (trainDepartureEvent.isPlatformChange()) {
                platformChanges++;
            }
        }
        if (departures > 0) {
            averageDelay = averageDelay / departures;
        }
    }

    private StationStatistic() {  }

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
