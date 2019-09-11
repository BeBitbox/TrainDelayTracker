package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.core.service.StationService;
import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import be.bitbox.traindelay.tracker.core.statistic.StatisticService;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class StationStaticsDayDivController {
    private final StatisticService statisticService;
    private final StationRetriever stationRetriever;

    @Autowired
    public StationStaticsDayDivController(StatisticService statisticService, StationRetriever stationRetriever) {
        this.statisticService = statisticService;
        this.stationRetriever = stationRetriever;
    }

    Div asDiv() {
        var div = new Div();
        div.setId("main_block_middle");

        var titleDiv = new Div(new Span("See statistics for a station"));

        var stationComboBox = new ComboBox<Station>();
        List<Station> stations = stationRetriever.getStationsFor(Country.BE);
        stationComboBox.setItems(stations);
        stationComboBox.setItemLabelGenerator((ItemLabelGenerator<Station>) Station::name);
        stationComboBox.setId("stationComboBox");
        stationComboBox.setValue(stations.get(0));

        var datePicker = new DatePicker(LocalDate.now().minusDays(1), new Locale("nl", "be"));
        datePicker.setMin(StationService.START_DATE_SERVICE);
        datePicker.setMax(LocalDate.now());

        StationStatisticGrid grid = createStationStatisticGrid(stationComboBox, datePicker);

        stationComboBox.addValueChangeListener(e -> {
            replaceOldGrid(div, stationComboBox, datePicker);
        });
        datePicker.addValueChangeListener(e -> {
            replaceOldGrid(div, stationComboBox, datePicker);
        });

        div.add(titleDiv, stationComboBox, datePicker, grid);
        return div;
    }

    private void replaceOldGrid(Div div, ComboBox<Station> stationComboBox, DatePicker datePicker) {
        div.getChildren()
                .filter(child -> child instanceof StationStatisticGrid)
                .findAny()
                .ifPresent(div::remove);
        div.add(createStationStatisticGrid(stationComboBox, datePicker));
    }

    private StationStatisticGrid createStationStatisticGrid(ComboBox<Station> stationComboBox, DatePicker datePicker) {
        var stationStatistic = statisticService.getStationStatisticFor(stationComboBox.getValue().stationId(), datePicker.getValue());
        return new StationStatisticGrid(stationStatistic);
    }
}
