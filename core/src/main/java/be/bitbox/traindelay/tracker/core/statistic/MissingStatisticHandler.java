package be.bitbox.traindelay.tracker.core.statistic;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MissingStatisticHandler {

    private final DailyStatisticDao dailyStatisticDao;
    private final StationStatisticDao stationStatisticDao;
    private final StationRetriever stationRetriever;
    private final EventBus eventBus;

    @Autowired
    public MissingStatisticHandler(DailyStatisticDao dailyStatisticDao,
                                   StationStatisticDao stationStatisticDao,
                                   StationRetriever stationRetriever,
                                   EventBus eventBus) {
        this.dailyStatisticDao = dailyStatisticDao;
        this.stationStatisticDao = stationStatisticDao;
        this.stationRetriever = stationRetriever;
        this.eventBus = eventBus;
    }

    public void onCommandFor(MissingDailyStatisticEvent missingDailyStatisticEvent) {
        var stations = stationRetriever.getStationsFor(Country.BE);
        var stationMissing = false;
        var collectedStationStatistic = new ArrayList<StationStatistic>();

        for (Station station : stations) {
            var stationStatistic = stationStatisticDao.getStationStatistic(station.stationId(), missingDailyStatisticEvent.getDate());
            if (stationStatistic == null) {
                stationMissing = true;
                eventBus.post(new MissingStationStatisticEvent(station.stationId(), missingDailyStatisticEvent.getDate()));
            } else {
                collectedStationStatistic.add(stationStatistic);
            }
        }

        if (!stationMissing) {
            var dailyStatistic = new DailyStatistic(collectedStationStatistic);
            //dailyStatistic.
        }
    }
}
