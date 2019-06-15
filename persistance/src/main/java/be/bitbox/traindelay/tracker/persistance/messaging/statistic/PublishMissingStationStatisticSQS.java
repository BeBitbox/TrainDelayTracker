package be.bitbox.traindelay.tracker.persistance.messaging.statistic;

import be.bitbox.traindelay.tracker.core.statistic.MissingDailyStatisticEvent;
import be.bitbox.traindelay.tracker.core.statistic.MissingStationStatisticEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;

@Component
class PublishMissingStationStatisticSQS {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishMissingStationStatisticSQS.class);
    private final AmazonSQS amazonSQS;
    private final String dailyQueueUrl;
    private final String stationQueueUrl;

    @Autowired
    PublishMissingStationStatisticSQS(AmazonSQS amazonSQS,
                                      EventBus eventBus,
                                      @Value("${missingdailystatistic.queue.url}") String dailyQueueUrl,
                                      @Value("${missingstationstatistic.queue.url}") String stationQueueUrl) {
        this.amazonSQS = amazonSQS;
        this.dailyQueueUrl = dailyQueueUrl;
        this.stationQueueUrl = stationQueueUrl;
        eventBus.register(this);
    }

    @Subscribe
    void subscribeMissingStationStatisticEvent(MissingStationStatisticEvent missingStationStatisticEvent) {
        send(new MissingStationStatisticMessage(missingStationStatisticEvent), stationQueueUrl);
    }

    @Subscribe
    void subscribeMissingDailyStatisticEvent(MissingDailyStatisticEvent missingDailyStatisticEvent) {
        send(new MissingDailyStatisticMessage(missingDailyStatisticEvent), dailyQueueUrl);
    }

    private void send(Object message, String queue){
        var objectMapper = new ObjectMapper();
        var stringWriter = new StringWriter();

        try {
            objectMapper.writeValue(stringWriter, message);
        } catch (IOException e) {
            LOGGER.error("Failed to transform {} to JSON.", message, e);
        }
        SendMessageRequest request = new SendMessageRequest(queue, stringWriter.toString());
        amazonSQS.sendMessage(request);
    }
}
