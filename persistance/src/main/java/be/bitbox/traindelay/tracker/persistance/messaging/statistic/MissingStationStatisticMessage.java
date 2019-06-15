package be.bitbox.traindelay.tracker.persistance.messaging.statistic;

import be.bitbox.traindelay.tracker.core.statistic.MissingStationStatisticEvent;

import java.time.LocalDate;
import java.util.Objects;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;

public class MissingStationStatisticMessage {
    private String stationId;
    private String localDate;

    public MissingStationStatisticMessage() {
    }

    MissingStationStatisticMessage(MissingStationStatisticEvent missingStationStatisticEvent) {
        this.stationId = missingStationStatisticEvent.getStationId().getId();
        this.localDate = missingStationStatisticEvent.getLocalDate().toString();
    }

    MissingStationStatisticEvent asMissingStationStatisticEvent() {
        return new MissingStationStatisticEvent(aStationId(stationId), LocalDate.parse(localDate));
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getLocalDate() {
        return localDate;
    }

    public void setLocalDate(String localDate) {
        this.localDate = localDate;
    }

    @Override
    public String toString() {
        return "MissingStationStatisticMessage{" +
                "stationId=" + stationId +
                ", localDate=" + localDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissingStationStatisticMessage that = (MissingStationStatisticMessage) o;
        return Objects.equals(stationId, that.stationId) &&
                Objects.equals(localDate, that.localDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, localDate);
    }
}
