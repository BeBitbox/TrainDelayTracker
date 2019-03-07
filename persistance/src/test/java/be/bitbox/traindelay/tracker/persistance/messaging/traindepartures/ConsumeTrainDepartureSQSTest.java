package be.bitbox.traindelay.tracker.persistance.messaging.traindepartures;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConsumeTrainDepartureSQSTest {

    @Mock
    private AmazonSQS amazonSQS;

    @Test
    public void updateLatestTrainDepartures() throws IOException {
        var recentTrainDepartures = new RecentTrainDepartures(5);
        var consumeTrainDepartureSQS = new ConsumeTrainDepartureSQS(amazonSQS, "url", recentTrainDepartures);
        var receiveMessageResult = new ReceiveMessageResult();
        var message = new Message();

        message.setBody(TestJsonFileReader.readJson());
        receiveMessageResult.setMessages(List.of(message));
        when(amazonSQS.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(receiveMessageResult);

        consumeTrainDepartureSQS.updateLatestTrainDepartures();

        var jsonTrainDepartures = recentTrainDepartures.list();
        assertThat(jsonTrainDepartures.size(), is(1));
        assertThat(jsonTrainDepartures.get(0).getStation(), is("myStation"));
    }
}