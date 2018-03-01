package be.bitbox.traindelay.tracker.core.station;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StationAvailabilityMonitorTest {

    @Mock
    private StationRetriever stationRetriever;

    @Test
    public void testAlwaysAvailableTrainStation() {
        Station station = aStation(aStationId("MYStation"), "my", Country.BE);
        when(stationRetriever.getBelgianStations()).thenReturn(singletonList(station));

        StationAvailabilityMonitor monitor = new StationAvailabilityMonitor(stationRetriever);
        for (int counter = 0; counter < 10; counter++) {
            monitor.positiveFeedbackFor(station);
            assertThat(monitor.getTrainStations()).containsExactly(station);
        }
    }

    @Test
    public void testAlwaysUnAvailableTrainStation() {
        Station station = aStation(aStationId("BadStation"), "bad", Country.BE);
        when(stationRetriever.getBelgianStations()).thenReturn(singletonList(station));

        StationAvailabilityMonitor monitor = new StationAvailabilityMonitor(stationRetriever);

        monitor.negativeFeedbackFor(station);
        int numberOfStationAppearences = 0;
        for (int counter = 0; counter < 20; counter++) {
            if (monitor.getTrainStations().contains(station)) {
                numberOfStationAppearences++;
                monitor.negativeFeedbackFor(station);
            }
        }
        assertThat(numberOfStationAppearences).isEqualTo(4);
    }

    @Test
    public void testMixOfTrainStation() {
        Station goodStation = aStation(aStationId("GoodStation"), "good", Country.BE);
        Station badStation = aStation(aStationId("BadStation"), "bad", Country.BE);
        Station neutralStation = aStation(aStationId("NeutralStation"), "neu", Country.BE);
        when(stationRetriever.getBelgianStations()).thenReturn(Arrays.asList(goodStation, badStation, neutralStation));

        StationAvailabilityMonitor monitor = new StationAvailabilityMonitor(stationRetriever);

        monitor.positiveFeedbackFor(goodStation);
        monitor.negativeFeedbackFor(badStation);
        monitor.positiveFeedbackFor(neutralStation);
        assertThat(monitor.getTrainStations()).containsOnly(goodStation, neutralStation);

        monitor.positiveFeedbackFor(goodStation);
        monitor.negativeFeedbackFor(badStation);
        monitor.negativeFeedbackFor(neutralStation);
        assertThat(monitor.getTrainStations()).containsOnly(goodStation);
        assertThat(monitor.getTrainStations()).containsOnly(goodStation);
        assertThat(monitor.getTrainStations()).containsOnly(goodStation);
        assertThat(monitor.getTrainStations()).containsOnly(goodStation);
        assertThat(monitor.getTrainStations()).containsOnly(goodStation, neutralStation, badStation);

    }
}