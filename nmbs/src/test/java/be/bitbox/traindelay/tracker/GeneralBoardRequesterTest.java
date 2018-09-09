package be.bitbox.traindelay.tracker;

import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.station.Country;
import be.bitbox.traindelay.tracker.irail.IRailBoardRequester;
import be.bitbox.traindelay.tracker.nmbs.NMBSBoardRequester;
import org.junit.Test;

import static be.bitbox.traindelay.tracker.core.station.Station.aStation;
import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;

public class GeneralBoardRequesterTest {

    @Test
    public void testDifferenceInBoards() {
        String url = "http://www.belgianrail.be/jp/sncb-nmbs-routeplanner/mgate.exe";
        NMBSBoardRequester nmbsBoardRequester = new NMBSBoardRequester(url);
        IRailBoardRequester iRailBoardRequester = new IRailBoardRequester("https://api.irail.be/liveboard/?id=");
        GeneralBoardRequester generalBoardRequester = new GeneralBoardRequester(nmbsBoardRequester, iRailBoardRequester);

        Board deinze = generalBoardRequester.requestBoardFor(aStation(aStationId("BE.NMBS.008892106"), "Deinze", Country.BE));
        Board antwerp = generalBoardRequester.requestBoardFor(aStation(aStationId("BE.NMBS.008821006"), "Antwerpen-Centraal", Country.BE));
        Board godinne = generalBoardRequester.requestBoardFor(aStation(aStationId("BE.NMBS.008863560"), "Godinne", Country.BE));
//        Board apenOost = generalBoardRequester.requestBoardFor(aStation(aStationId("BE.NMBS.008821022"), "Antwerpen-Oost", Country.BE));
    }
}