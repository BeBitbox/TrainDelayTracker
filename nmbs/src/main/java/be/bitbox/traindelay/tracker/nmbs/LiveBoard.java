/*
 * Copyright 2017 Bitbox : TrainDelayTracker
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveBoard {
    private long timestamp;
    private String station;
    private Stationinfo stationinfo;
    private Departures departures;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Stationinfo getStationinfo() {
        return stationinfo;
    }

    public void setStationinfo(Stationinfo stationinfo) {
        this.stationinfo = stationinfo;
    }

    public Departures getDepartures() {
        return departures;
    }

    public void setDepartures(Departures departures) {
        this.departures = departures;
    }

    @Override
    public String toString() {
        return "LiveBoard{" +
                "timestamp=" + timestamp +
                ", station='" + station + '\'' +
                ", stationinfo=" + stationinfo +
                ", departures=" + departures +
                '}';
    }
}
