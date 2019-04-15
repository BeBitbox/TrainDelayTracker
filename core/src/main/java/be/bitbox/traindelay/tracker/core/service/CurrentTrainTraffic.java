package be.bitbox.traindelay.tracker.core.service;

import java.time.LocalDateTime;
import java.util.List;

public class CurrentTrainTraffic {
    private final List<JsonTrainDeparture> trainDepartures;
    private final Fuss fuss;
    private final int averageDelay;
    
    CurrentTrainTraffic(List<JsonTrainDeparture> trainDepartures) {
        this.trainDepartures = trainDepartures;
        this.fuss = determineFussiness();
        this.averageDelay = determineAverageDelay();
    }

    private int determineAverageDelay() {
        if (trainDepartures.isEmpty()) {
            return 0;
        }
        int sumOfDelays = trainDepartures.stream()
                .mapToInt(JsonTrainDeparture::getDelay)
                .sum();
        return sumOfDelays / trainDepartures.size();
    }

    private Fuss determineFussiness() {
        var optionalTrainDeparture = trainDepartures.stream()
                .min(JsonTrainDeparture::compareTo);
        if (optionalTrainDeparture.isEmpty()) {
            return Fuss.CALM;
        }
        var oldestTrainDeparture = optionalTrainDeparture.get();
        if (LocalDateTime.now().minusHours(2).isAfter(oldestTrainDeparture.getExpectedDepartureTime())) {
            return Fuss.CALM;
        } else if (LocalDateTime.now().minusHours(1).isBefore(oldestTrainDeparture.getExpectedDepartureTime())) {
            return Fuss.BUSY;
        } else {
            return Fuss.MEDIOCRE;
        }        
    }

    public enum Fuss {
        CALM, MEDIOCRE, BUSY;
    }

    public List<JsonTrainDeparture> getTrainDepartures() {
        return trainDepartures;
    }

    public Fuss getFuss() {
        return fuss;
    }

    public int getAverageDelay() {
        return averageDelay;
    }
}
