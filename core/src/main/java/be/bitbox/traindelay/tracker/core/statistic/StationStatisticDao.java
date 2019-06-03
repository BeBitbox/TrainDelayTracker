package be.bitbox.traindelay.tracker.core.statistic;

import be.bitbox.traindelay.tracker.core.station.StationId;

import java.time.LocalDate;
import java.util.List;

public interface StationStatisticDao {

    StationStatistic getStationStatistic(StationId stationId, LocalDate date);

    void save(StationStatistic stationStatistic);

    List<StationStatistic> getStationStatistic(StationId stationId, LocalDate from, LocalDate toIncluded);
}
