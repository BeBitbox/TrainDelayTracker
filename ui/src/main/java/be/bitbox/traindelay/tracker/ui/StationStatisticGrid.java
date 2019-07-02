package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.core.statistic.Statistic;
import com.vaadin.flow.component.grid.Grid;

class StationStatisticGrid extends Grid<StationStatisticGrid.Entry> {

    StationStatisticGrid(Statistic statistic) {
        super();
        addColumn(Entry::getLabel);
        addColumn(Entry::getValue);

        if (statistic != null) {
            int departures = statistic.getDepartures();
            var departuresEntry = new Entry("Number of departures", String.format("%,d", departures));
            int delayPercentage = departures == 0 ? 0 : statistic.getDelays() * 100 / departures;
            var delaysEntry = new Entry("Delay percentage", delayPercentage + "%");
            var averageDelayEntry = new Entry("Average delay", statistic.getAverageDelay() + " seconds");
            var cancellationsEntry = new Entry("Cancellations", String.format("%,d trains", statistic.getCancellations()));
            var platformChangesEntry = new Entry("Platform Changes", String.format("%,d times", statistic.getPlatformChanges()));

            setItems(departuresEntry, delaysEntry, averageDelayEntry, cancellationsEntry, platformChangesEntry);
        }
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
