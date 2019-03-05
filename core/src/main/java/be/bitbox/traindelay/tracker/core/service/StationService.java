package be.bitbox.traindelay.tracker.core.service;

import be.bitbox.traindelay.tracker.core.station.*;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

@Service
public class StationService {
    public final static LocalDate START_DATE_SERVICE = LocalDate.of(2018, Month.FEBRUARY, 22); 
    private final TrainDepartureRepository trainDepartureRepository;
    private final StationRetriever stationRetriever;
    private Set<StationId> availableStations;
    
    @Autowired
    public StationService(TrainDepartureRepository trainDepartureRepository, StationRetriever stationRetriever) {
        this.trainDepartureRepository = trainDepartureRepository;
        this.stationRetriever = stationRetriever;
    }

    public SortedSet<JsonTrainDeparture> listTrainDeparturesFor(StationId stationId, String date) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new InvalidDateException(String.format("We cannot process date %s, expected format is year-month-day", date));
        }
        return listTrainDeparturesFor(stationId, localDate);    
    }
    
    public SortedSet<JsonTrainDeparture> listTrainDeparturesFor(StationId stationId, LocalDate date) {
        validateStationIsKnown(stationId);
        validateDateIsNotBeforeServiceStart(date);

        return trainDepartureRepository.listTrainDepartureFor(stationId, date)
                .stream()
                .map(JsonTrainDeparture::new)
                .collect(toCollection(TreeSet::new));
    }
    
    public List<JsonTrainDeparture> listRecentTrainDepartures() {
        return trainDepartureRepository.listRecentTrainDepartures();
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
