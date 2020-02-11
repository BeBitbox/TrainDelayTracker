package be.bitbox.traindelay.tracker.core.statistic;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import be.bitbox.traindelay.tracker.core.station.StationId;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

public class YearlyStationStatistic {
    private final List<Entry> top5Popular;
    private final List<Entry> bottom5Popular;
    private final List<Entry> top5Delays;
    private final List<Entry> bottom5Delays;
    private final List<Entry> top5Cancellations;
    private final List<Entry> bottom5Cancellations;
    private final List<Entry> top5AverageDelay;
    private final List<Entry> bottom5AverageDelay;
    private final List<StationId> noDepartures;

    private YearlyStationStatistic(List<StationStatistic> stationStatisticList) {
        noDepartures = stationStatisticList.stream()
                .filter(not(hasDepartures()))
                .map(StationStatistic::getStationId)
                .collect(toList());
        var statistics = stationStatisticList.stream()
                .filter(hasDepartures())
                .collect(toList());
        top5Popular = statistics.stream()
                .map(statistic -> new Entry(statistic.getStationId(), statistic.getDepartures()))
                .sorted(Comparator.comparingInt(Entry::getNumber).reversed())
                .limit(5)
                .collect(toList());
        bottom5Popular = statistics.stream()
                .map(statistic -> new Entry(statistic.getStationId(), statistic.getDepartures()))
                .sorted(Comparator.comparingInt(Entry::getNumber))
                .limit(5)
                .collect(toList());
        top5Delays = statistics.stream()
                .map(statistic -> new Entry(statistic.getStationId(), statistic.getDelays()))
                .sorted(Comparator.comparingInt(Entry::getNumber).reversed())
                .limit(5)
                .collect(toList());
        bottom5Delays = statistics.stream()
                .map(statistic -> new Entry(statistic.getStationId(), statistic.getDelays()))
                .sorted(Comparator.comparingInt(Entry::getNumber))
                .limit(5)
                .collect(toList());
        top5Cancellations = statistics.stream()
                .map(statistic -> new Entry(statistic.getStationId(), statistic.getCancellations()))
                .sorted(Comparator.comparingInt(Entry::getNumber).reversed())
                .limit(5)
                .collect(toList());
        bottom5Cancellations = statistics.stream()
                .map(statistic -> new Entry(statistic.getStationId(), statistic.getCancellations()))
                .sorted(Comparator.comparingInt(Entry::getNumber))
                .limit(5)
                .collect(toList());
        top5AverageDelay = statistics.stream()
                .map(statistic -> new Entry(statistic.getStationId(), statistic.getAverageDelay()))
                .sorted(Comparator.comparingInt(Entry::getNumber).reversed())
                .limit(5)
                .collect(toList());
        bottom5AverageDelay = statistics.stream()
                .map(statistic -> new Entry(statistic.getStationId(), statistic.getAverageDelay()))
                .sorted(Comparator.comparingInt(Entry::getNumber))
                .limit(5)
                .collect(toList());
    }

    private Predicate<StationStatistic> hasDepartures() {
        return statistic -> statistic.getDepartures() > 0;
    }

    public static YearlyStationStatistic yearlyStationStatisticFrom(List<StationStatistic> stationStatisticList) {
        checkNotNull(stationStatisticList, "you need to provide a list in order to calculate the statistics");
        return new YearlyStationStatistic(stationStatisticList);
    }

    public List<Entry> getBottom5Popular() {
        return bottom5Popular;
    }

    public List<Entry> getTop5Popular() {
        return top5Popular;
    }

    public List<Entry> getBottom5Cancellations() {
        return bottom5Cancellations;
    }

    public List<Entry> getTop5Delays() {
        return top5Delays;
    }

    public List<StationId> getNoDepartures() {
        return noDepartures;
    }

    public List<Entry> getBottom5Delays() {
        return bottom5Delays;
    }

    public List<Entry> getTop5Cancellations() {
        return top5Cancellations;
    }

    public List<Entry> getTop5AverageDelay() {
        return top5AverageDelay;
    }

    public List<Entry> getBottom5AverageDelay() {
        return bottom5AverageDelay;
    }

    public static class Entry {
        private final StationId stationId;
        private final Integer number;

        private Entry(StationId stationId, Integer number) {
            this.stationId = stationId;
            this.number = number;
        }

        public StationId getStationId() {
            return stationId;
        }

        public Integer getNumber() {
            return number;
        }
    }
}
