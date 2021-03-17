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
package be.bitbox.traindelay.tracker.core.service;

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Component
public class StationAvailabilityMonitor {

    private final Map<Station, Integer> stationFailList;

    @Autowired
    public StationAvailabilityMonitor(StationRetriever stationRetriever) {
        stationFailList = new HashMap<>();
        stationRetriever.getStationsFor(Country.BE)
                .forEach(station -> stationFailList.put(station, 0));
    }

    public void positiveFeedbackFor(Station station) {
        stationFailList.put(station, 0);
    }

    public void negativeFeedbackFor(Station station) {
        stationFailList.put(station, 5);
    }

    public List<Station> getTrainStations() {
        reduceAllFailedDepartures();

        return stationFailList.entrySet().stream()
                .filter(stationIntegerEntry -> stationIntegerEntry.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(toList());
    }

    private void reduceAllFailedDepartures() {
        Set<Map.Entry<Station, Integer>> entries = stationFailList.entrySet();
        for (Map.Entry<Station, Integer> entry : entries) {
            if (entry.getValue() > 0) {
                stationFailList.put(entry.getKey(), entry.getValue() - 1);
            }
        }
    }
}