package be.bitbox.traindelay.tracker.persistance.local;

import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.statistic.StationStatistic;
import be.bitbox.traindelay.tracker.core.statistic.StationStatisticDao;

import java.time.LocalDate;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.statistic.StationStatistic.StationStatisticBuilder.aStationStatistic;

public class LocalStationStatisticDao implements StationStatisticDao {

    @Override
    public StationStatistic getStationStatistic(StationId stationId, LocalDate date) {
        return aStationStatistic()
                .withStationId(stationId)
                .withAverageDelay(1200)
                .withCancellations(0)
                .withDay(date)
                .withDelays(0)
                .withDepartures(1)
                .withPlatformChanges(1)
                .build();
    }

    @Override
    public void save(StationStatistic stationStatistic) {

    }

    @Override
    public List<StationStatistic> getStationStatistic(StationId stationId, LocalDate from, LocalDate toIncluded) {
        return List.of(
                getStationStatistic(stationId, from),
                getStationStatistic(stationId, toIncluded)
        );
    }
}
