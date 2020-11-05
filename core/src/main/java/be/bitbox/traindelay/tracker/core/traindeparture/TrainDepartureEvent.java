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
package be.bitbox.traindelay.tracker.core.traindeparture;

import be.bitbox.traindelay.tracker.core.station.StationId;

import java.time.LocalDateTime;
import java.util.Objects;

public class TrainDepartureEvent {
    private LocalDateTime eventCreationTime;
    private StationId stationId;
    private LocalDateTime expectedDepartureTime;
    private int delay;
    private boolean canceled;
    private String vehicle;
    private String platform;
    private boolean platformChange;

    private TrainDepartureEvent() { }

    public LocalDateTime getEventCreationTime() {
        return eventCreationTime;
    }

    public StationId getStationId() {
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

    public static class Builder {
        private LocalDateTime eventCreationTime;
        private StationId stationId;
        private LocalDateTime expectedDepartureTime;
        private int delay;
        private boolean canceled;
        private String vehicle;
        private String platform;
        private boolean platformChange;

        private Builder() {
        }

        public static Builder createTrainDepartureEvent() {
            return new Builder();
        }

        public Builder withEventCreationTime(LocalDateTime eventCreationTime) {
            this.eventCreationTime = eventCreationTime;
            return this;
        }

        public Builder withStationId(StationId stationId) {
            this.stationId = stationId;
            return this;
        }

        public Builder withExpectedDepartureTime(LocalDateTime expectedDepartureTime) {
            this.expectedDepartureTime = expectedDepartureTime;
            return this;
        }

        public Builder withDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public Builder withCanceled(boolean canceled) {
            this.canceled = canceled;
            return this;
        }

        public Builder withVehicle(String vehicle) {
            this.vehicle = vehicle;
            return this;
        }

        public Builder withPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        public Builder withPlatformChange(boolean platformChange) {
            this.platformChange = platformChange;
            return this;
        }

        public TrainDepartureEvent build() {
            TrainDepartureEvent trainDepartureEvent = new TrainDepartureEvent();
            trainDepartureEvent.eventCreationTime = eventCreationTime;
            trainDepartureEvent.stationId = stationId;
            trainDepartureEvent.expectedDepartureTime = expectedDepartureTime;
            trainDepartureEvent.delay = delay;
            trainDepartureEvent.canceled = canceled;
            trainDepartureEvent.vehicle = vehicle;
            trainDepartureEvent.platform = platform;
            trainDepartureEvent.platformChange = platformChange;
            return trainDepartureEvent;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainDepartureEvent that = (TrainDepartureEvent) o;

        if (delay != that.delay) return false;
        if (canceled != that.canceled) return false;
        if (platformChange != that.platformChange) return false;
        if (!Objects.equals(eventCreationTime, that.eventCreationTime))
            return false;
        if (!Objects.equals(stationId, that.stationId)) return false;
        if (!Objects.equals(expectedDepartureTime, that.expectedDepartureTime))
            return false;
        if (!Objects.equals(vehicle, that.vehicle)) return false;
        return Objects.equals(platform, that.platform);
    }

    @Override
    public int hashCode() {
        int result = eventCreationTime != null ? eventCreationTime.hashCode() : 0;
        result = 31 * result + (stationId != null ? stationId.hashCode() : 0);
        result = 31 * result + (expectedDepartureTime != null ? expectedDepartureTime.hashCode() : 0);
        result = 31 * result + delay;
        result = 31 * result + (canceled ? 1 : 0);
        result = 31 * result + (vehicle != null ? vehicle.hashCode() : 0);
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        result = 31 * result + (platformChange ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TrainDepartureEvent{" +
                "eventCreationTime=" + eventCreationTime +
                ", stationId=" + stationId +
                ", expectedDepartureTime=" + expectedDepartureTime +
                ", delay=" + delay +
                ", canceled=" + canceled +
                ", vehicle='" + vehicle + '\'' +
                ", platform='" + platform + '\'' +
                ", platformChange=" + platformChange +
                '}';
    }
}