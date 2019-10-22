package be.bitbox.traindelay.tracker.application.local;

import com.amazonaws.services.sqs.AbstractAmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class LocalAmazonSQS extends AbstractAmazonSQS {

    @Override
    public ReceiveMessageResult receiveMessage(ReceiveMessageRequest request) {
        return new ReceiveMessageResult();
    }

    @Override
    public ReceiveMessageResult receiveMessage(String queueUrl) {
        return new ReceiveMessageResult();
    }

    @Override
    public SendMessageResult sendMessage(SendMessageRequest request) {
        return null;
    }
}
