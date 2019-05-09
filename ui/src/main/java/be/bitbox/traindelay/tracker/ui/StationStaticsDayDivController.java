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

    private void fillGrid(Grid grid, StationId stationId, LocalDate localDate) {
        var stationStatistic = stationStatisticService.getStationStatisticFor(stationId, localDate);

        var departuresEntry = new Entry("Number of departures", stationStatistic.getDepartures());
        var delaysEntry = new Entry("Delay percentage", stationStatistic.getDelays() / stationStatistic.getDepartures() * 100);
        var averageDelayEntry = new Entry("Average delay", stationStatistic.getAverageDelay());
        var cancellationsEntry = new Entry("Cancellations", stationStatistic.getCancellations());
        var platformChangesEntry = new Entry("Platform Changes", stationStatistic.getPlatformChanges());

        grid.setItems(departuresEntry, delaysEntry, averageDelayEntry, cancellationsEntry, platformChangesEntry);
    }

    private static class Entry {
        private final String label;
        private final Integer value;

        private Entry(String label, Integer value) {
            this.label = label;
            this.value = value;
        }

        String getLabel() {
            return label;
        }

        Integer getValue() {
            return value;
        }
    }
}
