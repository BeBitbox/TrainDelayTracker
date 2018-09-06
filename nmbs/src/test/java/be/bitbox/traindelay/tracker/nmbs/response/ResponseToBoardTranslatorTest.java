package be.bitbox.traindelay.tracker.nmbs.response;

import be.bitbox.traindelay.tracker.core.TrainDeparture;
import be.bitbox.traindelay.tracker.core.board.Board;
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

    @Test
    public void translateFrom() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("response.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Response response = objectMapper.readValue(inputStream, Response.class);

        ResponseToBoardTranslator translator = new ResponseToBoardTranslator();
        Board board = translator.translateFrom(response);
        assertThat(board).isNotNull();
        assertThat(board.getStationId()).isEqualTo(aStationId("BE.NMBS.008814001"));
        assertThat(board.getTime()).isNotNull();
        assertThat(board.getDepartures()).hasSize(50);

        TrainDeparture trainDeparture = board.getDepartures().get(0);
        assertThat(trainDeparture.getTime()).isEqualTo(LocalDateTime.of(2018, Month.AUGUST, 28, 6, 50, 0));
        assertThat(trainDeparture.getDelay()).isEqualTo(11);
        assertThat(trainDeparture.isCanceled()).isFalse();
        assertThat(trainDeparture.getVehicule()).isEqualTo("BE.NMBS.IC 2005");
        assertThat(trainDeparture.getPlatform()).isEqualTo("20");
        assertThat(trainDeparture.isPlatformChange()).isFalse();
    }
}