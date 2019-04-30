package be.bitbox.traindelay.tracker.core.stationstatistic;

import be.bitbox.traindelay.tracker.core.station.StationId;

import java.time.LocalDate;

public interface StationStatisticDao {

    StationStatistic getStationStatistic(StationId stationId, LocalDate date);

    void save(StationStatistic stationStatistic);
}
