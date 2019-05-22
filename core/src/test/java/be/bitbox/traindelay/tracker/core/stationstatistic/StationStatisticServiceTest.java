package be.bitbox.traindelay.tracker.core.stationstatistic;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.stationstatistic.StationStatistic.StationStatisticBuilder.aStationStatistic;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StationStatisticServiceTest {
    @Mock
    private EventBus eventBus;

    @Mock
    private StationStatisticDao stationStatisticdDao;

    @Mock
    private StationRetriever stationRetriever;

    @Test
    public void getStationStatistic_happyCase() {
        var stationId = aStationId("station");
        var localDate = LocalDate.now();
        var stationStatistic = new StationStatistic();
        when(stationStatisticdDao.getStationStatistic(stationId, localDate)).thenReturn(stationStatistic);
        var stationStatisticService = new StationStatisticService(stationStatisticdDao, eventBus, stationRetriever);

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
        var stationStatisticService = new StationStatisticService(stationStatisticdDao, eventBus, stationRetriever);

        var actualStationStatic = stationStatisticService.getStationStatisticFor(stationId, localDate);

        assertThat(actualStationStatic).isNull();
        verify(stationStatisticdDao).getStationStatistic(stationId, localDate);
        verify(eventBus).post(new MissingStationStatisticEvent(stationId, localDate));
    }

    @Test
    public void getYearlyStationStatistics() {
        StationId stationId1 = aStationId("station1");
        Station station1 = aStation(stationId1, "Station 1", Country.BE);
        StationId stationId2 = aStationId("station2");
        Station station2 = aStation(stationId2, "Station 2", Country.BE);
        LocalDate from = LocalDate.now().minusYears(1).minusDays(1);
        LocalDate to = LocalDate.now();

        when(stationRetriever.getStationsFor(Country.BE)).thenReturn(List.of(station1, station2));
        when(stationStatisticdDao.getStationStatistic(stationId1, from, to)).thenReturn(new ArrayList<>());
        StationStatistic stationStatistic1 = aStationStatistic()
                .withStationId(stationId2)
                .withDepartures(4)
                .withPlatformChanges(1)
                .withCancellations(3)
                .withDelays(2)
                .withDay(from)
                .withAverageDelay(32)
                .build();
        StationStatistic stationStatistic2 = aStationStatistic()
                .withStationId(stationId2)
                .withDepartures(40)
                .withPlatformChanges(10)
                .withCancellations(30)
                .withDelays(20)
                .withDay(from.plusDays(1))
                .withAverageDelay(42)
                .build();
        when(stationStatisticdDao.getStationStatistic(stationId2, from, to)).thenReturn(List.of(stationStatistic1, stationStatistic2));
        var stationStatisticService = new StationStatisticService(stationStatisticdDao, eventBus, stationRetriever);

        var actualStationStatic = stationStatisticService.getFullStationStaticFromLastYear();

        StationStatistic expectedStationStatistic = aStationStatistic()
                .withStationId(null)
                .withDepartures(44)
                .withPlatformChanges(11)
                .withCancellations(33)
                .withDelays(22)
                .withDay(null)
                .withAverageDelay(37)
                .build();
        assertThat(actualStationStatic).isEqualTo(expectedStationStatistic);
        var missingStationStatisticEventList = getMissingStationStatisticEvents(stationId1, stationId2);
        missingStationStatisticEventList.forEach(event -> verify(eventBus).post(event));
        verify(eventBus, times(missingStationStatisticEventList.size())).post(any());

        clearInvocations(eventBus);
        var cachedStationStatic = stationStatisticService.getFullStationStaticFromLastYear();
        assertThat(expectedStationStatistic).isEqualTo(cachedStationStatic);
        verifyZeroInteractions(eventBus);
    }

    private List<MissingStationStatisticEvent> getMissingStationStatisticEvents(StationId stationId1, StationId stationId2) {
        List<MissingStationStatisticEvent> missingStationStatisticEventList = new ArrayList<>();
        LocalDate counter = LocalDate.now().minusYears(1).minusDays(1);
        while (counter.isBefore(LocalDate.now())) {
            missingStationStatisticEventList.add(new MissingStationStatisticEvent(stationId1, counter));
            counter = counter.plusDays(1);
        }
        counter = LocalDate.now().minusYears(1).plusDays(1);
        while (counter.isBefore(LocalDate.now())) {
            missingStationStatisticEventList.add(new MissingStationStatisticEvent(stationId2, counter));
            counter = counter.plusDays(1);
        }
        return missingStationStatisticEventList;
    }
}