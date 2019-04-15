package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.core.service.JsonTrainDeparture;
import be.bitbox.traindelay.tracker.core.service.StationService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrentTrafficDivController {
    private final StationService stationService;
    
    @Autowired
    public CurrentTrafficDivController(StationService stationService) {
        this.stationService = stationService;
    }
    
    Div asDiv() {
        var div = new Div();
        var currentTrainTraffic = stationService.listRecentTrainDepartures();
        var grid = new Grid<JsonTrainDeparture>();
        grid.addColumn(JsonTrainDeparture::getStation).setHeader("Station");
        grid.addColumn(JsonTrainDeparture::getExpectedDepartureTime).setHeader("Time");
        grid.addColumn(JsonTrainDeparture::getDelay).setHeader("Delay");
        grid.setItems(currentTrainTraffic.getTrainDepartures());
        
        div.setSizeFull();
        grid.setSizeFull();
        var currentTrafficSpan = new Span("Current train traffic is : " + currentTrainTraffic.getFuss());
        var averageDelaySpan = new Span("Average train delay : " + currentTrainTraffic.getAverageDelay());
        averageDelaySpan.setClassName("averageDelaySpan");
        var infoDiv = new Div(currentTrafficSpan, averageDelaySpan);
                
        div.add(infoDiv);
        div.add(grid);
        return div;
    }
}
