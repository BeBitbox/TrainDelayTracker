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
package be.bitbox.traindelay.tracker.persistance.dynamodb.traindepartures;

import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.persistance.dynamodb.LocalDateTimeConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;

@DynamoDBTable(tableName = "DepartureEventStore")
public class DynamoDepartureEvent {
    private static final String SEPARATOR = "_";

    @DynamoDBHashKey(attributeName = "station")
    private String stationId;

    @DynamoDBRangeKey(attributeName = "expected")
    private String expectedDepartureTime;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime eventCreationTime;

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

    public DynamoDepartureEvent() {}

    DynamoDepartureEvent(TrainDepartureEvent event) {
        this.eventCreationTime = event.getEventCreationTime();
        this.stationId = event.getStationId().getId();
        this.expectedDepartureTime = event.getExpectedDepartureTime().toString() + SEPARATOR + getNumberBetween1And2000();
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
                .withExpectedDepartureTime(LocalDateTime.parse(expectedDepartureTime.split(SEPARATOR)[0]))
                .withDelay(delay)
                .withCanceled(canceled)
                .withVehicle(vehicle)
                .withPlatform(platform)
                .withPlatformChange(platformChange)
                .build();
    }

    private int getNumberBetween1And2000() {
        return ThreadLocalRandom.current().nextInt(2000) + 1;
    }

    public LocalDateTime getEventCreationTime() {
        return eventCreationTime;
    }

    public String getStationId() {
        return stationId;
    }

    public String getExpectedDepartureTime() {
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

    public void setEventCreationTime(LocalDateTime eventCreationTime) {
        this.eventCreationTime = eventCreationTime;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public void setExpectedDepartureTime(String expectedDepartureTime) {
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
        return "DynamoDepartureEvent{" +
                "stationId='" + stationId + '\'' +
                ", expectedDepartureTime=" + expectedDepartureTime +
                '}';
    }
}