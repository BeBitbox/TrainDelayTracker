package be.bitbox.traindelay.tracker.core.service;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import be.bitbox.traindelay.tracker.core.statistic.*;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureRepository;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MissingStatisticHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(MissingStatisticHandler.class);
    private final DailyStatisticDao dailyStatisticDao;
    private final StationStatisticDao stationStatisticDao;
    private final StationRetriever stationRetriever;
    private final EventBus eventBus;
    private final TrainDepartureRepository trainDepartureRepository;

    @Autowired
    public MissingStatisticHandler(DailyStatisticDao dailyStatisticDao,
                                   StationStatisticDao stationStatisticDao,
                                   StationRetriever stationRetriever,
                                   EventBus eventBus,
                                   TrainDepartureRepository trainDepartureRepository) {
        this.dailyStatisticDao = dailyStatisticDao;
        this.stationStatisticDao = stationStatisticDao;
        this.stationRetriever = stationRetriever;
        this.eventBus = eventBus;
        this.trainDepartureRepository = trainDepartureRepository;
    }

    public void onCommandFor(MissingDailyStatisticEvent missingDailyStatisticEvent) {
        LOGGER.info("Treating missing dailyEvent {} like a princess", missingDailyStatisticEvent);
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
            dailyStatisticDao.save(new DailyStatistic(collectedStationStatistic));
        }
    }

    public void onCommandFor(MissingStationStatisticEvent missingStationStatisticEvent) {
        LOGGER.info("Treating missing StationEvent {} like a princess", missingStationStatisticEvent);

        var departures = trainDepartureRepository.listTrainDepartureFor(missingStationStatisticEvent.getStationId(), missingStationStatisticEvent.getLocalDate());
        stationStatisticDao.save(new StationStatistic(departures, missingStationStatisticEvent));
    }
}
