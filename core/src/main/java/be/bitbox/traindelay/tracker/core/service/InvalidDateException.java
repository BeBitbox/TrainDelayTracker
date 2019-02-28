package be.bitbox.traindelay.tracker.core.service;

public class InvalidDateException extends RuntimeException {

    public InvalidDateException(String message) {
        super(message);
    }
}