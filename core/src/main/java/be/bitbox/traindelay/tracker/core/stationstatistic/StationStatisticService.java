package be.bitbox.traindelay.tracker.core.stationstatistic;

import be.bitbox.traindelay.tracker.core.station.StationId;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class StationStatisticService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StationStatisticService.class);
    private final StationStatisticDao stationStatisticDao;
    private final EventBus eventBus;

    @Autowired
    public StationStatisticService(StationStatisticDao stationStatisticDao, EventBus eventBus) {
        this.stationStatisticDao = stationStatisticDao;
        this.eventBus = eventBus;
    }

    public StationStatistic getStationStatisticFor(StationId stationId, LocalDate localDate) {
        var stationStatistic = safelyGetStationStaticFor(stationId, localDate);
        if (stationStatistic == null) {
            eventBus.post(new MissingStationStatisticEvent(stationId, localDate));
        }
        return stationStatistic;
    }

    private StationStatistic safelyGetStationStaticFor(StationId stationId, LocalDate localDate) {
        StationStatistic stationStatistic = null;
        try {
            stationStatistic = stationStatisticDao.getStationStatistic(stationId, localDate);
        }
        catch (Exception ex) {
            LOGGER.error("Error retrieving station statistic for " + stationId + " on " + localDate, ex);
        }
        return stationStatistic;
    }
}
