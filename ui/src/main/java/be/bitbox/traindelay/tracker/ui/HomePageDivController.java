package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.component.html.Div;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HomePageDivController {
    private final CurrentTrafficDivController currentTrafficDivController;
    private final StationStaticsDayDivController stationStaticsDayDivController;

    @Autowired
    public HomePageDivController(CurrentTrafficDivController currentTrafficDivController, StationStaticsDayDivController stationStaticsDayDivController) {
        this.currentTrafficDivController = currentTrafficDivController;
        this.stationStaticsDayDivController = stationStaticsDayDivController;
    }

    Div asDiv() {
        var div = new Div();
        div.setSizeFull();

        div.add(currentTrafficDivController.asDiv(), stationStaticsDayDivController.asDiv());
        return div;
    }
}
