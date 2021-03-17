package be.bitbox.traindelay.tracker.core.station;

import be.bitbox.traindelay.tracker.core.service.MissingStatisticHandler;
import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import be.bitbox.traindelay.tracker.core.statistic.DailyStatisticDao;
import be.bitbox.traindelay.tracker.core.statistic.MissingDailyStatisticEvent;
import be.bitbox.traindelay.tracker.core.statistic.MissingStationStatisticEvent;
import be.bitbox.traindelay.tracker.core.statistic.StationStatisticDao;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureRepository;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.statistic.DailyStatistic.DayStatisticBuilder.aDayStatistic;
import static be.bitbox.traindelay.tracker.core.statistic.StationStatistic.StationStatisticBuilder.aStationStatistic;
import static be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent.Builder.createTrainDepartureEvent;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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

    @Mock
    private TrainDepartureRepository trainDepartureRepository;

    @Test
    public void onCommandForDaily_AllRepresented() {
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
        verifyNoInteractions(eventBus);
        verify(stationStatisticDao).getStationStatistic(station1.stationId(), localDate);
        verify(stationStatisticDao).getStationStatistic(station2.stationId(), localDate);
    }

    @Test
    public void onCommandForDaily_withMissingInfo() {
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

        verifyNoInteractions(dailyStatisticDao);
        verify(stationStatisticDao).getStationStatistic(station1.stationId(), localDate);
        verify(stationStatisticDao).getStationStatistic(station2.stationId(), localDate);
        verify(eventBus).post(new MissingStationStatisticEvent(station2.stationId(), localDate));
    }

    @Test
    public void onCommandForMissingStationStatistic() {
        var stationId = aStationId("miepStation");
        var date = LocalDate.now();

        var departureEvent1 = createTrainDepartureEvent()
                .withStationId(stationId)
                .withPlatform("3")
                .withDelay(100)
                .withVehicle("BE.NMBS.TSJOEK1")
                .withExpectedDepartureTime(LocalDateTime.now())
                .build();
        var departureEvent2 = createTrainDepartureEvent()
                .withStationId(stationId)
                .withPlatform("3")
                .withDelay(20)
                .withCanceled(true)
                .withVehicle("BE.NMBS.TSJOEK2")
                .withExpectedDepartureTime(LocalDateTime.now())
                .build();
        var departureEvent3 = createTrainDepartureEvent()
                .withStationId(stationId)
                .withPlatform("3")
                .withDelay(0)
                .withVehicle("BE.NMBS.TSJOEK3")
                .withPlatformChange(true)
                .withExpectedDepartureTime(LocalDateTime.now())
                .build();
        when(trainDepartureRepository.listTrainDepartureFor(stationId, date)).thenReturn(List.of(departureEvent1, departureEvent2, departureEvent3));

        var missingStatisticHandler = new MissingStatisticHandler(dailyStatisticDao, stationStatisticDao, stationRetriever, eventBus, trainDepartureRepository);
        var missingStationStatisticEvent = new MissingStationStatisticEvent(stationId, date);
        missingStatisticHandler.onCommandFor(missingStationStatisticEvent);

        verifyNoInteractions(dailyStatisticDao);
        verifyNoInteractions(eventBus);
        var expectedStationStatistic = aStationStatistic()
                .withStationId(stationId)
                .withDay(date)
                .withAverageDelay(40)
                .withCancellations(1)
                .withDelays(2)
                .withPlatformChanges(1)
                .withDepartures(3)
                .build();
        verify(trainDepartureRepository).listTrainDepartureFor(stationId, date);
        verify(stationStatisticDao).save(expectedStationStatistic);
    }
}