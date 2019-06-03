package be.bitbox.traindelay.tracker.core.statistic;

import be.bitbox.traindelay.tracker.core.station.StationId;

import java.time.LocalDate;
import java.util.Objects;

public class MissingStationStatisticEvent {
    private final StationId stationId;
    private final LocalDate localDate;

    MissingStationStatisticEvent(StationId stationId, LocalDate localDate) {
        this.stationId = stationId;
        this.localDate = localDate;
    }

    public StationId getStationId() {
        return stationId;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    @Override
    public String toString() {
        return "MissingStationStatisticEvent{" +
                "stationId=" + stationId +
                ", localDate=" + localDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissingStationStatisticEvent that = (MissingStationStatisticEvent) o;
        return Objects.equals(stationId, that.stationId) &&
                Objects.equals(localDate, that.localDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, localDate);
    }
}
