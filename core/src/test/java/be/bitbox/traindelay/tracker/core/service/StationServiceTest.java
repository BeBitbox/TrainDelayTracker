package be.bitbox.traindelay.tracker.core.service;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.StationNotFoundException;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureRepository;
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
    private TrainDepartureRepository trainDepartureRepository;

    private StationService stationService;

    @Before
    public void setUp() {
        stationService = new StationService(trainDepartureRepository, stationRetriever);
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
        var localDateTime1 = LocalDateTime.of(localDate, localTime1);
        var traindeparture1 = createTrainDepartureEvent()
                .withStationId(stationId)
                .withPlatform("3")
                .withDelay(120)
                .withVehicle("BE.NMBS.TSJOEK")
                .withExpectedDepartureTime(localDateTime1)
                .build();
        var localTime2 = LocalTime.of(10, 52, 31);
        var localDateTime2 = LocalDateTime.of(localDate, localTime2);
        var traindeparture2 = createTrainDepartureEvent()
                .withStationId(stationId)
                .withPlatform("B")
                .withDelay(0)
                .withVehicle("TRAIN")
                .withExpectedDepartureTime(localDateTime2)
                .build();
        when(trainDepartureRepository.listTrainDepartureFor(stationId, localDate)).thenReturn(List.of(traindeparture1, traindeparture2));

        var jsonTrainDepartures = stationService.listTrainDeparturesFor(stationId, localDate);
        verify(trainDepartureRepository).listTrainDepartureFor(stationId, localDate);
        assertThat(jsonTrainDepartures.size(), is(2));
        var firstDeparture = jsonTrainDepartures.first();
        assertThat(firstDeparture.getDelay(), is(0));
        assertThat(firstDeparture.getExpectedDepartureTime(), is(localDateTime2));
        assertThat(firstDeparture.getVehicle(), is("TRAIN"));
        assertThat(firstDeparture.getPlatform(), is("B"));

        var secondDeparture = jsonTrainDepartures.last();
        assertThat(secondDeparture.getDelay(), is(2));
        assertThat(secondDeparture.getExpectedDepartureTime(), is(localDateTime1));
        assertThat(secondDeparture.getVehicle(), is("TSJOEK"));
        assertThat(secondDeparture.getPlatform(), is("3"));
    }
}