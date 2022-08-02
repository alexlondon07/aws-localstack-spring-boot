package com.aws.springbootlocalstackaws.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.aws.springbootlocalstackaws.repository")
public class DynamoDBConfig {

    @Value("${aws.dynamodb.endpoint}")
    private String awsEndpoint;

    @Value("${aws.accesskey}")
    private String awsAccesskey;

    @Value("${aws.secretkey}")
    private String awsSecretkey;

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig(){
        return DynamoDBMapperConfig.DEFAULT;
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB(){
        return AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration( new AwsClientBuilder.EndpointConfiguration(
                        awsEndpoint, awsRegion))
                .withCredentials(new AWSStaticCredentialsProvider( new BasicAWSCredentials(
                        awsAccesskey, awsSecretkey)))
                .build();
    }

}
