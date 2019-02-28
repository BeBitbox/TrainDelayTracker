package be.bitbox.traindelay.tracker.core.station;

public class StationNotFoundException extends RuntimeException {

    public StationNotFoundException(String message) {
        super(message);
    }
}
