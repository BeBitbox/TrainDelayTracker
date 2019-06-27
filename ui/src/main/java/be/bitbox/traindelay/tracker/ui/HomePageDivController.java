package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.component.html.Div;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HomePageDivController {
    private final CurrentTrafficDivController currentTrafficDivController;
    private final StationStaticsDayDivController stationStaticsDayDivController;
    private final StationStaticsYearDivController stationStaticsYearDivController;

    @Autowired
    public HomePageDivController(CurrentTrafficDivController currentTrafficDivController,
                                 StationStaticsDayDivController stationStaticsDayDivController,
                                 StationStaticsYearDivController stationStaticsYearDivController) {
        this.currentTrafficDivController = currentTrafficDivController;
        this.stationStaticsDayDivController = stationStaticsDayDivController;
        this.stationStaticsYearDivController = stationStaticsYearDivController;
    }

    Div asDiv() {
        var div = new Div();
        div.setSizeFull();

        Div clearDiv = new Div();
        clearDiv.setClassName("clearBoth");
        div.add(currentTrafficDivController.asDiv(), stationStaticsDayDivController.asDiv(), stationStaticsYearDivController.asDiv(), clearDiv);
        return div;
    }
}
