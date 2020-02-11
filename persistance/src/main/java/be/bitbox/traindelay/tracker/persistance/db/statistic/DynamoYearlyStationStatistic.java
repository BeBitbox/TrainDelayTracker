package be.bitbox.traindelay.tracker.persistance.db.statistic;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import be.bitbox.traindelay.tracker.core.statistic.StationStatistic;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.statistic.StationStatistic.StationStatisticBuilder.aStationStatistic;

@DynamoDBTable(tableName = "YearlyStationStatistic")
public class DynamoYearlyStationStatistic {

    @DynamoDBHashKey(attributeName = "station")
    private String stationId;

    @DynamoDBAttribute(attributeName = "departures")
    private int departures;

    @DynamoDBAttribute(attributeName = "delays")
    private int delays;

    @DynamoDBAttribute(attributeName = "averageDelay")
    private int averageDelay;

    @DynamoDBAttribute(attributeName = "cancellations")
    private int cancellations;

    @DynamoDBAttribute(attributeName = "platformChanges")
    private int platformChanges;

    public DynamoYearlyStationStatistic() {
    }

    DynamoYearlyStationStatistic(StationStatistic stationStatistic) {
        this.stationId = stationStatistic.getStationId().getId();
        this.departures = stationStatistic.getDepartures();
        this.delays = stationStatistic.getDelays();
        this.averageDelay = stationStatistic.getAverageDelay();
        this.cancellations = stationStatistic.getCancellations();
        this.platformChanges = stationStatistic.getPlatformChanges();
    }

    StationStatistic toStationStatistic() {
        return aStationStatistic()
                .withStationId(aStationId(stationId))
                .withDepartures(departures)
                .withDelays(delays)
                .withAverageDelay(averageDelay)
                .withCancellations(cancellations)
                .withPlatformChanges(platformChanges)
                .build();
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public int getDepartures() {
        return departures;
    }

    public void setDepartures(int departures) {
        this.departures = departures;
    }

    public int getDelays() {
        return delays;
    }

    public void setDelays(int delays) {
        this.delays = delays;
    }

    public int getAverageDelay() {
        return averageDelay;
    }

    public void setAverageDelay(int averageDelay) {
        this.averageDelay = averageDelay;
    }

    public int getCancellations() {
        return cancellations;
    }

    public void setCancellations(int cancellations) {
        this.cancellations = cancellations;
    }

    public int getPlatformChanges() {
        return platformChanges;
    }

    public void setPlatformChanges(int platformChanges) {
        this.platformChanges = platformChanges;
    }
}
