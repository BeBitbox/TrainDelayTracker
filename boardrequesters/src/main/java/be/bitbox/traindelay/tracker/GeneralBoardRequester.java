package be.bitbox.traindelay.tracker;


import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.board.BoardRequestException;
import be.bitbox.traindelay.tracker.core.board.BoardRequester;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.irail.IRailBoardRequester;
import be.bitbox.traindelay.tracker.nmbs.NMBSBoardRequester;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.metric.consumer.HealthCountsStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class GeneralBoardRequester implements BoardRequester {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralBoardRequester.class);
    private final NMBSBoardRequester nmbsBoardRequester;
    private final IRailBoardRequester iRailBoardRequester;

    @Autowired
    public GeneralBoardRequester(NMBSBoardRequester nmbsBoardRequester, IRailBoardRequester iRailBoardRequester) {
        this.nmbsBoardRequester = nmbsBoardRequester;
        this.iRailBoardRequester = iRailBoardRequester;
    }

    @Override
    public Board requestBoardFor(Station station) {
        Board board;
        try {
            board = nmbsBoardRequester.requestBoardFor(station);
        } catch (BoardRequestException ex) {
            LOGGER.error("Failure for NMBS, trying iRail for station " + station, ex);
            board = iRailBoardRequester.requestBoardFor(station);
        }
        return board;
    }

    @Override
    public void resetCircuitBreakers() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // IGNORE
        }
        Hystrix.reset();
        HealthCountsStream.reset();
    }
}
