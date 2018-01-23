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
package be.bitbox.traindelay.belgian.tracker.persistance;

import be.bitbox.traindelay.belgian.tracker.harvest.TrainDepartureEvent;
import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.time.LocalDateTime;
import java.util.UUID;

@DynamoDBTable(tableName = "TrainDepartureEvents")
public class DynamoTrainDepartureEvent {
    private final String id;
    private final LocalDateTime eventCreationTime;
    private final String stationId;
    private final LocalDateTime expectedDepartureTime;
    private final int delay;
    private final boolean canceled;
    private final String vehicule;
    private final String platform;
    private final boolean platformChange;

    DynamoTrainDepartureEvent(TrainDepartureEvent event) {
        id = UUID.randomUUID().toString();
        this.eventCreationTime = event.getEventCreationTime();
        this.stationId = event.getStationId().getId();
        this.expectedDepartureTime = event.getExpectedDepartureTime();
        this.delay = event.getDelay();
        this.canceled = event.isCanceled();
        this.vehicule = event.getVehicule();
        this.platform = event.getPlatform();
        this.platformChange = event.isPlatformChange();
    }

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "created")
    public LocalDateTime getEventCreationTime() {
        return eventCreationTime;
    }

    @DynamoDBAttribute(attributeName = "station")
    public String getStationId() {
        return stationId;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "expected")
    public LocalDateTime getExpectedDepartureTime() {
        return expectedDepartureTime;
    }

    @DynamoDBAttribute(attributeName = "delay")
    public int getDelay() {
        return delay;
    }

    @DynamoDBAttribute(attributeName = "canceled")
    public boolean isCanceled() {
        return canceled;
    }

    @DynamoDBAttribute(attributeName = "vehicule")
    public String getVehicule() {
        return vehicule;
    }

    @DynamoDBAttribute(attributeName = "platform")
    public String getPlatform() {
        return platform;
    }

    @DynamoDBAttribute(attributeName = "platformChange")
    public boolean isPlatformChange() {
        return platformChange;
    }

    static public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

        @Override
        public String convert(final LocalDateTime time) {
            return time.toString();
        }

        @Override
        public LocalDateTime unconvert(final String stringValue) {
            return LocalDateTime.parse(stringValue);
        }
    }
}