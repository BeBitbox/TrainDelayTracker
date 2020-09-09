package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.ui.divgenerators.HomePageDivGenerator;
import be.bitbox.traindelay.tracker.ui.divgenerators.SupportDivGenerator;
import be.bitbox.traindelay.tracker.ui.divgenerators.TrainDepartureDivController;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

@Route("")
@PWA(name = "Train traffic", shortName = "Train traffic")
@StyleSheet("style.css")
public class HomePageUI extends VerticalLayout implements LocaleChangeObserver {
    private final static Logger LOGGER = LoggerFactory.getLogger(HomePageUI.class);
    private final Map<Tab, Supplier<Div>> tabOverview = new HashMap<>();
    private final Map<Tab, Div> divs = new HashMap<>();
    private final H1 titleH1;
    private final H3 subTitleH3;
    private final ComboBox<LanguageOptionSpan> languagePicker;

    private final Tab overviewTab;
    private final Tab trainDeparturesTab;
    private final Tab supportTab;

    public HomePageUI(HomePageDivGenerator homePageDivGenerator,
                      TrainDepartureDivController trainDepartureDivController,
                      SupportDivGenerator supportDivGenerator) {
        LOGGER.info("Initialize UI");
        overviewTab = new Tab();
        overviewTab.setId("overviewTab");
        trainDeparturesTab = new Tab();
        trainDeparturesTab.setId("trainDeparturesTab");
        supportTab = new Tab();
        supportTab.setId("supportTab");

        tabOverview.put(overviewTab, homePageDivGenerator::asDiv);
        tabOverview.put(trainDeparturesTab, trainDepartureDivController::asDiv);
        tabOverview.put(supportTab, supportDivGenerator::asDiv);

        var tabs = new Tabs(overviewTab, trainDeparturesTab, supportTab);
        overviewTab.setSelected(true);
        tabs.addSelectedChangeListener(event -> setTabsVisibility());
        titleH1 = new H1();
        titleH1.addClassName("titleH1");
        subTitleH3 = new H3();
        languagePicker = defineLanguagePicker();

        Header header = new Header(titleH1, subTitleH3, languagePicker);
        header.addClassName("header");

        add(header, tabs);
    }

    private ComboBox<LanguageOptionSpan> defineLanguagePicker() {
        var languagePicker = new ComboBox<LanguageOptionSpan>();
        languagePicker.addClassName("languagePicker");
        languagePicker.setId("languagePicker");
        languagePicker.setRenderer(
                new IconRenderer<>(LanguageOptionSpan::getImage, languageOption -> " " + languageOption.getLanguage())
        );
        languagePicker.addValueChangeListener(
                change -> {
                    if (change != null && change.getValue() != null) {
                        UI.getCurrent().getSession().setLocale(change.getValue().getLocale());
                    }
                }
        );

        languagePicker.setItems(LanguageOptionSpan.DUTCH, LanguageOptionSpan.FRENCH, LanguageOptionSpan.ENGLISH);
        return languagePicker;
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
        subTitleH3.setText(getTranslation("subtitle"));
        overviewTab.setLabel(getTranslation("tab.overview"));
        trainDeparturesTab.setLabel(getTranslation("tab.traindepartures"));
        supportTab.setLabel(getTranslation("tab.contact"));

        languagePicker.setValue(LanguageOptionSpan.from(localeChangeEvent.getLocale()));
        divs.clear();
        getChildren()
                .filter(child -> child instanceof Div)
                .forEach(this::remove);
        setTabsVisibility();
    }
}
