package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableFunction;
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
    public HomePageUI(HomePageDivController homePageDivController, TrainDepartureDivController trainDepartureDivController) {
        var overviewTab = new Tab("Overview");
        overviewTab.setId("overviewTab");
        var trainDeparturesTab = new Tab("Train Departures");
        trainDeparturesTab.setId("trainDeparturesTab");
        tabOverview.put(overviewTab, homePageDivController::asDiv);
        tabOverview.put(trainDeparturesTab, trainDepartureDivController::asDiv);

        var tabs = new Tabs(overviewTab, trainDeparturesTab);
        overviewTab.setSelected(true);
        tabs.addSelectedChangeListener(event -> setTabsVisibility());
        var titleH1 = new H1("TrainTraffic.be");
        titleH1.addClassName("titleH1");
        var subTitleH3 = new H3("Independent train departure statistics");
        var languagePicker = new ComboBox<LanguageOption>();
        languagePicker.addClassName("languagePicker");
        languagePicker.setRenderer(new IconRenderer<>(languageOption -> languageOption));

        var optionDutch = new LanguageOption(new Image("frontend/nl_flag.png", "Nederlands"), "Nederlands");
        var optionFrench = new LanguageOption(new Image("frontend/fr_flag.png", "Français"), "Français");
        var optionEnglish = new LanguageOption(new Image("frontend/uk_flag.png", "English"), "English");
        languagePicker.setItems(optionDutch, optionFrench, optionEnglish);
        ComboBox comboBox = new ComboBox();

        Header header = new Header(titleH1, subTitleH3, languagePicker);
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

    private static class LanguageOption extends Span {
        private final Image image;
        private final String language;

        private LanguageOption(Image image, String language) {
            this.image = image;
            this.language = language;
            add(image);
            add(language);
        }

        public Image getImage() {
            return image;
        }

        public String getLanguage() {
            return language;
        }
    }
}
