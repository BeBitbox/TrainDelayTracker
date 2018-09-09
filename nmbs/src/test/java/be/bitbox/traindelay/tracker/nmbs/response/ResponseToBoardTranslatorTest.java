package be.bitbox.traindelay.tracker.nmbs.response;

import be.bitbox.traindelay.tracker.core.TrainDeparture;
import be.bitbox.traindelay.tracker.core.board.Board;
import be.bitbox.traindelay.tracker.core.board.BoardNotFoundException;
import be.bitbox.traindelay.tracker.core.station.StationId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.Month;

import static be.bitbox.traindelay.tracker.core.station.StationId.aStationId;
import static org.assertj.core.api.Assertions.assertThat;

public class ResponseToBoardTranslatorTest {
    private ResponseToBoardTranslator translator = ResponseToBoardTranslator.INSTANCE;

    @Test
    public void translateNormalCase() throws IOException {
        Response response = getResponseFrom("response.json");

        Board board = translator.translateFrom(response);
        assertThat(board).isNotNull();
        assertThat(board.getStationId()).isEqualTo(aStationId("BE.NMBS.008814001"));
        assertThat(board.getTime()).isNotNull();
        assertThat(board.getDepartures()).hasSize(50);

        TrainDeparture trainDeparture = board.getDepartures().get(0);
        assertThat(trainDeparture.getTime()).isEqualTo(LocalDateTime.of(2018, Month.AUGUST, 28, 6, 50, 0));
        assertThat(trainDeparture.getDelay()).isEqualTo(660);
        assertThat(trainDeparture.isCanceled()).isFalse();
        assertThat(trainDeparture.getVehicule()).isEqualTo("BE.NMBS.IC2005");
        assertThat(trainDeparture.getPlatform()).isEqualTo("20");
        assertThat(trainDeparture.isPlatformChange()).isFalse();
    }

    @Test
    public void translateCanceledTrain() throws IOException {
        Response response = getResponseFrom("responseWithCancel.json");

        Board board = translator.translateFrom(response);
        assertThat(board).isNotNull();
        assertThat(board.getStationId()).isEqualTo(aStationId("BE.NMBS.008892106"));
        assertThat(board.getTime()).isNotNull();
        assertThat(board.getDepartures()).hasSize(50);

        TrainDeparture trainDeparture = board.getDepartures().get(0);
        assertThat(trainDeparture.getTime()).isEqualTo(LocalDateTime.of(2018, Month.SEPTEMBER, 4, 17, 23, 0));
        assertThat(trainDeparture.getDelay()).isEqualTo(0);
        assertThat(trainDeparture.isCanceled()).isTrue();
        assertThat(trainDeparture.getVehicule()).isEqualTo("BE.NMBS.IC437");
        assertThat(trainDeparture.getPlatform()).isEqualTo("?");
        assertThat(trainDeparture.isPlatformChange()).isFalse();
    }

    @Test
    public void translateUndoubledTrain() throws IOException {
        Response response = getResponseFrom("responseWithUndoubledTrains.json");

        Board board = translator.translateFrom(response);
        assertThat(board).isNotNull();
        assertThat(board.getStationId()).isEqualTo(aStationId("BE.NMBS.008821006"));
        assertThat(board.getTime()).isNotNull();
        assertThat(board.getDepartures()).hasSize(50);

        assertThat(board.getDepartures().get(0).getVehicule()).isEqualTo("BE.NMBS.S322580");
        assertThat(board.getDepartures().get(30).getVehicule()).isEqualTo("BE.NMBS.IC3133");
        assertThat(board.getDepartures().get(31).getVehicule()).isEqualTo("BE.NMBS.IC4312");
        assertThat(board.getDepartures().get(32).getVehicule()).isEqualTo("BE.NMBS.IC4312");
        assertThat(board.getDepartures().get(49).getVehicule()).isEqualTo("BE.NMBS.IC4313");
    }

    @Test(expected = BoardNotFoundException.class)
    public void translateEmptyBoard() throws IOException {
        Response response = getResponseFrom("responseEmpty.json");
        translator.translateFrom(response);
    }

    private Response getResponseFrom(String fileName) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, Response.class);
    }
}