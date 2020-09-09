package be.bitbox.traindelay.tracker.ui.divgenerators;

import be.bitbox.traindelay.tracker.core.statistic.StatisticService;
import be.bitbox.traindelay.tracker.ui.gridgenerators.StationStatisticGrid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class StationStaticsYearDivController extends DivGenerator {
    private final StatisticService statisticService;

    @Autowired
    public StationStaticsYearDivController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @Override
    public Div asDiv() {
        var div = new Div();
        div.setId("main_block_right");
        var titleDiv = new Div(new Span(translate("title.statistics.lastyear")));
        var stationStatistic = statisticService.getFullStationStaticFromLastYear();
        div.add(titleDiv, new StationStatisticGrid(stationStatistic));
        return div;
    }
}
