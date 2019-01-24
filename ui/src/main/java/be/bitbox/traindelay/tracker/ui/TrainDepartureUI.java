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

import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.core.station.StationRetriever;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureQuery;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.TreeSet;

import static java.util.stream.Collectors.toCollection;

@SpringUI
@Title("Train departures")
public class TrainDepartureUI extends UI {

    private final Label label;
    private final ComboBox<Station> stationComboBox;
    private final DateField date;
    private final Grid<TrainDepartureVo> grid;
    private final TrainDepartureQuery trainDepartureQuery;

    @Autowired
    public TrainDepartureUI(StationRetriever stationRetriever, TrainDepartureQuery trainDepartureQuery) {
        label = new Label("See all departures");
        stationComboBox = new ComboBox<>();
        stationComboBox.setItems(stationRetriever.getStationsFor(Country.BE));
        stationComboBox.setItemCaptionGenerator(Station::name);
        stationComboBox.setWidth(250f, Unit.PIXELS);
        date = new DateField();
        date.setDateFormat("dd-MM-yyyy");
        date.setValue(LocalDate.now().minusDays(1));
        grid = new Grid<>();
        this.trainDepartureQuery = trainDepartureQuery;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        grid.addColumn(TrainDepartureVo::getLocalTime).setCaption("Departure");
        grid.addColumn(TrainDepartureVo::getPlatform).setCaption("Platform");
        grid.addColumn(TrainDepartureVo::getVehicle).setCaption("Vehicle");
        grid.addColumn(TrainDepartureVo::getDelay).setCaption("Delay (minutes)");
        grid.setSizeFull();

        HorizontalLayout header = new HorizontalLayout(label, stationComboBox, date);
        VerticalLayout mainLayout = new VerticalLayout(header, grid);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(grid, 0.8f);
        setContent(mainLayout);

        stationComboBox.addValueChangeListener(e -> listTrainDepartures(e.getValue(), date.getValue()));
        date.addValueChangeListener(e -> listTrainDepartures(stationComboBox.getValue(), e.getValue()));
    }

    private void listTrainDepartures(Station station, LocalDate date) {
        if (station == null || date == null) {
            return;
        }

        TreeSet<TrainDepartureVo> trainDepartureVos = trainDepartureQuery.listTrainDepartureFor(station.stationId(), date)
                .stream()
                .map(TrainDepartureVo::new)
                .collect(toCollection(TreeSet::new));
        grid.setItems(trainDepartureVos);
    }

    private class TrainDepartureVo implements Comparable {
        private final LocalTime localTime;
        private final String platform;
        private final String vehicle;
        private final int delay;

        private TrainDepartureVo(TrainDepartureEvent trainDepartureEvent) {
            localTime = trainDepartureEvent.getExpectedDepartureTime().toLocalTime();
            platform = trainDepartureEvent.getPlatform();
            vehicle = trainDepartureEvent.getVehicle().replace("BE.NMBS.", "");
            delay = trainDepartureEvent.getDelay() / 60;
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
        public int compareTo(Object o) {
            return localTime.compareTo(((TrainDepartureVo) o).localTime);
        }
    }
}