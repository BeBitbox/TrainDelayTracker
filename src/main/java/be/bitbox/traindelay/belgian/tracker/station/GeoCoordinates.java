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

public class GeoCoordinates {
    private final double longitude;
    private final double altitude;

    private GeoCoordinates(double longitude, double altitude) {
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public static GeoCoordinates aGeoCoordinates(double longitude, double altitude) {
        return new GeoCoordinates(longitude, altitude);
    }

    public double longitude() {
        return longitude;
    }

    public double altitude() {
        return altitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoCoordinates that = (GeoCoordinates) o;

        if (Double.compare(that.longitude, longitude) != 0) return false;
        return Double.compare(that.altitude, altitude) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(longitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "GeoCoordinates{" +
                "longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }
}