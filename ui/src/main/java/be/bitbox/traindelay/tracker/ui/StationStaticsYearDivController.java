package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.core.statistic.StatisticService;
import com.vaadin.flow.component.html.Div;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StationStaticsYearDivController {
    private final StatisticService statisticService;

    @Autowired
    public StationStaticsYearDivController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    Div asDiv() {
        var div = new Div();

        var stationStatistic = statisticService.getFullStationStaticFromLastYear();
        div.add(new StationStatisticGrid(stationStatistic));
        return div;
    }
}
