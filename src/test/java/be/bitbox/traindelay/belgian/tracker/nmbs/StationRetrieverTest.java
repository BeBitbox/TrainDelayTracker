package be.bitbox.traindelay.belgian.tracker.nmbs;

import be.bitbox.traindelay.belgian.tracker.station.Station;
import be.bitbox.traindelay.belgian.tracker.station.StationId;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import static be.bitbox.traindelay.belgian.tracker.station.Country.BE;
import static be.bitbox.traindelay.belgian.tracker.station.GeoCoordinates.aGeoCoordinates;
import static be.bitbox.traindelay.belgian.tracker.station.Station.aStation;
import static be.bitbox.traindelay.belgian.tracker.station.StationId.aStationId;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StationRetrieverTest {

    @Test
    public void getBelgianStations() throws FileNotFoundException {
        StationRetriever retriever = new StationRetriever();

        List<Station> stations = retriever.getBelgianStations();

        assertThat(stations, hasSize(557));
        StationId expectedFirstStationId = aStationId("BE.NMBS.008895000");
        Station expectedStation = aStation(expectedFirstStationId, "Aalst", BE)
                .withAlternativeFrenchName("Alost")
                .withGeoCoordinates(aGeoCoordinates(4.039653,50.942813));
        assertThat(stations.get(0), is(expectedStation));

        Station expectedFullStation = aStation(aStationId("BE.NMBS.008881158"), "Erbisœul", BE)
                .withAlternativeFrenchName("Erbisoeul")
                .withAlternativeEnglishName("Erbisoeul")
                .withAlternativeGermanName("Erbisoeul")
                .withAlternativeDutchName("Erbisoeul")
                .withGeoCoordinates(aGeoCoordinates(3.887987,50.507025));
        Optional<Station> optionalStation = stations.stream()
                .filter(station -> aStationId("BE.NMBS.008881158").equals(station.stationId()))
                .findAny();
        assertThat(optionalStation.isPresent(), is(true));
        assertThat(optionalStation.get(), is(expectedFullStation));
    }
}