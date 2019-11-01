package be.bitbox.traindelay.tracker.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
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
public class HomePageUI extends VerticalLayout implements LocaleChangeObserver {
    private final Map<Tab, Supplier<Div>> tabOverview = new HashMap<>();
    private final Map<Tab, Div> divs = new HashMap<>();
    private final H1 titleH1;
    private final ComboBox<LanguageOptionSpan> languagePicker;

    private final Tab overviewTab;
    private final Tab trainDeparturesTab;
    private final Tab contactTab;

    @Autowired
    public HomePageUI(HomePageDivController homePageDivController,
                      TrainDepartureDivController trainDepartureDivController,
                      ContactDivController contactDivController) {
        overviewTab = new Tab();
        overviewTab.setId("overviewTab");
        trainDeparturesTab = new Tab();
        trainDeparturesTab.setId("trainDeparturesTab");
        contactTab = new Tab();
        contactTab.setId("contactTab");

        tabOverview.put(overviewTab, homePageDivController::asDiv);
        tabOverview.put(trainDeparturesTab, trainDepartureDivController::asDiv);
        tabOverview.put(contactTab, contactDivController::asDiv);

        var tabs = new Tabs(overviewTab, trainDeparturesTab, contactTab);
        overviewTab.setSelected(true);
        tabs.addSelectedChangeListener(event -> setTabsVisibility());
        titleH1 = new H1();
        titleH1.addClassName("titleH1");
        var subTitleH3 = new H3("Independent train departure statistics");
        languagePicker = new ComboBox<>();
        languagePicker.addClassName("languagePicker");
        languagePicker.setId("languagePicker");
        languagePicker.setRenderer(new IconRenderer<>(LanguageOptionSpan::getImage, languageOption -> " " + languageOption.getLanguage()));
        languagePicker.addValueChangeListener(change -> UI.getCurrent().getSession().setLocale(change.getValue().getLocale()));

        languagePicker.setItems(LanguageOptionSpan.DUTCH, LanguageOptionSpan.FRENCH, LanguageOptionSpan.ENGLISH);

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

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        titleH1.setText(getTranslation("title"));
        overviewTab.setLabel(getTranslation("tab.overview"));
        trainDeparturesTab.setLabel(getTranslation("tab.traindepartures"));
        contactTab.setLabel(getTranslation("tab.contact"));

        languagePicker.setValue(LanguageOptionSpan.from(localeChangeEvent.getLocale()));
    }
}
