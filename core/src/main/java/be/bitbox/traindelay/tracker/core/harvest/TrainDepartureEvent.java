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
package be.bitbox.traindelay.tracker.core.harvest;

import be.bitbox.traindelay.tracker.core.station.StationId;

import java.time.LocalDateTime;

public class TrainDepartureEvent {
    private LocalDateTime eventCreationTime;
    private StationId stationId;
    private LocalDateTime expectedDepartureTime;
    private int delay;
    private boolean canceled;
    private String vehicule;
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

    public String getVehicule() {
        return vehicule;
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
        private String vehicule;
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

        public Builder withVehicule(String vehicule) {
            this.vehicule = vehicule;
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
            trainDepartureEvent.vehicule = vehicule;
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
        if (eventCreationTime != null ? !eventCreationTime.equals(that.eventCreationTime) : that.eventCreationTime != null)
            return false;
        if (stationId != null ? !stationId.equals(that.stationId) : that.stationId != null) return false;
        if (expectedDepartureTime != null ? !expectedDepartureTime.equals(that.expectedDepartureTime) : that.expectedDepartureTime != null)
            return false;
        if (vehicule != null ? !vehicule.equals(that.vehicule) : that.vehicule != null) return false;
        return platform != null ? platform.equals(that.platform) : that.platform == null;
    }

    @Override
    public int hashCode() {
        int result = eventCreationTime != null ? eventCreationTime.hashCode() : 0;
        result = 31 * result + (stationId != null ? stationId.hashCode() : 0);
        result = 31 * result + (expectedDepartureTime != null ? expectedDepartureTime.hashCode() : 0);
        result = 31 * result + delay;
        result = 31 * result + (canceled ? 1 : 0);
        result = 31 * result + (vehicule != null ? vehicule.hashCode() : 0);
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
                ", vehicule='" + vehicule + '\'' +
                ", platform='" + platform + '\'' +
                ", platformChange=" + platformChange +
                '}';
    }
}