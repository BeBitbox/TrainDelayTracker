package be.bitbox.traindelay.tracker.core.service;

import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.core.util.JsonLocalDateTimeDeserializer;
import be.bitbox.traindelay.tracker.core.util.JsonLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public class JsonTrainDeparture implements Comparable<JsonTrainDeparture> {
    private String station;
    private LocalDateTime expectedDepartureTime;
    private int delay;
    private boolean canceled;
    private String vehicle;
    private String platform;
    private boolean platformChange;

    public JsonTrainDeparture() { }

    public JsonTrainDeparture(TrainDepartureEvent trainDepartureEvent) {
        station = trainDepartureEvent.getStationId().getId();
        expectedDepartureTime = trainDepartureEvent.getExpectedDepartureTime();
        delay = trainDepartureEvent.getDelay() / 60;
        canceled = trainDepartureEvent.isCanceled();
        vehicle = trainDepartureEvent.getVehicle().replace("BE.NMBS.", "");
        platform = trainDepartureEvent.getPlatform();
        platformChange = trainDepartureEvent.isPlatformChange();
    }

    public String getStation() {
        return station;
    }

    @JsonSerialize(using = JsonLocalDateTimeSerializer.class)
    public LocalDateTime getExpectedDepartureTime() {
        return expectedDepartureTime;
    }
    
    @JsonDeserialize(using = JsonLocalDateTimeDeserializer.class)
    public void setExpectedDepartureTime(LocalDateTime expectedDepartureTime) {
        this.expectedDepartureTime = expectedDepartureTime;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setStation(String station) {
        this.station = station;
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

    public String getVehicle() {
        return vehicle;
    }

    public String getPlatform() {
        return platform;
    }

    public boolean isPlatformChange() {
        return platformChange;
    }
    
    @Override
    public int compareTo(JsonTrainDeparture other) {
        return expectedDepartureTime.compareTo(other.expectedDepartureTime);
    }

    public static final class Builder {
        private String station;
        private LocalDateTime expectedDepartureTime;
        private int delay;
        private boolean canceled;
        private String vehicle;
        private String platform;
        private boolean platformChange;

        private Builder() {
        }

        public static Builder aJsonTrainDeparture() {
            return new Builder();
        }

        public Builder withStation(String station) {
            this.station = station;
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

        public JsonTrainDeparture build() {
            JsonTrainDeparture jsonTrainDeparture = new JsonTrainDeparture();
            jsonTrainDeparture.setStation(station);
            jsonTrainDeparture.setExpectedDepartureTime(expectedDepartureTime);
            jsonTrainDeparture.setDelay(delay);
            jsonTrainDeparture.setCanceled(canceled);
            jsonTrainDeparture.setVehicle(vehicle);
            jsonTrainDeparture.setPlatform(platform);
            jsonTrainDeparture.setPlatformChange(platformChange);
            return jsonTrainDeparture;
        }
    }
}
