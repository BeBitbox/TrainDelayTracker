package be.bitbox.traindelay.tracker.core.stationstatistic;

import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureRepository;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static be.bitbox.traindelay.tracker.core.stationstatistic.StationStatistic.StationStatisticBuilder.aStationStatistic;

@Service
public class StationStatisticsEventHandler {
    private final TrainDepartureRepository trainDepartureRepository;
    private final StationStatisticDao stationStatisticDao;


    @Autowired
    public StationStatisticsEventHandler(EventBus eventBus, TrainDepartureRepository trainDepartureRepository, StationStatisticDao stationStatisticDao) {
        this.trainDepartureRepository = trainDepartureRepository;
        this.stationStatisticDao = stationStatisticDao;
        eventBus.register(this);
    }

    @Subscribe
    public void subscribe(MissingStationStatisticEvent event) {
        var trainDepartureEvents = trainDepartureRepository.listTrainDepartureFor(event.getStationId(), event.getLocalDate());

        int averageDelay = 0;
        int delays = 0;
        int cancellations = 0;
        int platformChanges = 0;
        int departures = trainDepartureEvents.size();

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
            averageDelay = averageDelay/ departures;
        }

        stationStatisticDao.save(aStationStatistic()
                .withStationId(event.getStationId())
                .withDay(event.getLocalDate())
                .withDepartures(departures)
                .withDelays(delays)
                .withAverageDelay(averageDelay)
                .withCancellations(cancellations)
                .withPlatformChanges(platformChanges)
                .build());
    }

}
