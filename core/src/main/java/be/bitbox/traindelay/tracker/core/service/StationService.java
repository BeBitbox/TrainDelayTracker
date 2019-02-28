package be.bitbox.traindelay.tracker.core.service;

import be.bitbox.traindelay.tracker.core.station.*;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

@Service
public class StationService {
    public final static LocalDate START_DATE_SERVICE = LocalDate.of(2018, Month.FEBRUARY, 22); 
    private final TrainDepartureQuery trainDepartureQuery;
    private final StationRetriever stationRetriever;
    private Set<StationId> availableStations;
    
    @Autowired
    public StationService(TrainDepartureQuery trainDepartureQuery, StationRetriever stationRetriever) {
        this.trainDepartureQuery = trainDepartureQuery;
        this.stationRetriever = stationRetriever;
    }

    public SortedSet<TrainDepartureVo> listTrainDeparturesFor(StationId stationId, String date) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new InvalidDateException(String.format("We cannot process date %s, expected format is year-month-day", date));
        }
        return listTrainDeparturesFor(stationId, localDate);    
    }
    
    public SortedSet<TrainDepartureVo> listTrainDeparturesFor(StationId stationId, LocalDate date) {
        validateStationIsKnown(stationId);
        validateDateIsNotBeforeServiceStart(date);

        return trainDepartureQuery.listTrainDepartureFor(stationId, date)
                .stream()
                .map(TrainDepartureVo::new)
                .collect(toCollection(TreeSet::new));
    }

    private void validateDateIsNotBeforeServiceStart(LocalDate date) {
        if (START_DATE_SERVICE.isAfter(date)) {
            throw new InvalidDateException(String.format("Date %s is too early. It cannot to be after %s", date, START_DATE_SERVICE));
        }
    }

    private void validateStationIsKnown(StationId stationId) {
        if (availableStations == null) {
            readAvailableStations();
        }
        if (!availableStations.contains(stationId)) {
            throw new StationNotFoundException("Not found: " + stationId);
        }
    }

    private void readAvailableStations() {
        availableStations = stationRetriever
                .getStationsFor(Country.BE)
                .stream()
                .map(Station::stationId)
                .collect(Collectors.toSet());
    }
}
