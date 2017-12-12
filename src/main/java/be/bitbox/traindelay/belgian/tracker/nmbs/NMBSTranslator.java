/*
 * Copyright 2017 Bitbox : BelgianTrainDelayTracker
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

import be.bitbox.traindelay.belgian.tracker.Board;
import be.bitbox.traindelay.belgian.tracker.BoardTranslationException;
import be.bitbox.traindelay.belgian.tracker.station.StationId;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

import static be.bitbox.traindelay.belgian.tracker.Board.aBoardForStation;
import static be.bitbox.traindelay.belgian.tracker.station.StationId.aStationId;
import static org.springframework.util.StringUtils.isEmpty;

public class NMBSTranslator {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");

    Board translateFrom(LiveBoard liveBoard) {
        if (liveBoard == null) {
            throw new BoardTranslationException("The liveboard can not be null!" );
        }
        StationId stationId = retrieveStationId(liveBoard.getStationinfo());
        LocalDateTime localDateTime = Instant.ofEpochMilli(liveBoard.getTimestamp() * 1000).atZone(ZONE_ID).toLocalDateTime();
        return aBoardForStation(stationId, localDateTime);
    }

    private StationId retrieveStationId(Stationinfo stationinfo) {
        if (stationinfo == null || isEmpty(stationinfo.getId())) {
            throw new BoardTranslationException("The stationInfo is missing a necessary ID : " + stationinfo);
        }

        return aStationId(stationinfo.getId());
    }

}
