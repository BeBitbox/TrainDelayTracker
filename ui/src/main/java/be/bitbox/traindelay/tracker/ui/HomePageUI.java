package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

@Route("")
@PWA(name = "Train traffic", shortName = "Train traffic")
@StyleSheet("style.css")
public class HomePageUI extends VerticalLayout {
    private final Map<Tab, Supplier<Div>> tabOverview = new HashMap<>();
    private final Map<Tab, Div> divs = new HashMap<>();

    @Autowired
    public HomePageUI(CurrentTrafficDivController currentTrafficDivController, TrainDepartureDivController trainDepartureDivController) {
        var overviewTab = new Tab("Overview");
        var trainDeparturesTab = new Tab("Train Departures");
        tabOverview.put(overviewTab, currentTrafficDivController::asDiv);
        tabOverview.put(trainDeparturesTab, trainDepartureDivController::asDiv);
        
        var tabs = new Tabs(overviewTab, trainDeparturesTab);
        overviewTab.setSelected(true);
        tabs.addSelectedChangeListener(event -> setTabsVisibility());
        var titleH1 = new H1("TrainTraffic.be");
        titleH1.addClassName("titleH1");
        var subTitleH3 = new H3("Independent train departure statistics");
        subTitleH3.addClassName("subTitleH3");
        Header header = new Header(titleH1, subTitleH3);
        header.addClassName("header");
        
        add(header, tabs);
        setTabsVisibility();
    }
    
    private void setTabsVisibility() {
        for (Map.Entry<Tab, Supplier<Div>> entry : tabOverview.entrySet()) {
            var tab = entry.getKey();
            if (tab.isSelected()) {
                var div = divs.get(tab);
                if (div == null) {
                    div = entry.getValue().get();
                    add(div);
                    divs.put(tab, div);
                }
                div.setVisible(true);
            } else {
                ofNullable(divs.get(tab))
                        .ifPresent(div -> div.setVisible(false));
            }
        }
    }
}
