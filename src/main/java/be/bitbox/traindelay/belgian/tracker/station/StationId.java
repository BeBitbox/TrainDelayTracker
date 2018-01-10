package be.bitbox.traindelay.belgian.tracker.station;

import static be.bitbox.traindelay.belgian.tracker.ValidationUtils.checkNotEmpty;

public class StationId {

    private final String id;

    private StationId(String id) {
        this.id = id;
    }

    public static StationId aStationId(String id) {
        checkNotEmpty(id ,"Id cannot be null!");
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

    @Override
    public String toString() {
        return "StationId{" +
                "id='" + id + '\'' +
                '}';
    }
}
