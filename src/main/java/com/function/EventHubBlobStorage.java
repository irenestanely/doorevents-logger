package com.function;

import com.microsoft.azure.functions.annotation.*;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import com.microsoft.azure.functions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventHubBlobStorage {

    private static final Logger LOGGER = Logger.getLogger(EventHubBlobStorage.class.getName());

    @FunctionName("EventHubTriggerBlobStorage")
    public void persist(
            @EventHubTrigger(name = "message", eventHubName = "test", connection = "EventHubConnectionString", consumerGroup = "$Default", cardinality = Cardinality.MANY) List<String> message,
            final ExecutionContext context) {

        message.forEach(singleMessage -> {
            try (ByteArrayInputStream dataStream = new ByteArrayInputStream(
                    singleMessage.getBytes(Charset.defaultCharset()))) {
                createBlobClient().upload(dataStream, singleMessage.length());
            } catch (IOException e) {
                context.getLogger().log(Level.SEVERE, "IOException on blob storage" + e.getMessage());
            }
        });
    }

    public BlobClient createBlobClient() {
        Optional<BlobClient> blobClient = Optional.empty();
        try {
            blobClient = Optional.of(new BlobServiceClientBuilder()
                    .endpoint(System.getenv("StorageAccountEndpoint"))
                    .sasToken(System.getenv("StorageAccountSASToken"))
                    .buildClient()
                    .createBlobContainerIfNotExists(LocalDate.now().toString())
                    .getBlobClient(LocalDateTime.now().toString()));
        } catch (BlobStorageException e) {
            LOGGER.log(Level.SEVERE, "BlobStorageException details with status: " + e.getStatusCode() +
                    "and response:" + e.getResponse());
        }
        return blobClient.get();
    }
}
