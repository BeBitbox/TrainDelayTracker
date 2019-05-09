package be.bitbox.traindelay.tracker.core.stationstatistic;

import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureRepository;
import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.stationstatistic.StationStatistic.StationStatisticBuilder.aStationStatistic;
import static be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent.Builder.createTrainDepartureEvent;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StationStatisticsEventHandlerTest {

    @Mock
    private EventBus eventBus;

    @Mock
    private TrainDepartureRepository trainDepartureRepository;

    @Mock
    private StationStatisticDao stationStatisticDao;

    @Test
    public void subscribe() {
        var stationId = StationId.aStationId("miepStation");
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

        var stationStatisticService = new StationStatisticsEventHandler(eventBus, trainDepartureRepository, stationStatisticDao);
        stationStatisticService.subscribe(new MissingStationStatisticEvent(stationId, date));

        var expectedStationStatistic = aStationStatistic()
                .withStationId(stationId)
                .withDay(date)
                .withAverageDelay(40)
                .withCancellations(1)
                .withDelays(2)
                .withPlatformChanges(1)
                .withDepartures(3)
                .build();
        verify(eventBus).register(stationStatisticService);
        verify(trainDepartureRepository).listTrainDepartureFor(stationId, date);
        verify(stationStatisticDao).save(expectedStationStatistic);
    }
}