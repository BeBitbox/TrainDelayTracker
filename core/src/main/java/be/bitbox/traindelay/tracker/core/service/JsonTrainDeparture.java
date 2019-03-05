package be.bitbox.traindelay.tracker.core.service;

import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.core.util.JsonLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
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
    
    @JsonDeserialize(using = JsonDeserializer.class)
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
}
