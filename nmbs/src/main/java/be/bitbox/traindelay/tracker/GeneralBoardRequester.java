package be.bitbox.traindelay.tracker;


import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.board.BoardRequester;
import be.bitbox.traindelay.tracker.core.station.Station;
import be.bitbox.traindelay.tracker.irail.IRailBoardRequester;
import be.bitbox.traindelay.tracker.nmbs.NMBSBoardRequester;
import com.google.common.base.Stopwatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GeneralBoardRequester implements BoardRequester {

    private final NMBSBoardRequester nmbsBoardRequester;
    private final IRailBoardRequester iRailBoardRequester;

    @Autowired
    public GeneralBoardRequester(NMBSBoardRequester nmbsBoardRequester, IRailBoardRequester iRailBoardRequester) {
        this.nmbsBoardRequester = nmbsBoardRequester;
        this.iRailBoardRequester = iRailBoardRequester;
    }

    @Override
    public Board requestBoardFor(Station station) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Board nmbsBoard = nmbsBoardRequester.requestBoardFor(station);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.reset().start();
        Board iRailBoard = iRailBoardRequester.requestBoardFor(station);
        stopwatch.stop();
        System.out.println("NMBS : " + elapsed + "ms IRail : " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
        if (nmbsBoard.getDepartures().equals(iRailBoard.getDepartures())) {
            System.out.println("both boards are equal");
        } else {
            System.err.println("difference");
        }
        System.out.println(iRailBoard);
        System.out.println(nmbsBoard);
        return nmbsBoard;
    }
}
