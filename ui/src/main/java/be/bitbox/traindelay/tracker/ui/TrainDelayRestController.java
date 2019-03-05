package be.bitbox.traindelay.tracker.ui;

import be.bitbox.traindelay.tracker.core.service.JsonTrainDeparture;
import be.bitbox.traindelay.tracker.core.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collection;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;

@RestController
public class TrainDelayRestController {
    private final StationService stationService;

    @Autowired
    public TrainDelayRestController(StationService stationService) {
        this.stationService = stationService;
    }

    @RequestMapping("/api/v1/station/{stationId}/depatures")
    public Collection<JsonTrainDeparture> trainsOnDate(@PathVariable String stationId,
                                                       @RequestParam(required = false) String date) {
        if (StringUtils.isEmpty(date)) {
            return stationService.listTrainDeparturesFor(aStationId(stationId), LocalDate.now());
        } else {
            return stationService.listTrainDeparturesFor(aStationId(stationId), date);
        }
    }
}
