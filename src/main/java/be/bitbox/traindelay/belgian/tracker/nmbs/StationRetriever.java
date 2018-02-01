/*
 * Copyright 2018 Bitbox : BelgianTrainDelayTracker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.bitbox.traindelay.belgian.tracker.nmbs;

import be.bitbox.traindelay.belgian.tracker.station.Country;
import be.bitbox.traindelay.belgian.tracker.station.GeoCoordinates;
import be.bitbox.traindelay.belgian.tracker.station.Station;
import be.bitbox.traindelay.belgian.tracker.station.StationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;

import static be.bitbox.traindelay.belgian.tracker.ValidationUtils.checkNotEmpty;
import static be.bitbox.traindelay.belgian.tracker.ValidationUtils.isNumeric;
import static be.bitbox.traindelay.belgian.tracker.station.GeoCoordinates.aGeoCoordinates;
import static be.bitbox.traindelay.belgian.tracker.station.Station.aStation;
import static be.bitbox.traindelay.belgian.tracker.station.StationId.aStationId;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class StationRetriever {
    private final static Logger LOGGER = LoggerFactory.getLogger(StationRetriever.class);
    private final String nmbsBaseUrl;

    private List<Station> stations;

    @Autowired
    StationRetriever(@Value("${tracker.base.url}") String nmbsBaseUrl) throws FileNotFoundException {
        this.nmbsBaseUrl = nmbsBaseUrl;
    }

    public List<Station> getBelgianStations() {
        if (stations == null) {
            readInBelgianStations();
        }
        return stations;
    }

    private void readInBelgianStations() {
        InputStream inputStream = StationRetriever.class.getResourceAsStream("stations.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        stations = reader.lines()
                .skip(1)
                .map(TranslateCSVStringToStation())
                .filter(csvStation -> "be".equals(csvStation.countryCode))
                .map(CSVStation::translateToStation)
                .parallel()
                .filter(station -> isTrainStationKnown(station.stationId()))
                .collect(toList());
    }

    private boolean isTrainStationKnown(StationId stationId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = nmbsBaseUrl + stationId.getId() + "&format=json";
        try {
            restTemplate.getForObject(url, LiveBoard.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            if (ex.getStatusCode() == NOT_FOUND || ex.getStatusCode() == INTERNAL_SERVER_ERROR) {
                LOGGER.error("Did not find station with " + stationId + " and code " + ex.getStatusText());
                return false;
            }
        }
        return true;
    }


    private Function<String, CSVStation> TranslateCSVStringToStation() {
        return line -> {
            String[] split = line.split(",");
            if (split.length < 9) {
                throw new IllegalArgumentException("Not expected length of CSV-line, expected 10 but got " + split.length);
            }

            CSVStation csvStation = new CSVStation();
            csvStation.URI = split[0];
            csvStation.name = split[1];
            csvStation.alternativeFr = split[2];
            csvStation.alternativeNl = split[3];
            csvStation.alternativeDe = split[4];
            csvStation.alternativeEn = split[5];
            csvStation.countryCode = split[6];
            csvStation.longitude = split[7];
            csvStation.latitude = split[8];
            return csvStation;
        };
    }


    private class CSVStation {
        private String URI, name, alternativeFr, alternativeNl, alternativeDe, alternativeEn, countryCode, longitude, latitude;

        Station translateToStation() {
            StationId stationId = aStationId(retrieveIdFromURI());
            Station station = aStation(stationId, name, Country.translateFrom(countryCode));
            if (!isEmpty(alternativeFr)) {
                station.withAlternativeFrenchName(alternativeFr);
            }
            if (!isEmpty(alternativeNl)) {
                station.withAlternativeDutchName(alternativeNl);
            }
            if (!isEmpty(alternativeDe)) {
                station.withAlternativeGermanName(alternativeDe);
            }
            if (!isEmpty(alternativeEn)) {
                station.withAlternativeEnglishName(alternativeEn);
            }
            GeoCoordinates geoCoordinates = getGeoCoordinatesFromLongitudeAndLatitude();
            if (geoCoordinates != null) {
                station.withGeoCoordinates(geoCoordinates);
            }

            return station;
        }

        private GeoCoordinates getGeoCoordinatesFromLongitudeAndLatitude() {
            if (isNumeric(longitude) && isNumeric(latitude)) {
                double x = Double.valueOf(longitude);
                double y = Double.valueOf(latitude);
                return aGeoCoordinates(x, y);
            }
            return null;
        }

        private String retrieveIdFromURI() {
            checkNotEmpty(URI, "Expected filled in URI for " + toString());
            int lastIndexOf = URI.lastIndexOf('/');
            if (lastIndexOf > 0) {
                return "BE.NMBS." + URI.substring(lastIndexOf + 1);
            }
            throw new IllegalArgumentException("Expected valid URI for " + toString());
        }

        @Override
        public String toString() {
            return "CSVStation{" +
                    "URI='" + URI + '\'' +
                    ", name='" + name + '\'' +
                    ", alternativeFr='" + alternativeFr + '\'' +
                    ", alternativeNl='" + alternativeNl + '\'' +
                    ", alternativeDe='" + alternativeDe + '\'' +
                    ", alternativeEn='" + alternativeEn + '\'' +
                    ", countryCode='" + countryCode + '\'' +
                    ", longitude='" + longitude + '\'' +
                    ", latitude='" + latitude + '\'' +
                    '}';
        }
    }
}