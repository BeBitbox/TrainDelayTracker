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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static be.bitbox.traindelay.tracker.core.TrainDeparture.aTrainDeparture;
import static be.bitbox.traindelay.tracker.core.board.Board.aBoardForStation;
import static be.bitbox.traindelay.tracker.core.board.BoardNotFoundException.aBoardNotFoundException;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;

public enum ResponseToBoardTranslator {
    INSTANCE;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String VEHICLE_PREFIX = "BE.NMBS.";
    private static final String STATION_PREFIX = "BE.NMBS.00";
    private static final String UNKNOWN_PLATFORM = "?";
    private static final ZoneId PARIS_CENTRAL_TIME = ZoneId.of("Europe/Paris");

    public Board translateFrom(Response response) {
        ResultDetails resultDetails = response.getSvcResL()[0].getRes();

        if (resultDetails.getCommon().getLocL().length == 0) {
            throw aBoardNotFoundException("Board not found");
        }
        String extStationID = resultDetails.getCommon().getLocL()[0].getExtId();
        Board board = aBoardForStation(aStationId(STATION_PREFIX + extStationID), LocalDateTime.now(PARIS_CENTRAL_TIME));

        String[] vehicleNames = Stream.of(resultDetails.getCommon().getProdL())
                .map(vehicule -> vehicule.getName().replaceAll(" ", ""))
                .toArray(String[]::new);

        for (Detail detail : resultDetails.getJnyL()) {
            StopInformation stop = detail.getStbStop();
            PlatformTranslator platformTranslator = new PlatformTranslator(stop);

            LocalDateTime scheduledTime = getScheduledTime(detail, stop);
            int delay = getDelayFrom(detail, scheduledTime);
            boolean canceled = stop.isdCncl();
            String vehicle = VEHICLE_PREFIX + vehicleNames[stop.getdProdX()];
            String platform = platformTranslator.platform;
            boolean platformChange = platformTranslator.platformChange;
            board.addDeparture(aTrainDeparture(scheduledTime, delay, canceled, vehicle, platform, platformChange));
        }

        return board;
    }

    private LocalDateTime getScheduledTime(Detail detail, StopInformation stop) {
        LocalDateTime scheduledTime;
        if (stop.getdTimeS().length() > 6) {
            scheduledTime = getLocalDateTimeInFuture(detail, stop.getdTimeS());
        } else {
            scheduledTime = LocalDateTime.from(DATE_TIME_FORMATTER.parse(detail.getDate() + stop.getdTimeS()));
        }
        return scheduledTime;
    }

    private LocalDateTime getLocalDateTimeInFuture(Detail detail, String time) {
        LocalDateTime oldDate = LocalDateTime.from(DATE_TIME_FORMATTER.parse(detail.getDate() + time.substring(2)));
        long extraDays = Long.parseLong(time.substring(0, 2));
        return oldDate.plusDays(extraDays);
    }

    private int getDelayFrom(Detail detail, LocalDateTime scheduledTime) {
        int delay = 0;
        if (detail.getStbStop().getdTimeR() != null) {
            LocalDateTime expectedTime;
            if (detail.getStbStop().getdTimeR().length() > 6) {
                expectedTime = getLocalDateTimeInFuture(detail, detail.getStbStop().getdTimeR());
            } else {
                expectedTime = LocalDateTime.from(DATE_TIME_FORMATTER.parse(detail.getDate() + detail.getStbStop().getdTimeR()));
            }
            delay = (int) scheduledTime.until(expectedTime, ChronoUnit.SECONDS);
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
            } else if (stopInformation.getdPlatfS() != null) {
                platform = stopInformation.getdPlatfS();
                platformChange = false;
            } else {
                platform = UNKNOWN_PLATFORM;
                platformChange = false;
            }
        }
    }
}