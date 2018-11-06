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
package be.bitbox.traindelay.tracker.persistance.traindeparture;

import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.persistance.LocalDateTimeConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;

@DynamoDBTable(tableName = "TrainDepartureEventStore")
public class DynamoTrainDepartureEvent {
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @DynamoDBHashKey(attributeName = "id")
    private String id;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBRangeKey(attributeName = "created")
    private LocalDateTime eventCreationTime;

    @DynamoDBAttribute(attributeName = "station")
    private String stationId;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "expected")
    private LocalDateTime expectedDepartureTime;

    @DynamoDBAttribute(attributeName = "delay")
    private int delay;

    @DynamoDBAttribute(attributeName = "canceled")
    private boolean canceled;

    @DynamoDBAttribute(attributeName = "vehicule")
    private String vehicle;

    @DynamoDBAttribute(attributeName = "platform")
    private String platform;

    @DynamoDBAttribute(attributeName = "platformChange")
    private boolean platformChange;

    public DynamoTrainDepartureEvent() {}

    DynamoTrainDepartureEvent(TrainDepartureEvent event) {
        id = LocalDate.now().format(DATE_FORMATTER) + '.' + getNumberBetween1And200();
        this.eventCreationTime = event.getEventCreationTime();
        this.stationId = event.getStationId().getId();
        this.expectedDepartureTime = event.getExpectedDepartureTime();
        this.delay = event.getDelay();
        this.canceled = event.isCanceled();
        this.vehicle = event.getVehicle();
        this.platform = event.getPlatform();
        this.platformChange = event.isPlatformChange();
    }

    TrainDepartureEvent asTrainDepartureEvent() {
        return TrainDepartureEvent.Builder.createTrainDepartureEvent()
                .withEventCreationTime(eventCreationTime)
                .withStationId(aStationId(stationId))
                .withExpectedDepartureTime(expectedDepartureTime)
                .withDelay(delay)
                .withCanceled(canceled)
                .withVehicle(vehicle)
                .withPlatform(platform)
                .withPlatformChange(platformChange)
                .build();
    }

    private int getNumberBetween1And200() {
        return ThreadLocalRandom.current().nextInt(200) + 1;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getEventCreationTime() {
        return eventCreationTime;
    }

    public String getStationId() {
        return stationId;
    }

    public LocalDateTime getExpectedDepartureTime() {
        return expectedDepartureTime;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getPlatform() {
        return platform;
    }

    public boolean isPlatformChange() {
        return platformChange;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEventCreationTime(LocalDateTime eventCreationTime) {
        this.eventCreationTime = eventCreationTime;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public void setExpectedDepartureTime(LocalDateTime expectedDepartureTime) {
        this.expectedDepartureTime = expectedDepartureTime;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setPlatformChange(boolean platformChange) {
        this.platformChange = platformChange;
    }

    @Override
    public String toString() {
        return "DynamoTrainDepartureEvent{" +
                "stationId='" + stationId + '\'' +
                ", expectedDepartureTime=" + expectedDepartureTime +
                '}';
    }
}