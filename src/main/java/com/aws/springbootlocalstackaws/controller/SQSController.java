package com.aws.springbootlocalstackaws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/sqs")
@RequiredArgsConstructor
public class SQSController {

    @Value("${aws.sqs.endpoint}")
    private String awsSqsEndpoint;

    @Autowired
    private final QueueMessagingTemplate queueMessagingTemplate;

    @GetMapping("/push/{message}")
    public void pushMessage(@PathVariable("message") String message) {
        queueMessagingTemplate.send(awsSqsEndpoint, MessageBuilder.withPayload(message).build());
    }

    @SqsListener("learning-queue")
    public void loadMessagesFromQueue(String message) {
        System.out.println(" Message from learning-queue Queue {} " + message);
    }


}
