package be.bitbox.traindelay.tracker.core.statistic;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.statistic.DailyStatistic.DayStatisticBuilder.aDayStatistic;
import static be.bitbox.traindelay.tracker.core.statistic.StationStatistic.StationStatisticBuilder.aStationStatistic;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MissingStatisticHandlerTest {

    @Mock
    private DailyStatisticDao dailyStatisticDao;

    @Mock
    private StationStatisticDao stationStatisticDao;

    @Mock
    private StationRetriever stationRetriever;

    @Mock
    private EventBus eventBus;

    @Test
    public void onCommandFor_AllRepresented() {
        var station1 = aStation(aStationId("station1"), "staion 1", Country.BE);
        var station2 = aStation(aStationId("station2"), "staion 2", Country.BE);
        var localDate = LocalDate.now();
        var stationStatistic1 = aStationStatistic()
                .withStationId(station1.stationId())
                .withAverageDelay(100)
                .withCancellations(5)
                .withDay(localDate)
                .withDelays(4)
                .withDepartures(10)
                .withPlatformChanges(4)
                .build();
        var stationStatistic2 = aStationStatistic()
                .withStationId(station1.stationId())
                .withAverageDelay(1200)
                .withCancellations(0)
                .withDay(localDate)
                .withDelays(0)
                .withDepartures(1)
                .withPlatformChanges(1)
                .build();

        when(stationRetriever.getStationsFor(Country.BE)).thenReturn(List.of(station1, station2));
        when(stationStatisticDao.getStationStatistic(station1.stationId(), localDate)).thenReturn(stationStatistic1);
        when(stationStatisticDao.getStationStatistic(station2.stationId(), localDate)).thenReturn(stationStatistic2);

        var missingStatisticHandler = new MissingStatisticHandler(dailyStatisticDao, stationStatisticDao, stationRetriever, eventBus, trainDepartureRepository);
        MissingDailyStatisticEvent missingDailyStatisticEvent = new MissingDailyStatisticEvent(localDate);
        missingStatisticHandler.onCommandFor(missingDailyStatisticEvent);

        var dailyStatistic = aDayStatistic()
                .withAverageDelay(200)
                .withCancellations(5)
                .withDay(localDate)
                .withDepartures(11)
                .withPlatformChanges(5)
                .withDelays(4)
                .build();
        verify(dailyStatisticDao).save(dailyStatistic);
        verifyZeroInteractions(eventBus);
    }

    @Test
    public void onCommandFor_withMissingInfo() {
        var station1 = aStation(aStationId("station1"), "staion 1", Country.BE);
        var station2 = aStation(aStationId("station2"), "staion 2", Country.BE);
        var localDate = LocalDate.now();
        var stationStatistic1 = aStationStatistic()
                .withStationId(station1.stationId())
                .withAverageDelay(100)
                .withCancellations(5)
                .withDay(localDate)
                .withDelays(4)
                .withDepartures(10)
                .withPlatformChanges(4)
                .build();

        when(stationRetriever.getStationsFor(Country.BE)).thenReturn(List.of(station1, station2));
        when(stationStatisticDao.getStationStatistic(station1.stationId(), localDate)).thenReturn(stationStatistic1);

        var missingStatisticHandler = new MissingStatisticHandler(dailyStatisticDao, stationStatisticDao, stationRetriever, eventBus, trainDepartureRepository);
        MissingDailyStatisticEvent missingDailyStatisticEvent = new MissingDailyStatisticEvent(localDate);
        missingStatisticHandler.onCommandFor(missingDailyStatisticEvent);

        verifyZeroInteractions(dailyStatisticDao);
        verify(eventBus).post(new MissingStationStatisticEvent(station2.stationId(), localDate));
    }
}