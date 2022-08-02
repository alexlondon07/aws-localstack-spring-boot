package com.aws.springbootlocalstackaws.model;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@DynamoDBTable( tableName = "Car")
@Getter
@Setter
public class Car {

    @Id
    @DynamoDBHashKey(attributeName = "id")
    @JsonIgnore
    private String id;

    @DynamoDBAttribute(attributeName = "color")
    private String color;

    @DynamoDBAttribute(attributeName = "model")
    private String model;

    @DynamoDBAttribute(attributeName = "brand")
    private String brand;

    @DynamoDBAttribute(attributeName = "available")
    private Boolean available;

}