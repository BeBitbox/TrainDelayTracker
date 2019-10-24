package be.bitbox.traindelay.tracker.core.statistic;

public class DummyStatistic implements Statistic {
    private final static DummyStatistic dummyStatistic = new DummyStatistic();

    private DummyStatistic() { }

    public static DummyStatistic aDummyStatistic() {
        return dummyStatistic;
    }

    @Override
    public int getDepartures() {
        return 0;
    }

    @Override
    public int getDelays() {
        return 0;
    }

    @Override
    public int getAverageDelay() {
        return 0;
    }

    @Override
    public int getCancellations() {
        return 0;
    }

    @Override
    public int getPlatformChanges() {
        return 0;
    }
}
