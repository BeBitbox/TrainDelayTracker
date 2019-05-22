package be.bitbox.traindelay.tracker.core.stationstatistic;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static be.bitbox.traindelay.tracker.core.stationstatistic.StationStatistic.StationStatisticBuilder.aStationStatistic;
import static java.util.Optional.ofNullable;

@Component
public class StationStatisticService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StationStatisticService.class);
    private final StationStatisticDao stationStatisticDao;
    private final EventBus eventBus;
    private final StationRetriever stationRetriever;
    private StationStatistic stationStatisticOfYear;
    private LocalDateTime expiredTime;

    @Autowired
    public StationStatisticService(StationStatisticDao stationStatisticDao, EventBus eventBus, StationRetriever stationRetriever) {
        this.stationStatisticDao = stationStatisticDao;
        this.eventBus = eventBus;
        this.stationRetriever = stationRetriever;
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
        } catch (Exception ex) {
            LOGGER.error("Error retrieving station statistic for " + stationId + " on " + localDate, ex);
        }
        return stationStatistic;
    }

    public StationStatistic getFullStationStaticFromLastYear() {
        if (expiredTime == null || LocalDateTime.now().isAfter(expiredTime)) {
            this.stationStatisticOfYear = createFullStationStaticFromLastYear();
            this.expiredTime = LocalDateTime.now().plusMinutes(5);
        }
        return stationStatisticOfYear;

    }

    private StationStatistic createFullStationStaticFromLastYear() {
        var stations = stationRetriever.getStationsFor(Country.BE);
        var today = LocalDate.now();
        var oneYearAgo = today.minusYears(1).minusDays(1);
        var validateMissingStatistics = new ValidateMissingStatistics(stations, oneYearAgo, today);
        AtomicInteger departures = new AtomicInteger();
        AtomicInteger delays = new AtomicInteger();
        AtomicInteger platformChanges = new AtomicInteger();
        AtomicInteger cancellations = new AtomicInteger();
        AtomicInteger total = new AtomicInteger();
        AtomicInteger averageDelay = new AtomicInteger();
        stations.stream()
                .map(station -> stationStatisticDao.getStationStatistic(station.stationId(), oneYearAgo, today))
                .flatMap(Collection::stream)
                .forEach(stationStatistic ->
                {
                    departures.getAndAdd(stationStatistic.getDepartures());
                    delays.getAndAdd(stationStatistic.getDelays());
                    platformChanges.getAndAdd(stationStatistic.getPlatformChanges());
                    cancellations.getAndAdd(stationStatistic.getCancellations());
                    total.getAndIncrement();
                    averageDelay.getAndAdd(stationStatistic.getAverageDelay());
                    validateMissingStatistics.markOk(stationStatistic.getStationId(), stationStatistic.getDay());
                });

        validateMissingStatistics.notifyMissingEventsIn(eventBus);

        return aStationStatistic()
                .withDepartures(departures.get())
                .withDelays(delays.get())
                .withPlatformChanges(platformChanges.get())
                .withCancellations(cancellations.get())
                .withAverageDelay(averageDelay.get() / total.get())
                .build();
    }

    private static class ValidateMissingStatistics {
        final Map<StationId, SortedSet<LocalDate>> expectedStatistics;

        private ValidateMissingStatistics(List<Station> stations, LocalDate from, LocalDate to) {
            expectedStatistics = new HashMap<>();
            stations.forEach(station -> expectedStatistics.put(station.stationId(), createDateRange(from, to)));
        }

        private SortedSet<LocalDate> createDateRange(LocalDate from, LocalDate to) {
            SortedSet<LocalDate> sortedSet = new TreeSet<>();
            LocalDate counter = LocalDate.from(from);
            while (counter.isBefore(to)) {
                sortedSet.add(counter);
                counter = counter.plusDays(1);
            }
            return sortedSet;
        }

        private void markOk(StationId stationId, LocalDate day) {
            ofNullable(expectedStatistics.get(stationId))
                    .ifPresent(set -> set.remove(day));
        }

        private void notifyMissingEventsIn(EventBus eventBus) {
            for (Map.Entry<StationId, SortedSet<LocalDate>> entry : expectedStatistics.entrySet()) {
                for (LocalDate date : entry.getValue()) {
                    eventBus.post(new MissingStationStatisticEvent(entry.getKey(), date));
                }

            }
        }
    }
}
