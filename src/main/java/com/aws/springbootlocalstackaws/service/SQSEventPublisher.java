package com.aws.springbootlocalstackaws.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class SQSEventPublisher {

    @Autowired
    private AmazonSQS amazonSQS;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishEvent(JsonNode message) {
        log.info("Generating event : {}", message);
        SendMessageRequest sendMessageRequest = null;
        try {
            sendMessageRequest = new SendMessageRequest().withQueueUrl("http://localhost:4566/000000000000/sample-queue.fifo")
                    .withMessageBody(objectMapper.writeValueAsString(message))
                    .withMessageGroupId("Sample Message")
                    .withMessageDeduplicationId(UUID.randomUUID().toString());
            amazonSQS.sendMessage(sendMessageRequest);
            log.info("Event has been published in SQS.");
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException e : {} and stacktrace : {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Exception ocurred while pushing event to sqs : {} and stacktrace ; {}", e.getMessage(), e);
        }

    }
}
