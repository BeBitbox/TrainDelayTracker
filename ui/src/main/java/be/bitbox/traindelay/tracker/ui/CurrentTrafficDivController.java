package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.core.service.CurrentTrainTraffic;
import be.bitbox.traindelay.tracker.core.service.StationService;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static java.util.stream.Collectors.toList;

@Component
public class CurrentTrafficDivController {
    private final StationService stationService;
    private final StationRetriever stationRetriever;

    @Autowired
    public CurrentTrafficDivController(StationService stationService, StationRetriever stationRetriever) {
        this.stationService = stationService;
        this.stationRetriever = stationRetriever;
    }

    Div asDiv() {
        var div = new Div();
        var currentTrainTraffic = stationService.listRecentTrainDepartures();
        var grid = new Grid<CurrentTrainDepartureVO>();
        grid.addColumn(departureVO -> departureVO.station).setHeader("Station");
        grid.addColumn(departureVO -> departureVO.expectedDepartureTime).setHeader("Time");
        grid.addColumn(departureVO -> departureVO.delay).setHeader("Delay (minutes)");
        grid.setItems(listLastTen(currentTrainTraffic));
        grid.setWidth("33%");

        div.setSizeFull();

        var infoDiv = getCurrentTrafficDiv(currentTrainTraffic);
        div.add(infoDiv);
        div.add(grid);
        return div;
    }

    private Div getCurrentTrafficDiv(CurrentTrainTraffic currentTrainTraffic) {
        String label;
        String imageLocation;
        switch (currentTrainTraffic.getFuss()) {
            case BUSY:
                label = "Busy train traffic";
                imageLocation = "frontend/red_trains.png";
                break;
            case MEDIOCRE:
                label = "Normal train traffic";
                imageLocation = "frontend/orange_trains.png";
                break;
            case CALM:
            default:
                label = "Calm train traffic";
                imageLocation = "frontend/green_train.png";
        }

        var div = new Div();
        var averageDelayText = ", the average train delay is " + currentTrainTraffic.getAverageDelay() + " minutes";
        var fussSpan = new Span(label + averageDelayText);
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
                .limit(10)
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
