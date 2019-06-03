package be.bitbox.traindelay.tracker.core.statistic;

import be.bitbox.traindelay.tracker.core.station.StationId;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class StatisticService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticService.class);
    private final StationStatisticDao stationStatisticDao;
    private final EventBus eventBus;
    private final DailyStatisticDao dailyStatisticDao;
    private YearlyStatistic stationStatisticOfYear;
    private LocalDateTime expiredTime;

    @Autowired
    public StatisticService(StationStatisticDao stationStatisticDao, EventBus eventBus, DailyStatisticDao dailyStatisticDao) {
        this.stationStatisticDao = stationStatisticDao;
        this.eventBus = eventBus;
        this.dailyStatisticDao = dailyStatisticDao;
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

    public YearlyStatistic getFullStationStaticFromLastYear() {
        if (expiredTime == null || LocalDateTime.now().isAfter(expiredTime)) {
            this.stationStatisticOfYear = createFullStaticFromLastYear();
            this.expiredTime = LocalDateTime.now().plusHours(1);
        }
        return stationStatisticOfYear;
    }

    private YearlyStatistic createFullStaticFromLastYear() {
        var today = LocalDate.now();
        var oneYearAgo = today.minusYears(1).minusDays(1);
        var dailyStatisticList = dailyStatisticDao.getDayStatistic(oneYearAgo, today);
        Set<LocalDate> expectedDates = getAllExpectedDates(today, oneYearAgo);

        dailyStatisticList.forEach(dailyStatistic -> expectedDates.remove(dailyStatistic.getDay()));
        expectedDates.forEach(localDate -> eventBus.post(new MissingDailyStatisticEvent(localDate)));
        return new YearlyStatistic(dailyStatisticList);
    }

    private Set<LocalDate> getAllExpectedDates(LocalDate today, LocalDate oneYearAgo) {
        Set<LocalDate> expectedDates = new HashSet<>();
        var counterDay = oneYearAgo;
        while (counterDay.isBefore(today)) {
            expectedDates.add(counterDay);
            counterDay = counterDay.plusDays(1);
        }
        return expectedDates;
    }
}
