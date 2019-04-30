package be.bitbox.traindelay.tracker.core.stationstatistic;

import be.bitbox.traindelay.tracker.core.station.StationId;

import java.time.LocalDate;

public class MissingStationStatisticEvent {
    private final StationId stationId;
    private final LocalDate localDate;

    public MissingStationStatisticEvent(StationId stationId, LocalDate localDate) {
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
}
