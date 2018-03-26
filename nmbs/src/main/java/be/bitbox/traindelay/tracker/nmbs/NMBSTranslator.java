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

import be.bitbox.traindelay.tracker.core.TrainDeparture;
import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.station.StationId;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static be.bitbox.traindelay.tracker.core.board.BoardTranslationException.aBoardTranslationException;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;

enum NMBSTranslator {

    INSTANCE;

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");
    private static final String UNKNOWN_VEHICULE = "UNKNOWN";
    private static final String UNKNOWN_PLATFORM = "?";

    Board translateFrom(LiveBoard liveBoard) {
        if (liveBoard == null) {
            throw aBoardTranslationException("The liveboard can not be null!" );
        }
        StationId stationId = retrieveStationId(liveBoard.getStationinfo());
        LocalDateTime localDateTime = translateToDateFrom(liveBoard.getTimestamp());
        Board board = Board.aBoardForStation(stationId, localDateTime);
        addDeparturesIfPresent(board, liveBoard.getDepartures());
        return board;
    }

    private void addDeparturesIfPresent(Board board, Departures departures) {
        if (departures == null || departures.getDeparture() ==  null) {
            return;
        }
        departures.getDeparture().forEach(departure -> board.addDeparture(translateFrom(departure)));
    }

    private TrainDeparture translateFrom(Departure departure) {
        LocalDateTime time = translateToDateFrom(departure.getTime());
        boolean canceled = departure.getCanceled() != 0;
        String vehicule = getVehiculeFrom(departure.getVehicle());
        Platforminfo platforminfo = departure.getPlatforminfo();
        return TrainDeparture.aTrainDeparture(time, departure.getDelay(), canceled, vehicule, getPlatform(platforminfo), getPlatformChanged(platforminfo));
    }

    private String getVehiculeFrom(String vehicule) {
        if (StringUtils.isEmpty(vehicule)) {
            return UNKNOWN_VEHICULE;
        }
        return vehicule;
    }

    private String getPlatform(Platforminfo platforminfo) {
        if (platforminfo == null || StringUtils.isEmpty(platforminfo.getName())) {
            return UNKNOWN_PLATFORM;
        }
        return platforminfo.getName();
    }

    private boolean getPlatformChanged(Platforminfo platforminfo) {
        if (platforminfo == null) {
            return true;
        }
        return platforminfo.getNormal() != 1;
    }

    private StationId retrieveStationId(Stationinfo stationinfo) {
        if (stationinfo == null || StringUtils.isEmpty(stationinfo.getId())) {
            throw aBoardTranslationException("The stationInfo is missing a necessary ID : " + stationinfo);
        }

        return aStationId(stationinfo.getId());
    }

    private LocalDateTime translateToDateFrom(long timestamp) {
        return Instant.ofEpochMilli(timestamp * 1000).atZone(ZONE_ID).toLocalDateTime();
    }
}
