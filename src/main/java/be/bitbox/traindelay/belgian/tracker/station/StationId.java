package be.bitbox.traindelay.belgian.tracker.station;

import org.springframework.util.StringUtils;

import static org.springframework.util.StringUtils.isEmpty;

public class StationId {

    private final String id;

    private StationId(String id) {
        this.id = id;
    }

    public static StationId aStationId(String id) {
        if (isEmpty(id)) {
            throw new IllegalArgumentException("Id cannot be null!");
        }
        return new StationId(id);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StationId stationId = (StationId) o;

        return id.equals(stationId.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
