package be.bitbox.traindelay.tracker.nmbs;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static be.bitbox.traindelay.tracker.core.station.Country.BE;
import static be.bitbox.traindelay.tracker.core.station.GeoCoordinates.aGeoCoordinates;
import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class NMBSStationRetrieverTest {

    @Test
    public void getBelgianStations() {
        StationRetriever retriever = new NMBSStationRetriever();

        List<Station> stations = retriever.getStationsFor(Country.BE);

        assertThat(stations, hasSize(556));
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