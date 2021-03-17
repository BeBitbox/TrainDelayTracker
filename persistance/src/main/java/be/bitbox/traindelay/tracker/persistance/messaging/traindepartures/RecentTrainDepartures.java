package be.bitbox.traindelay.tracker.persistance.messaging.traindepartures;

import be.bitbox.traindelay.tracker.core.traindeparture.JsonTrainDeparture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class RecentTrainDepartures {
    private final int maxTrainDepartures;
    private final SortedSet<JsonTrainDeparture> departures;

    @Autowired
    public RecentTrainDepartures(@Value("${traindepartures.recent.max}") int maxTrainDepartures) {
        this.maxTrainDepartures = maxTrainDepartures;
        departures = new TreeSet<>();
    }

    synchronized void addTrainDeparture(JsonTrainDeparture trainDeparture) {
        departures.add(trainDeparture);
        while (departures.size() > maxTrainDepartures) {
            departures.remove(departures.last());
        }
    }

    public List<JsonTrainDeparture> list() {
        return newArrayList(departures);
    }
}
