package com.aws.springbootlocalstackaws.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

@Configuration
public class AwsSQSConfig {

    @Value("${aws.sqs.endpoint}")
    private String awsEndpoint;

    @Value("${aws.accesskey}")
    private String awsAccessKey;

    @Value("${aws.secretkey}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
        return new QueueMessagingTemplate(amazonSQSAsync());
    }



    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion)
                )
                .withCredentials(new AWSStaticCredentialsProvider(getCredentialsAWS()))
                .build();
    }

    public AWSCredentials getCredentialsAWS() {
        return new BasicAWSCredentials(
                awsAccessKey,
                awsSecretKey
        );
    }
}
