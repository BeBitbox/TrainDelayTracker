package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.component.grid.Grid;

import be.bitbox.traindelay.tracker.core.statistic.Statistic;
import static be.bitbox.traindelay.tracker.core.statistic.DummyStatistic.aDummyStatistic;

class StationStatisticGrid extends Grid<StationStatisticGrid.Entry> {

    StationStatisticGrid(Statistic statistic) {
        super();
        addColumn(Entry::getLabel);
        addColumn(Entry::getValue);

        Statistic existingStatistic = statistic == null ? aDummyStatistic() : statistic;
        int departures = existingStatistic.getDepartures();
        var departuresEntry = new Entry("Number of departures", String.format("%,d", departures));
        int delayPercentage = departures == 0 ? 0 : existingStatistic.getDelays() * 100 / departures;
        var delaysEntry = new Entry("Delay percentage", delayPercentage + "%");
        var averageDelayEntry = new Entry("Average delay", existingStatistic.getAverageDelay() + " seconds");
        var cancellationsEntry = new Entry("Cancellations", String.format("%,d trains", existingStatistic.getCancellations()));
        var platformChangesEntry = new Entry("Platform Changes", String.format("%,d times", existingStatistic.getPlatformChanges()));

        setItems(departuresEntry, delaysEntry, averageDelayEntry, cancellationsEntry, platformChangesEntry);
    }

    static class Entry {
        private final String label;
        private final String value;

        private Entry(String label, String value) {
            this.label = label;
            this.value = value;
        }

        String getLabel() {
            return label;
        }

        String getValue() {
            return value;
        }
    }

}
