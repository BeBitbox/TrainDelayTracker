package be.bitbox.traindelay.tracker.core.stationstatistic;

import be.bitbox.traindelay.tracker.core.station.StationId;

import java.time.LocalDate;
import java.util.List;

public interface StationStatisticRepository {
    List<StationStatistic> getStationStatisticFor(StationId stationId, LocalDate localDate);
}
