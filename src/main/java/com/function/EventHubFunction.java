package com.function;

import java.util.Random;
import java.util.logging.Level;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BlobOutput;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.StorageAccount;
import com.microsoft.azure.functions.annotation.TimerTrigger;

import java.util.Optional;

public class EventHubFunction {
    @FunctionName("sendDoorEvents")
    @EventHubOutput(name = "event", eventHubName = "test", connection = "EventHubConnectionString")
    public DoorEvent sendDoorEvents(@TimerTrigger(name = "timerInfo", schedule = "*/10 * * * * *") // every 10 seconds
    String timerInfo,
            final ExecutionContext context) // every 10 s
    {
        Random rand = new Random(); // instance of random class
        int upperbound = 25;
        // generate random values from 0-24
        int int_random = rand.nextInt(upperbound);
        context.getLogger().info("Java Timer trigger function executed at: "
                + java.time.LocalDateTime.now());
        return new DoorEvent("test" + int_random, "v001");
    }
}
