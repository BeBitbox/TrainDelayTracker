package be.bitbox.traindelay.tracker.persistance.board;

import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.board.BoardDao;
import be.bitbox.traindelay.tracker.core.station.StationId;
import be.bitbox.traindelay.tracker.persistance.AWSTestClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;

import static be.bitbox.traindelay.tracker.core.traindeparture.TrainDeparture.aTrainDeparture;
import static be.bitbox.traindelay.tracker.core.board.Board.aBoardForStation;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.of;
import static java.time.Month.JANUARY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DynamoBoardDaoIntegrationTest {

    @Test
    @Ignore("Requires real DB")
    public void testDao() {
        AmazonDynamoDB client = AWSTestClient.create();
        BoardDao boardDao = new DynamoBoardDao(client);

        StationId id = StationId.aStationId("mystation");
        Board board = aBoardForStation(id, now());
        LocalDateTime trainLeavingTime = of(2018, JANUARY, 12, 5, 46, 0);
        board.addDeparture(aTrainDeparture(trainLeavingTime, 5, false, "MyTrain1", "C", false));
        LocalDateTime trainLeavingTime2 = of(2018, JANUARY, 12, 5, 46, 0);
        board.addDeparture(aTrainDeparture(trainLeavingTime2, 0, true, "MyTrain2", "7", true));

        boardDao.saveBoard(board);

        Board actualBoard = boardDao.getLastBoardFor(id);
        assertThat(actualBoard, is(board));
    }
}