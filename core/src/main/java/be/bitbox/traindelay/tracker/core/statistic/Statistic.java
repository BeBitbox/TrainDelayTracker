package be.bitbox.traindelay.tracker.core.statistic;

public interface Statistic {
    int getDepartures();

    int getDelays();

    int getAverageDelay();

    int getCancellations();

    int getPlatformChanges();
}
