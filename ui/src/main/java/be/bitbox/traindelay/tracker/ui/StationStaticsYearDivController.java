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
public class StationStaticsYearDivController {
    private final StationStatisticService stationStatisticService;
    private final StationRetriever stationRetriever;

    @Autowired
    public StationStaticsYearDivController(StationStatisticService stationStatisticService, StationRetriever stationRetriever) {
        this.stationStatisticService = stationStatisticService;
        this.stationRetriever = stationRetriever;
    }

    Div asDiv() {
        var div = new Div();

        var stationStatistic = stationStatisticService.getFullStationStaticFromLastYear();
        div.add(new StationStatisticGrid(stationStatistic));
        return div;
    }
}
