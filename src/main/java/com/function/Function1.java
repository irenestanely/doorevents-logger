package com.function;

import java.util.Random;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.BlobOutput;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;
import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.StorageAccount;
import com.microsoft.azure.functions.annotation.TimerTrigger;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function1 {


    @FunctionName("sendTime")
    @EventHubOutput(name = "event", eventHubName = "", connection = "default_eventhub_connection")
    public String sendTimes(@TimerTrigger(
        name = "sendTimeTrigger",
        schedule = "*/20 * * * * *") // every 20 seconds
        String timerInfo,
    final ExecutionContext context)  //every 10 s
    {
        context.getLogger().info("Java Timer trigger function executed at: "
            + java.time.LocalDateTime.now());  
        return LocalDateTime.now().toString();  
    }

    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("example")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    @FunctionName("sendTime")
    @EventHubOutput(name = "event", eventHubName = "", connection = "default_eventhub_connection")
    public String sendTime(@TimerTrigger(
        name = "sendTimeTrigger",
        schedule = "*/20 * * * * *") // every 20 seconds
        String timerInfo,
    final ExecutionContext context)  //every 10 s
    {
        Random rand = new Random(); //instance of random class
        int upperbound = 25;
        //generate random values from 0-24
        int int_random = rand.nextInt(upperbound);
        context.getLogger().info("Java Timer trigger function executed at: "
            + java.time.LocalDateTime.now());  
        return LocalDateTime.now().toString();  
    }


    @FunctionName("sendDoorEvents")
    @EventHubOutput(name = "event", eventHubName = "test", connection = "EventHubConnectionString")
    public DoorEvent sendDoorEvents(@TimerTrigger(
        name = "timerInfo",
        schedule = "*/10 * * * * *") // every 10 seconds
        String timerInfo,
    final ExecutionContext context)  //every 10 s
    {
        Random rand = new Random(); //instance of random class
        int upperbound = 25;
        //generate random values from 0-24
        int int_random = rand.nextInt(upperbound);
        context.getLogger().info("Java Timer trigger function executed at: "
            + java.time.LocalDateTime.now());
        return new DoorEvent("test"+int_random, timerInfo);
    }

    @FunctionName("processDoorEvents")
    public void processDoorEvents(
        @EventHubTrigger(
            name = "msg",
            eventHubName = "test", // blank because the value is included in the connection string
            cardinality = Cardinality.ONE,
            connection = "EventHubConnectionString",
            consumerGroup = "$Default")
            DoorEvent item,
        @CosmosDBOutput(
            name = "databaseOutput",
            databaseName = "DoorEventsDb",
            collectionName = "DoorEvents",
            connectionStringSetting = "CosmosDBConnectionString")
            OutputBinding<DoorEvent> document,
        final ExecutionContext context) {

        context.getLogger().info("Event hub message received: " + item.toString());

        document.setValue(item);
    }

    @FunctionName("file")
    @StorageAccount("AzureWebJobsStorage")
    public HttpResponseMessage createFile(@HttpTrigger(name = "req", methods = {
            HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<DoorEvent>> request,
            @BindingName("name") String name, @BindingName("content") String content,
            @BlobOutput(name = "target", path = "file-container/{name}", dataType = "binary") OutputBinding<byte[]> outputItem,
            final ExecutionContext context) {
        try {
            byte[] decodedURLBytes = Base64.getUrlDecoder().decode(content);
            outputItem.setValue(decodedURLBytes);
        } catch (Exception ex) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(ex.getMessage()).build();
        }

        return request.createResponseBuilder(HttpStatus.OK).body("The File \"" + name + " is created").build();
    }

}


