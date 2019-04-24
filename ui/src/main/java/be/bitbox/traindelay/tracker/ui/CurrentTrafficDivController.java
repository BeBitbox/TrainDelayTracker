package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.core.service.CurrentTrainTraffic;
import be.bitbox.traindelay.tracker.core.service.StationService;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
        grid.addColumn(departureVO -> departureVO.delay).setHeader("Delay");
        grid.setItems(listLastTen(currentTrainTraffic));

        div.setSizeFull();
        var currentTrafficSpan = new Span("Current train traffic is : " + currentTrainTraffic.getFuss());
        var averageDelaySpan = new Span("Average train delay : " + currentTrainTraffic.getAverageDelay());
        averageDelaySpan.setClassName("averageDelaySpan");
        var infoDiv = new Div(currentTrafficSpan, averageDelaySpan);

        div.add(infoDiv);
        div.add(grid);
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
        private final LocalDateTime expectedDepartureTime;
        private final int delay;

        private CurrentTrainDepartureVO(String station, LocalDateTime expectedDepartureTime, int delay) {
            this.station = stationRetriever.getStationById(aStationId(station)).name();
            this.expectedDepartureTime = expectedDepartureTime;
            this.delay = delay;
        }
    }

}
