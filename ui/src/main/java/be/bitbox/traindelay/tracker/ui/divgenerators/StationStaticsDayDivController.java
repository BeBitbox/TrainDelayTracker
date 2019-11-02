package be.bitbox.traindelay.tracker.ui.divgenerators;

import be.bitbox.traindelay.tracker.core.service.StationService;
import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import be.bitbox.traindelay.tracker.core.statistic.StatisticService;
import be.bitbox.traindelay.tracker.ui.gridgenerators.StationStatisticGrid;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class StationStaticsDayDivController extends DivGenerator {
    private final StatisticService statisticService;
    private final StationRetriever stationRetriever;

    @Autowired
    public StationStaticsDayDivController(StatisticService statisticService, StationRetriever stationRetriever) {
        this.statisticService = statisticService;
        this.stationRetriever = stationRetriever;
    }

    @Override
    public Div asDiv() {
        var locale = UI.getCurrent().getLocale();
        var div = new Div();
        div.setId("main_block_middle");

        var titleDiv = new Div(new Span(translate("title.statistics.daily", locale)));

        var stationComboBox = new ComboBox<Station>();
        List<Station> stations = stationRetriever.getStationsFor(Country.BE);
        stationComboBox.setItems(stations);
        stationComboBox.setItemLabelGenerator((ItemLabelGenerator<Station>) Station::name);
        stationComboBox.setId("stationComboBoxSmallDiv");
        stationComboBox.setValue(stations.get(0));

        var datePicker = new DatePicker(LocalDate.now().minusDays(1), locale);
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
