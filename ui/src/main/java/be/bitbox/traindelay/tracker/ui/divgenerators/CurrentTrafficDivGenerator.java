package be.bitbox.traindelay.tracker.ui.divgenerators;

import be.bitbox.traindelay.tracker.core.service.CurrentTrainTraffic;
import be.bitbox.traindelay.tracker.core.service.StationService;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static java.util.stream.Collectors.toList;

@SpringComponent
@UIScope
public class CurrentTrafficDivGenerator extends DivGenerator {
    private final StationService stationService;
    private final StationRetriever stationRetriever;

    @Autowired
    public CurrentTrafficDivGenerator(StationService stationService, StationRetriever stationRetriever) {
        this.stationService = stationService;
        this.stationRetriever = stationRetriever;
    }

    @Override
    public Div asDiv() {
        var div = new Div();
        var locale = UI.getCurrent().getLocale();
        div.setId("main_block_left");
        var currentTrainTraffic = stationService.listRecentTrainDepartures();
        var grid = new Grid<CurrentTrainDepartureVO>();
        grid.addColumn(departureVO -> departureVO.station).setHeader(translate("general.station", locale));
        grid.addColumn(departureVO -> departureVO.expectedDepartureTime).setHeader(translate("general.time", locale));
        grid.addColumn(departureVO -> departureVO.delay).setHeader(translate("general.delay.minutes", locale));
        grid.setItems(listLastTen(currentTrainTraffic));

        var infoDiv = getCurrentTrafficDiv(currentTrainTraffic, locale);
        div.add(infoDiv);
        div.add(grid);
        return div;
    }

    private Div getCurrentTrafficDiv(CurrentTrainTraffic currentTrainTraffic, Locale locale) {
        String label;
        String imageLocation;
        switch (currentTrainTraffic.getFuss()) {
            case BUSY:
                label = translate("general.traffic.busy", locale);
                imageLocation = "frontend/red_trains.png";
                break;
            case MEDIOCRE:
                label = translate("general.traffic.normal", locale);
                imageLocation = "frontend/orange_trains.png";
                break;
            case CALM:
            default:
                label = translate("general.traffic.calm", locale);
                imageLocation = "frontend/green_train.png";
        }

        var div = new Div();
        var averageDelayText = String.format(translate("general.traffic.delay", locale), label, currentTrainTraffic.getAverageDelay());
        var fussSpan = new Span(averageDelayText);
        var image = new Image(imageLocation, label);
        div.add(image, fussSpan);
        div.setClassName("fussTrainTraffic");
        return div;
    }

    private List<CurrentTrainDepartureVO> listLastTen(CurrentTrainTraffic currentTrainTraffic) {
        var trainDepartures = currentTrainTraffic.getTrainDepartures();
        return trainDepartures
                .stream()
                .map(departure -> new CurrentTrainDepartureVO(departure.getStation(), departure.getExpectedDepartureTime(), departure.getDelay()))
                .limit(8)
                .collect(toList());
    }

    private class CurrentTrainDepartureVO {
        private final String station;
        private final LocalTime expectedDepartureTime;
        private final int delay;

        private CurrentTrainDepartureVO(String station, LocalDateTime expectedDepartureTime, int delay) {
            var stationById = stationRetriever.getStationById(aStationId(station));
            this.station = StringUtils.isEmpty(stationById.alternativeEn()) ? stationById.name() : stationById.alternativeEn();
            this.expectedDepartureTime = expectedDepartureTime.toLocalTime();
            this.delay = delay;
        }
    }

}
