package be.bitbox.traindelay.tracker.persistance.messaging.statistic;

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

import java.io.IOException;
import java.io.StringWriter;

class PublishMissingDailyStatisticSQS {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishMissingDailyStatisticSQS.class);
    private final AmazonSQS amazonSQS;
    private final String queueUrl;

    @Autowired
    PublishMissingDailyStatisticSQS(AmazonSQS amazonSQS,
                                    EventBus eventBus,
                                    @Value("${missingdailystatistic.queue.url}") String queueUrl) {
        this.amazonSQS = amazonSQS;
        this.queueUrl = queueUrl;
        eventBus.register(this);
    }

    @Subscribe
    void subscribeMissingStationStatisticEvent(MissingStationStatisticEvent missingStationStatisticEvent) {
        var objectMapper = new ObjectMapper();
        var stringWriter = new StringWriter();

        try {
            objectMapper.writeValue(stringWriter, missingStationStatisticEvent);
        } catch (IOException e) {
            LOGGER.error("Failed to transform {} to JSON.", missingStationStatisticEvent, e);
        }
        SendMessageRequest request = new SendMessageRequest(queueUrl, stringWriter.toString());
        amazonSQS.sendMessage(request);
    }
}
