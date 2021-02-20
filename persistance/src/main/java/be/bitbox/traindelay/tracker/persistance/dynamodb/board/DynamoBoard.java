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
package be.bitbox.traindelay.tracker.persistance.dynamodb.board;

import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDeparture;
import be.bitbox.traindelay.tracker.persistance.dynamodb.LocalDateTimeConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.time.LocalDateTime;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.board.Board.aBoardForStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.traindeparture.TrainDeparture.aTrainDeparture;
import static java.util.stream.Collectors.toList;

@DynamoDBTable(tableName = "BoardStore")
public class DynamoBoard {

    @DynamoDBHashKey(attributeName = "stationId")
    private String stationId;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "time")
    private LocalDateTime time;

    @DynamoDBAttribute(attributeName = "departures")
    private List<DynamoTrainDeparture> departures;

    public DynamoBoard() {
    }

    DynamoBoard(Board board) {
        this.stationId = board.getStationId().getId();
        this.time = board.getTime();
        this.departures = board.getDepartures().stream()
                .map(DynamoTrainDeparture::new)
                .collect(toList());
    }

    Board toBoard() {
        Board board = aBoardForStation(aStationId(stationId), time);
        departures.forEach(dynamoTrainDeparture -> board.addDeparture(dynamoTrainDeparture.toTrainDeparture()));
        return board;
    }

    @DynamoDBDocument
    public static class DynamoTrainDeparture {
        @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
        @DynamoDBAttribute(attributeName = "time")
        private LocalDateTime time;

        @DynamoDBAttribute(attributeName = "delay")
        private int delay;

        @DynamoDBAttribute(attributeName = "canceled")
        private boolean canceled;

        @DynamoDBAttribute(attributeName = "vehicle")
        private String vehicle;

        @DynamoDBAttribute(attributeName = "platform")
        private String platform;

        @DynamoDBAttribute(attributeName = "platformChange")
        private boolean platformChange;

        DynamoTrainDeparture(TrainDeparture trainDeparture) {
            this.time = trainDeparture.getTime();
            this.delay = trainDeparture.getDelay();
            this.canceled = trainDeparture.isCanceled();
            this.vehicle = trainDeparture.getVehicle();
            this.platform = trainDeparture.getPlatform();
            this.platformChange = trainDeparture.isPlatformChange();
        }

        public DynamoTrainDeparture() {
        }

        private TrainDeparture toTrainDeparture() {
            return aTrainDeparture(time, delay, canceled, vehicle, platform, platformChange);
        }

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }

        public int getDelay() {
            return delay;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }

        public String getVehicle() {
            return vehicle;
        }

        public void setVehicle(String vehicle) {
            this.vehicle = vehicle;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public boolean isPlatformChange() {
            return platformChange;
        }

        public void setPlatformChange(boolean platformChange) {
            this.platformChange = platformChange;
        }
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public List<DynamoTrainDeparture> getDepartures() {
        return departures;
    }

    public void setDepartures(List<DynamoTrainDeparture> departures) {
        this.departures = departures;
    }
}