/*
 * Copyright 2018 Bitbox : TrainDelayTracker
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
package be.bitbox.traindelay.tracker.nmbs;

import be.bitbox.traindelay.tracker.core.station.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;

import static be.bitbox.traindelay.tracker.core.ValidationUtils.checkNotEmpty;
import static be.bitbox.traindelay.tracker.core.ValidationUtils.isNumeric;
import static be.bitbox.traindelay.tracker.core.station.GeoCoordinates.aGeoCoordinates;
import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class NMBSStationRetriever implements StationRetriever {
    private final static Logger LOGGER = LoggerFactory.getLogger(NMBSStationRetriever.class);

    private List<Station> stations;

    @Override
    public List<Station> getStationsFor(Country country) {
        if (country == Country.BE) {
            if (stations == null) {
                stations = readInBelgianStations();
            }
            return stations;
        }
        return emptyList();
    }

    private List<Station> readInBelgianStations() {
        InputStream inputStream = NMBSStationRetriever.class.getResourceAsStream("stations.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        List<Station> stationList = reader.lines()
                .skip(1)
                .map(TranslateCSVStringToStation())
                .filter(csvStation -> "be".equals(csvStation.countryCode))
                .map(CSVStation::translateToStation)
                .collect(toList());
        LOGGER.info("Read in all Belgian stations : {} in total", stationList.size());
        return stationList;
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