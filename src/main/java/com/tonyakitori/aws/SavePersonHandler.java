package com.tonyakitori.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class SavePersonHandler implements RequestHandler<PersonRequest, PersonResponse> {

    private AmazonDynamoDB amazonDynamoDB;

    private String DYNAMO_DB_TABLE_NAME = "Person";
    private Regions REGION = Regions.US_EAST_1;

    @Override
    public PersonResponse handleRequest(PersonRequest personRequest, Context context) {
        this.initDynamoDbClient();

        persistData(personRequest);

        PersonResponse personResponse = new PersonResponse();
        personResponse.setMessage("Saved Successfully!");
        return personResponse;
    }

    private void persistData(PersonRequest personRequest) throws ConditionalCheckFailedException {

        Map<String, AttributeValue> attributesMap = new HashMap<>();

        attributesMap.put("id", new AttributeValue().withN(String.valueOf(personRequest.getId())));
        attributesMap.put("firstName", new AttributeValue(personRequest.getFirstName()));
        attributesMap.put("lastName", new AttributeValue(personRequest.getLastName()));
        attributesMap.put("age", new AttributeValue().withN(String.valueOf(personRequest.getAge())));
        attributesMap.put("address", new AttributeValue(personRequest.getAddress()));

        amazonDynamoDB.putItem(DYNAMO_DB_TABLE_NAME, attributesMap);
    }

    private void initDynamoDbClient() {
        this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
    }
}
