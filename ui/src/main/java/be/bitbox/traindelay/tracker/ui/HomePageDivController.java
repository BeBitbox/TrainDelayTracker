package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.ui.divgenerators.CurrentTrafficDivGenerator;
import com.vaadin.flow.component.html.Div;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HomePageDivController {
    private final CurrentTrafficDivGenerator currentTrafficDivGenerator;
    private final StationStaticsDayDivController stationStaticsDayDivController;
    private final StationStaticsYearDivController stationStaticsYearDivController;

    @Autowired
    public HomePageDivController(CurrentTrafficDivGenerator currentTrafficDivGenerator,
                                 StationStaticsDayDivController stationStaticsDayDivController,
                                 StationStaticsYearDivController stationStaticsYearDivController) {
        this.currentTrafficDivGenerator = currentTrafficDivGenerator;
        this.stationStaticsDayDivController = stationStaticsDayDivController;
        this.stationStaticsYearDivController = stationStaticsYearDivController;
    }

    Div asDiv() {
        var div = new Div();
        div.setSizeFull();

        Div clearDiv = new Div();
        clearDiv.setClassName("clearBoth");
        div.add(currentTrafficDivGenerator.asDiv(), stationStaticsDayDivController.asDiv(), stationStaticsYearDivController.asDiv(), clearDiv);
        return div;
    }
}
