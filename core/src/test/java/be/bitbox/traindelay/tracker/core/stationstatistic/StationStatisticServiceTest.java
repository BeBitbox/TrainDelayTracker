package be.bitbox.traindelay.tracker.core.stationstatistic;

import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StationStatisticServiceTest {
    @Mock
    private EventBus eventBus;

    @Mock
    private StationStatisticDao stationStatisticdDao;

    @Test
    public void getStationStatistic_happyCase() {
        var stationId = aStationId("station");
        var localDate = LocalDate.now();
        var stationStatistic = new StationStatistic();
        when(stationStatisticdDao.getStationStatistic(stationId, localDate)).thenReturn(stationStatistic);
        var stationStatisticService = new StationStatisticService(stationStatisticdDao, eventBus);

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
        var stationStatisticService = new StationStatisticService(stationStatisticdDao, eventBus);

        var actualStationStatic = stationStatisticService.getStationStatisticFor(stationId, localDate);

        assertThat(actualStationStatic).isNull();
        verify(stationStatisticdDao).getStationStatistic(stationId, localDate);
        verify(eventBus).post(new MissingStationStatisticEvent(stationId, localDate));
    }
}