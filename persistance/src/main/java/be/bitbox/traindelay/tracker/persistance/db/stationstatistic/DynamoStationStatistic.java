package be.bitbox.traindelay.tracker.persistance.db.stationstatistic;

import be.bitbox.traindelay.tracker.core.stationstatistic.StationStatistic;
import be.bitbox.traindelay.tracker.persistance.db.InstantConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

import java.time.Instant;
import java.time.LocalDate;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static be.bitbox.traindelay.tracker.core.stationstatistic.StationStatistic.StationStatisticBuilder.aStationStatistic;

@DynamoDBTable(tableName = "StationStatistic")
public class DynamoStationStatistic {

    @DynamoDBHashKey(attributeName = "station")
    private String stationId;

    @DynamoDBRangeKey(attributeName = "local_date")
    private String date;

    @DynamoDBTypeConverted(converter = InstantConverter.class)
    @DynamoDBAttribute(attributeName = "creationTime")
    private Instant creationTime;

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

    public DynamoStationStatistic() {
    }

    DynamoStationStatistic(StationStatistic stationStatistic) {
        this.stationId = stationStatistic.getStationId().getId();
        this.date = stationStatistic.getDay().toString();
        this.creationTime = Instant.now();
        this.departures = stationStatistic.getDepartures();
        this.delays = stationStatistic.getDelays();
        this.averageDelay = stationStatistic.getAverageDelay();
        this.cancellations = stationStatistic.getCancellations();
        this.platformChanges = stationStatistic.getPlatformChanges();
    }

    StationStatistic toStationStatistic() {
        return aStationStatistic()
                .withStationId(aStationId(stationId))
                .withDay(LocalDate.parse(date))
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
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
