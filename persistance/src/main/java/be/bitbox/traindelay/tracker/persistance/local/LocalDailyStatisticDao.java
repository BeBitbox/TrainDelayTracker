package be.bitbox.traindelay.tracker.persistance.local;

import be.bitbox.traindelay.tracker.core.statistic.DailyStatistic;
import be.bitbox.traindelay.tracker.core.statistic.DailyStatisticDao;

import java.time.LocalDate;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.statistic.DailyStatistic.DayStatisticBuilder.aDayStatistic;

public class LocalDailyStatisticDao implements DailyStatisticDao {

    @Override
    public List<DailyStatistic> getDayStatistic(LocalDate from, LocalDate toIncluded) {
        return List.of(aDayStatistic()
                .withDepartures(4)
                .withPlatformChanges(1)
                .withCancellations(3)
                .withDelays(2)
                .withDay(LocalDate.now())
                .withAverageDelay(32)
                .build());
    }

    @Override
    public DailyStatistic getDayStatistic(LocalDate date) {
        return aDayStatistic()
                .withDepartures(4)
                .withPlatformChanges(1)
                .withCancellations(3)
                .withDelays(2)
                .withDay(LocalDate.now())
                .withAverageDelay(32)
                .build();
    }

    @Override
    public void save(DailyStatistic dailyStatistic) {

    }
}
