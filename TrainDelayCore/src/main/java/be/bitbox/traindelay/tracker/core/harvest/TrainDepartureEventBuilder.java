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
package be.bitbox.traindelay.tracker.core.harvest;

import be.bitbox.traindelay.tracker.core.station.StationId;

import java.time.LocalDateTime;

public final class TrainDepartureEventBuilder {
    private LocalDateTime eventCreationTime;
    private StationId stationId;
    private LocalDateTime expectedDepartureTime;
    private int delay;
    private boolean canceled;
    private String vehicule;
    private String platform;
    private boolean platformChange;

    private TrainDepartureEventBuilder() {
    }

    public static TrainDepartureEventBuilder aTrainDepartureEvent() {
        return new TrainDepartureEventBuilder();
    }

    public TrainDepartureEventBuilder withEventCreationTime(LocalDateTime eventCreationTime) {
        this.eventCreationTime = eventCreationTime;
        return this;
    }

    public TrainDepartureEventBuilder withStationId(StationId stationId) {
        this.stationId = stationId;
        return this;
    }

    public TrainDepartureEventBuilder withExpectedDepartureTime(LocalDateTime expectedDepartureTime) {
        this.expectedDepartureTime = expectedDepartureTime;
        return this;
    }

    public TrainDepartureEventBuilder withDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public TrainDepartureEventBuilder withCanceled(boolean canceled) {
        this.canceled = canceled;
        return this;
    }

    public TrainDepartureEventBuilder withVehicule(String vehicule) {
        this.vehicule = vehicule;
        return this;
    }

    public TrainDepartureEventBuilder withPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public TrainDepartureEventBuilder withPlatformChange(boolean platformChange) {
        this.platformChange = platformChange;
        return this;
    }

    public TrainDepartureEvent build() {
        TrainDepartureEvent trainDepartureEvent = new TrainDepartureEvent();
        trainDepartureEvent.setEventCreationTime(eventCreationTime);
        trainDepartureEvent.setStationId(stationId);
        trainDepartureEvent.setExpectedDepartureTime(expectedDepartureTime);
        trainDepartureEvent.setDelay(delay);
        trainDepartureEvent.setCanceled(canceled);
        trainDepartureEvent.setVehicule(vehicule);
        trainDepartureEvent.setPlatform(platform);
        trainDepartureEvent.setPlatformChange(platformChange);
        return trainDepartureEvent;
    }
}