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
package be.bitbox.traindelay.tracker.nmbs.response;

import be.bitbox.traindelay.tracker.core.board.Board;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static be.bitbox.traindelay.tracker.core.TrainDeparture.aTrainDeparture;
import static be.bitbox.traindelay.tracker.core.board.Board.aBoardForStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;

@Component
public class ResponseToBoardTranslator {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String VEHICLE_PREFIX = "BE.NMBS.";
    private static final String STATION_PREFIX = "BE.NMBS.00";
    private static final String UNKNOWN_PLATFORM = "?";

    public Board translateFrom(Response response) {
        ResultDetails resultDetails = response.getSvcResL()[0].getRes();

        String extStationID = resultDetails.getCommon().getLocL()[0].getExtId();
        Board board = aBoardForStation(aStationId(STATION_PREFIX + extStationID), LocalDateTime.now());

        String[] vehicleNames = Stream.of(resultDetails.getCommon().getProdL())
                .map(Vehicule::getName)
                .toArray(String[]::new);

        for (int i = 0; i < resultDetails.getJnyL().length; i++) {
            Detail detail = resultDetails.getJnyL()[i];
            StopInformation stop = detail.getStbStop();
            PlatformTranslator platformTranslator = new PlatformTranslator(stop);

            LocalDateTime scheduledTime = LocalDateTime.from(DATE_TIME_FORMATTER.parse(detail.getDate() + stop.getdTimeS()));
            int delay = getDelayFrom(detail, scheduledTime);
            boolean canceled = stop.isdCncl();
            String vehicle = VEHICLE_PREFIX + vehicleNames[i];
            String platform = platformTranslator.platform;
            boolean platformChange = platformTranslator.platformChange;
            board.addDeparture(aTrainDeparture(scheduledTime, delay, canceled, vehicle, platform, platformChange));
        }

        return board;
    }

    private int getDelayFrom(Detail detail, LocalDateTime scheduledTime) {
        int delay = 0;
        if (detail.getStbStop().getdTimeR() != null) {
            LocalDateTime expectedTime = LocalDateTime.from(DATE_TIME_FORMATTER.parse(detail.getDate() + detail.getStbStop().getdTimeR()));
            delay = (int) scheduledTime.until(expectedTime, ChronoUnit.MINUTES);
        }
        return delay;
    }

    private static class PlatformTranslator {
        String platform;
        boolean platformChange;

        PlatformTranslator(StopInformation stopInformation) {
            if (stopInformation.getdPlatfR() != null) {
                platform = stopInformation.getdPlatfR();
                platformChange = true;
            } else if (stopInformation.getdPlatfS() != null){
                platform = stopInformation.getdPlatfS();
                platformChange = false;
            } else {
                platform = UNKNOWN_PLATFORM;
                platformChange = false;
            }
        }
    }
}