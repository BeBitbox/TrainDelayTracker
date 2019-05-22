package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.core.stationstatistic.StationStatistic;
import com.vaadin.flow.component.grid.Grid;

class StationStatisticGrid extends Grid<StationStatisticGrid.Entry> {

    StationStatisticGrid(StationStatistic stationStatistic) {
        super();
        addColumn(Entry::getLabel);
        addColumn(Entry::getValue);
        setWidth("33%");

        if (stationStatistic != null) {
            int departures = stationStatistic.getDepartures();
            var departuresEntry = new Entry("Number of departures", String.valueOf(departures));
            Integer delayPercentage = departures == 0 ? 0 : (int) stationStatistic.getDelays() * 100 / departures;
            var delaysEntry = new Entry("Delay percentage", delayPercentage + "%");
            var averageDelayEntry = new Entry("Average delay", stationStatistic.getAverageDelay() + " seconds");
            var cancellationsEntry = new Entry("Cancellations", stationStatistic.getCancellations() + " trains");
            var platformChangesEntry = new Entry("Platform Changes", stationStatistic.getPlatformChanges() + " times");

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
