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
package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.core.service.JsonTrainDeparture;
import be.bitbox.traindelay.tracker.core.service.StationService;
import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.stream.Collectors;

@Route("")
@PWA(name = "Train departures", shortName = "Train departures")
public class TrainDepartureUI extends VerticalLayout {
    private final Label label;
    private final ComboBox<Station> stationComboBox;
    private final DatePicker date;
    private final Grid<TrainDepartureVo> grid;
    private final StationService stationService;

    @Autowired
    public TrainDepartureUI(StationRetriever stationRetriever, StationService stationService) {
        this.stationService = stationService;
        label = new Label("See all departures");
        stationComboBox = new ComboBox<>();
        stationComboBox.setItems(stationRetriever.getStationsFor(Country.BE));
        stationComboBox.setItemLabelGenerator((ItemLabelGenerator<Station>) Station::name);
        stationComboBox.setWidth("250px");
        date = new DatePicker(LocalDate.now().minusDays(1), new Locale("nl", "be"));
        grid = new Grid<>();
        grid.addColumn(TrainDepartureVo::getLocalTime).setHeader("Departure");
        grid.addColumn(TrainDepartureVo::getPlatform).setHeader("Platform");
        grid.addColumn(TrainDepartureVo::getVehicle).setHeader("Vehicle");
        grid.addColumn(TrainDepartureVo::getDelay).setHeader("Delay (minutes)");
        grid.setSizeFull();

        HorizontalLayout header = new HorizontalLayout(label, stationComboBox, date);
        add(header, grid);
        setSizeFull();
        
        stationComboBox.addValueChangeListener(e -> listTrainDepartures(e.getValue(), date.getValue()));
        date.addValueChangeListener(e -> listTrainDepartures(stationComboBox.getValue(), e.getValue()));
    }

    private void listTrainDepartures(Station station, LocalDate date) {
        if (station == null || date == null) {
            return;
        }
        var trainDepartureVos = stationService.listTrainDeparturesFor(station.stationId(), date)
                .stream()
                .map(TrainDepartureVo::new)
                .collect(Collectors.toSet());
        grid.setItems(trainDepartureVos);
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