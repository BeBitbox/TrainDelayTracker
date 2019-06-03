package be.bitbox.traindelay.tracker.core.statistic;

import com.google.common.eventbus.EventBus;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.statistic.DailyStatistic.DayStatisticBuilder.aDayStatistic;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticServiceTest {
    @Mock
    private EventBus eventBus;

    @Mock
    private StationStatisticDao stationStatisticdDao;

    @Mock
    private DailyStatisticDao dailyStatisticDao;

    @Test
    public void getStationStatistic_happyCase() {
        var stationId = aStationId("station");
        var localDate = LocalDate.now();
        var stationStatistic = new StationStatistic();
        when(stationStatisticdDao.getStationStatistic(stationId, localDate)).thenReturn(stationStatistic);
        var stationStatisticService = new StatisticService(stationStatisticdDao, eventBus, dailyStatisticDao);

        var actualStationStatic = stationStatisticService.getStationStatisticFor(stationId, localDate);

        assertThat(actualStationStatic).isEqualTo(stationStatistic);
        verify(stationStatisticdDao).getStationStatistic(stationId, localDate);
        verifyZeroInteractions(eventBus);
    }

    @Test
    public void getStationStatistic_ErrorsThrown() {
        var stationId = aStationId("station");
        var localDate = LocalDate.now();
        when(stationStatisticdDao.getStationStatistic(stationId, localDate)).thenThrow(new IllegalArgumentException("BOYCOT"));
        var stationStatisticService = new StatisticService(stationStatisticdDao, eventBus, dailyStatisticDao);

        var actualStationStatic = stationStatisticService.getStationStatisticFor(stationId, localDate);

        assertThat(actualStationStatic).isNull();
        verify(stationStatisticdDao).getStationStatistic(stationId, localDate);
        verify(eventBus).post(new MissingStationStatisticEvent(stationId, localDate));
    }

    @Test
    public void getYearlyStationStatistics() {
        LocalDate from = LocalDate.now().minusYears(1).minusDays(1);
        LocalDate to = LocalDate.now();
        DailyStatistic dailyStatistic1 = aDayStatistic()
                .withDepartures(4)
                .withPlatformChanges(1)
                .withCancellations(3)
                .withDelays(2)
                .withDay(from)
                .withAverageDelay(32)
                .build();
        DailyStatistic dailyStatistic2 = aDayStatistic()
                .withDepartures(40)
                .withPlatformChanges(10)
                .withCancellations(30)
                .withDelays(20)
                .withDay(from.plusDays(1))
                .withAverageDelay(42)
                .build();
        when(dailyStatisticDao.getDayStatistic(from, to)).thenReturn(List.of(dailyStatistic1, dailyStatistic2));

        var stationStatisticService = new StatisticService(stationStatisticdDao, eventBus, dailyStatisticDao);

        var actualStationStatic = stationStatisticService.getFullStationStaticFromLastYear();
        assertThat(actualStationStatic.getDepartures()).isEqualTo(44);
        assertThat(actualStationStatic.getPlatformChanges()).isEqualTo(11);
        assertThat(actualStationStatic.getCancellations()).isEqualTo(33);
        assertThat(actualStationStatic.getDelays()).isEqualTo(22);
        assertThat(actualStationStatic.getAverageDelay()).isEqualTo(41);

        var expectedEvents = getMissingDailyStatisticEvents(from.plusDays(2), to);
        verify(eventBus, times(expectedEvents.size())).post(any());
        expectedEvents.forEach(event -> verify(eventBus).post(event));
    }

    private List<MissingDailyStatisticEvent> getMissingDailyStatisticEvents(LocalDate from, LocalDate to) {
        List<MissingDailyStatisticEvent> missingDailyStatisticEvents = new ArrayList<>();
        LocalDate counter = from;
        while (counter.isBefore(to)) {
            missingDailyStatisticEvents.add(new MissingDailyStatisticEvent(counter));
            counter = counter.plusDays(1);
        }
        return missingDailyStatisticEvents;
    }
}