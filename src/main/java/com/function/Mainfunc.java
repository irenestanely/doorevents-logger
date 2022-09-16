package router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.azure.cosmos.util.CosmosPagedIterable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.implementation.Utils;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;

public class Mainfunc {

    public static void main(String[] args) {
       // CosmosClient.CreateAndInitializeAsync("AccountEndpoint=https://vin-subscriptions.documents.azure.com:443/;AccountKey=lmJL5NyOB2yvWC36SrmWYNlhk9EAIQgnfwOwLYbMITaIHnEAqKRTXi7UlteEtN3G0XqW8ykBLeK80KdJElFwag==;");
       asyncClient();
    }


    public static void asyncClient() {
        long startTime = new Date().getTime();
        CosmosAsyncClient cosmosClient = new CosmosClientBuilder()
            .endpoint("https://vin-subscriptions.documents.azure.com:443/vin-subscriptions")
            .key("lmJL5NyOB2yvWC36SrmWYNlhk9EAIQgnfwOwLYbMITaIHnEAqKRTXi7UlteEtN3G0XqW8ykBLeK80KdJElFwag==")
            .consistencyLevel(ConsistencyLevel.CONSISTENT_PREFIX)
            .preferredRegions(Arrays.asList("East US"))
            .directMode()
            .buildAsyncClient();


            long clientTime = new Date().getTime();
            long timeElapsed1 = clientTime - startTime;

            System.out.println("Execution1 time in milliseconds: " + timeElapsed1);


            CosmosAsyncDatabase database = cosmosClient.getDatabase("Subscriptions-example");
            CosmosAsyncContainer container = database.getContainer("vehicles");

            String sql = "SELECT r.vin as vin , r.subscriptionId as subscriptionId, r.filters as filters FROM Items r WHERE r.vin = '" + 1000001 + "'";


            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
            options.setQueryMetricsEnabled(true);

            CosmosPagedFlux<VehicleSubscription> pagedFluxResponse = container.queryItems(
                sql, options, VehicleSubscription.class);

                List<VehicleSubscription> list = new ArrayList<>();
            long queryTime = new Date().getTime();
            long timeElapsed2 =  queryTime- clientTime;

            System.out.println("Execution2 time in milliseconds: " + timeElapsed2);
            pagedFluxResponse.byPage().toIterable().forEach(response -> {
                System.out.println("Diagnostics::"+response.getCosmosDiagnostics().toString());
                list.addAll(response.getElements().stream().collect(Collectors.toList()));
            });
                System.out.println("list::"+list.toString());
                long endTime = new Date().getTime();
                long timeElapsed = endTime - startTime;

                System.out.println("Execution time in milliseconds: " + timeElapsed);
    }


}
