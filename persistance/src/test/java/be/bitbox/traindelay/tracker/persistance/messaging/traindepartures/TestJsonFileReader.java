package be.bitbox.traindelay.tracker.persistance.messaging.traindepartures;

import com.amazonaws.util.IOUtils;

import java.io.IOException;

final class TestJsonFileReader {
    private TestJsonFileReader() {}

    static String readJson() throws IOException {
        return IOUtils.toString(TestJsonFileReader.class.getResourceAsStream("expectedFormat.json"));
    }
}
