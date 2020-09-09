/*
 * Copyright 2018 Bitbox : TrainDelayTracker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.bitbox.traindelay.tracker.ui.divgenerators;

import be.bitbox.traindelay.tracker.core.service.JsonTrainDeparture;
import be.bitbox.traindelay.tracker.core.service.StationService;
import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.TreeSet;

import static java.util.stream.Collectors.toCollection;

@SpringComponent
@UIScope
public class TrainDepartureDivController extends DivGenerator {
    private final StationService stationService;
    private final StationRetriever stationRetriever;

    @Autowired
    public TrainDepartureDivController(StationRetriever stationRetriever, StationService stationService) {
        this.stationService = stationService;
        this.stationRetriever = stationRetriever;
    }

    @Override
    public Div asDiv() {
        var locale = UI.getCurrent().getLocale();
        var div = new Div();
        var label = new Label(translate("title.departures", locale));
        var stationComboBox = new ComboBox<Station>();
        stationComboBox.setItems(stationRetriever.getStationsFor(Country.BE));
        stationComboBox.setItemLabelGenerator((ItemLabelGenerator<Station>) Station::name);
        stationComboBox.setWidth("250px");
        stationComboBox.setId("stationComboBoxPage");
        label.setFor(stationComboBox);
        var datePicker = new DatePicker(LocalDate.now().minusDays(1), locale);
        datePicker.setId("datePickerPage");
        datePicker.setMin(StationService.START_DATE_SERVICE);
        datePicker.setMax(LocalDate.now());
        Grid<TrainDepartureVo> grid = new Grid<>();
        grid.addColumn(TrainDepartureVo::getLocalTime).setHeader(translate("general.time", locale));
        grid.addColumn(TrainDepartureVo::getPlatform).setHeader(translate("general.platform", locale));
        grid.addColumn(TrainDepartureVo::getVehicle).setHeader(translate("general.vehicle", locale));
        grid.addColumn(TrainDepartureVo::getDelay).setHeader(translate("general.delay.minutes", locale));

        HorizontalLayout header = new HorizontalLayout(label, stationComboBox, datePicker);
        div.add(header, grid);
        div.setSizeFull();

        stationComboBox.addValueChangeListener(e -> grid.setItems(listTrainDepartures(e.getValue(), datePicker.getValue())));
        datePicker.addValueChangeListener(e -> listTrainDepartures(stationComboBox.getValue(), e.getValue()));
        return div;
    }

    private TreeSet<TrainDepartureVo> listTrainDepartures(Station station, LocalDate date) {
        if (station == null || date == null) {
            return null;
        }
        return stationService.listTrainDeparturesFor(station.stationId(), date)
                .stream()
                .map(TrainDepartureVo::new)
                .collect(toCollection(TreeSet::new));
    }

    private static class TrainDepartureVo implements Comparable<TrainDepartureVo> {
        private final LocalTime localTime;
        private final String platform;
        private final String vehicle;
        private final int delay;

        TrainDepartureVo(JsonTrainDeparture jsonTrainDeparture) {
            localTime = jsonTrainDeparture.getExpectedDepartureTime().toLocalTime();
            platform = jsonTrainDeparture.getPlatform();
            vehicle = jsonTrainDeparture.getVehicle();
            delay = jsonTrainDeparture.getDelay();
        }

        LocalTime getLocalTime() {
            return localTime;
        }

        String getPlatform() {
            return platform;
        }

        String getVehicle() {
            return vehicle;
        }

        int getDelay() {
            return delay;
        }

        @Override
        public int compareTo(TrainDepartureVo other) {
            return localTime.compareTo(other.localTime);
        }
    }
}