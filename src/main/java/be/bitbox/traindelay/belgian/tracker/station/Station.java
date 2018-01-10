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
package be.bitbox.traindelay.belgian.tracker.station;

import static be.bitbox.traindelay.belgian.tracker.ValidationUtils.checkNotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public class Station {
    private final StationId stationId;
    private final String name;
    private final Country country;

    private String alternativeFr;
    private String alternativeNl;
    private String alternativeDe;
    private String alternativeEn;
    private GeoCoordinates geoCoordinates;

    private Station(StationId stationId, String name, Country country) {
        this.stationId = stationId;
        this.name = name;
        this.country = country;
    }

    public static Station aStation(StationId stationId, String name, Country country) {
        checkNotNull(stationId, "A station needs to have a stationId");
        checkNotEmpty(name, "A station needs to have a name");
        checkNotNull(country, "A station needs to be located in a country");
        return new Station(stationId, name, country);
    }

    public Station withAlternativeFrenchName(String alternativeFr) {
        this.alternativeFr = alternativeFr;
        return this;
    }

    public Station withAlternativeDutchName(String alternativeNl) {
        this.alternativeNl = alternativeNl;
        return this;
    }

    public Station withAlternativeGermanName(String alternativeDe) {
        this.alternativeDe = alternativeDe;
        return this;
    }

    public Station withAlternativeEnglishName(String alternativeEn) {
        this.alternativeEn = alternativeEn;
        return this;
    }

    public Station withGeoCoordinates(GeoCoordinates geoCoordinates) {
        this.geoCoordinates = geoCoordinates;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        if (!stationId.equals(station.stationId)) return false;
        if (!name.equals(station.name)) return false;
        if (country != station.country) return false;
        if (alternativeFr != null ? !alternativeFr.equals(station.alternativeFr) : station.alternativeFr != null)
            return false;
        if (alternativeNl != null ? !alternativeNl.equals(station.alternativeNl) : station.alternativeNl != null)
            return false;
        if (alternativeDe != null ? !alternativeDe.equals(station.alternativeDe) : station.alternativeDe != null)
            return false;
        if (alternativeEn != null ? !alternativeEn.equals(station.alternativeEn) : station.alternativeEn != null)
            return false;
        return geoCoordinates != null ? geoCoordinates.equals(station.geoCoordinates) : station.geoCoordinates == null;
    }

    @Override
    public int hashCode() {
        int result = stationId.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + country.hashCode();
        result = 31 * result + (alternativeFr != null ? alternativeFr.hashCode() : 0);
        result = 31 * result + (alternativeNl != null ? alternativeNl.hashCode() : 0);
        result = 31 * result + (alternativeDe != null ? alternativeDe.hashCode() : 0);
        result = 31 * result + (alternativeEn != null ? alternativeEn.hashCode() : 0);
        result = 31 * result + (geoCoordinates != null ? geoCoordinates.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Station{" +
                "stationId=" + stationId +
                ", name='" + name + '\'' +
                ", country=" + country +
                ", alternativeFr='" + alternativeFr + '\'' +
                ", alternativeNl='" + alternativeNl + '\'' +
                ", alternativeDe='" + alternativeDe + '\'' +
                ", alternativeEn='" + alternativeEn + '\'' +
                ", geoCoordinates=" + geoCoordinates +
                '}';
    }

    public StationId stationId() {
        return stationId;
    }
}