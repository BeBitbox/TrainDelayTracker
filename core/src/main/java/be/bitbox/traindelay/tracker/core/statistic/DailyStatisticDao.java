package be.bitbox.traindelay.tracker.core.statistic;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatisticDao {

    List<DailyStatistic> getDayStatistic(LocalDate from, LocalDate toIncluded);

    DailyStatistic getDayStatistic(LocalDate date);
}
