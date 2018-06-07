package be.bitbox.traindelay.tracker.nmbs.gtfs;

import be.bitbox.traindelay.tracker.nmbs.gtfs.GtfsSomething;
import com.google.common.io.ByteStreams;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

public class GtfsSomethingTest {
    @Test
    public void gtfs() throws Exception {
        while (true) {
            GtfsSomething something = new GtfsSomething();
//        InputStream inputStream = this.getClass().getResourceAsStream("feed");
            InputStream inputStream = new URL("").openStream();
            something.gtfs(ByteStreams.toByteArray(inputStream));
            Thread.sleep(15000);
        }

    }

}