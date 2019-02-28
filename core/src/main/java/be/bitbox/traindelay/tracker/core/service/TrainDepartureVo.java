package be.bitbox.traindelay.tracker.core.service;

import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;

import java.time.LocalTime;

public class TrainDepartureVo implements Comparable {
    private final LocalTime localTime;
    private final String platform;
    private final String vehicle;
    private final int delay;

    TrainDepartureVo(TrainDepartureEvent trainDepartureEvent) {
        localTime = trainDepartureEvent.getExpectedDepartureTime().toLocalTime();
        platform = trainDepartureEvent.getPlatform();
        vehicle = trainDepartureEvent.getVehicle().replace("BE.NMBS.", "");
        delay = trainDepartureEvent.getDelay() / 60;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public String getPlatform() {
        return platform;
    }

    public String getVehicle() {
        return vehicle;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public int compareTo(Object o) {
        return localTime.compareTo(((TrainDepartureVo) o).localTime);
    }
}
