package be.bitbox.traindelay.tracker.core.service;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.StationNotFoundException;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent.Builder.createTrainDepartureEvent;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StationServiceTest {

    @Mock
    private StationRetriever stationRetriever;

    @Mock
    private TrainDepartureQuery trainDepartureQuery;

    private StationService stationService;

    @Before
    public void setUp() {
        stationService = new StationService(trainDepartureQuery, stationRetriever);
    }

    @Test(expected = StationNotFoundException.class)
    public void listTrainDeparturesUnexistingStation_LocalDate() {
        var station = aStation(aStationId("MyStation"), "my", Country.BE);
        when(stationRetriever.getStationsFor(Country.BE)).thenReturn(singletonList(station));

        stationService.listTrainDeparturesFor(aStationId("otherStation"), LocalDate.now());
    }

    @Test(expected = StationNotFoundException.class)
    public void listTrainDeparturesUnexistingStation() {
        var station = aStation(aStationId("MyStation"), "my", Country.BE);
        when(stationRetriever.getStationsFor(Country.BE)).thenReturn(singletonList(station));

        stationService.listTrainDeparturesFor(aStationId("otherStation"), "2018-02-01");
    }

    @Test(expected = InvalidDateException.class)
    public void listTrainDeparturesUnexistingDateFormat() {
        stationService.listTrainDeparturesFor(aStationId("station"), "2018-0201");
    }

    @Test(expected = InvalidDateException.class)
    public void listTrainDepartures_TooEarlyDate() {
        var station = aStation(aStationId("station"), "My Station", Country.BE);
        when(stationRetriever.getStationsFor(Country.BE)).thenReturn(singletonList(station));

        var earlyDate = "2016-12-30";
        stationService.listTrainDeparturesFor(aStationId("station"), earlyDate);
    }

    @Test(expected = InvalidDateException.class)
    public void listTrainDepartures_TooEarlyDate_LocalDate() {
        var station = aStation(aStationId("station"), "My Station", Country.BE);
        when(stationRetriever.getStationsFor(Country.BE)).thenReturn(singletonList(station));

        var earlyDate = LocalDate.of(2016, Month.DECEMBER, 3);
        stationService.listTrainDeparturesFor(aStationId("station"), earlyDate);
    }

    @Test
    public void listTrainDepartures() {
        var stationId = aStationId("station");
        var station = aStation(stationId, "My Station", Country.BE);
        var localDate = LocalDate.ofYearDay(2018, 99);
        when(stationRetriever.getStationsFor(Country.BE)).thenReturn(singletonList(station));
        var localTime1 = LocalTime.of(11, 56, 34);
        var traindeparture1 = createTrainDepartureEvent()
                .withStationId(stationId)
                .withPlatform("3")
                .withDelay(120)
                .withVehicle("BE.NMBS.TSJOEK")
                .withExpectedDepartureTime(LocalDateTime.of(localDate, localTime1))
                .build();
        var localTime2 = LocalTime.of(10, 52, 31);
        var traindeparture2 = createTrainDepartureEvent()
                .withStationId(stationId)
                .withPlatform("B")
                .withDelay(0)
                .withVehicle("TRAIN")
                .withExpectedDepartureTime(LocalDateTime.of(localDate, localTime2))
                .build();
        when(trainDepartureQuery.listTrainDepartureFor(stationId, localDate)).thenReturn(List.of(traindeparture1, traindeparture2));

        var trainDepartureVos = stationService.listTrainDeparturesFor(stationId, localDate);
        verify(trainDepartureQuery).listTrainDepartureFor(stationId, localDate);
        assertThat(trainDepartureVos.size(), is(2));
        var firstVo = trainDepartureVos.first();
        assertThat(firstVo.getDelay(), is(0));
        assertThat(firstVo.getLocalTime(), is(localTime2));
        assertThat(firstVo.getVehicle(), is("TRAIN"));
        assertThat(firstVo.getPlatform(), is("B"));

        var secondVo = trainDepartureVos.last();
        assertThat(secondVo.getDelay(), is(2));
        assertThat(secondVo.getLocalTime(), is(localTime1));
        assertThat(secondVo.getVehicle(), is("TSJOEK"));
        assertThat(secondVo.getPlatform(), is("3"));
    }
}