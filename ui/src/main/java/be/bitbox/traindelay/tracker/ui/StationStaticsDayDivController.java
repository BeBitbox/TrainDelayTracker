package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.core.service.StationService;
import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import be.bitbox.traindelay.tracker.core.stationstatistic.StationStatisticService;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Component
public class StationStaticsDayDivController {
    private final StationStatisticService stationStatisticService;
    private final StationRetriever stationRetriever;

    @Autowired
    public StationStaticsDayDivController(StationStatisticService stationStatisticService, StationRetriever stationRetriever) {
        this.stationStatisticService = stationStatisticService;
        this.stationRetriever = stationRetriever;
    }

    Div asDiv() {
        var div = new Div();

        var stationComboBox = new ComboBox<Station>();
        List<Station> stations = stationRetriever.getStationsFor(Country.BE);
        stationComboBox.setItems(stations);
        stationComboBox.setItemLabelGenerator((ItemLabelGenerator<Station>) Station::name);
        stationComboBox.setId("stationComboBox");
        stationComboBox.setValue(stations.get(0));

        var datePicker = new DatePicker(LocalDate.now().minusDays(1), new Locale("nl", "be"));
        datePicker.setMin(StationService.START_DATE_SERVICE);
        datePicker.setMax(LocalDate.now());

        var grid = new Grid<Entry>();
        grid.addColumn(Entry::getLabel);
        grid.addColumn(Entry::getValue);
        grid.setWidth("33%");

        fillGrid(grid, stationComboBox.getValue().stationId(), datePicker.getValue());
        stationComboBox.addValueChangeListener(e -> fillGrid(grid, stationComboBox.getValue().stationId(), datePicker.getValue()));
        datePicker.addValueChangeListener(e -> fillGrid(grid, stationComboBox.getValue().stationId(), datePicker.getValue()));

        div.setSizeFull();
        div.add(stationComboBox, datePicker, grid);
        return div;
    }

    private void fillGrid(Grid<Entry> grid, StationId stationId, LocalDate localDate) {
        var stationStatistic = stationStatisticService.getStationStatisticFor(stationId, localDate);

        if (stationStatistic != null) {
            int departures = stationStatistic.getDepartures();
            var departuresEntry = new Entry("Number of departures", String.valueOf(departures));
            Integer delayPercentage = departures == 0 ? 0 : (int) stationStatistic.getDelays() * 100 / departures;
            var delaysEntry = new Entry("Delay percentage", delayPercentage + "%");
            var averageDelayEntry = new Entry("Average delay", stationStatistic.getAverageDelay() + " seconds");
            var cancellationsEntry = new Entry("Cancellations", stationStatistic.getCancellations() + " trains");
            var platformChangesEntry = new Entry("Platform Changes", stationStatistic.getPlatformChanges() + " times");

            grid.setItems(departuresEntry, delaysEntry, averageDelayEntry, cancellationsEntry, platformChangesEntry);
        }
    }

    private static class Entry {
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
