package it.justinpolidori.ChaosMonkey;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

@Component
public class Killer {

    private final ApiClient client;
    private final CoreV1Api api;

    {
        try {
            client = Config.defaultClient();
            api = new CoreV1Api(client);
        } catch (IOException e) {
            throw new RuntimeException("Unable to authenticate to the cluster:"+e);
        }
    }

    private String selectRandomPod(String namespace, Integer timeout) throws ApiException {
        Random rand = new Random();

        V1PodList podList = api.listNamespacedPod(namespace, "false",false,
                null, null, null, null,
                null, null, timeout, Boolean.FALSE);


        V1Pod candidateToKill = podList.getItems().get(rand.nextInt(podList.getItems().size()));

        return candidateToKill.getMetadata().getName();
    }

    public boolean killPod(String namespace, String candidateToKill, Integer timeout) throws ApiException {
        V1Pod killedPod = api.deleteNamespacedPod(candidateToKill, namespace, "false", "false", timeout, Boolean.FALSE, null, null);
    }
}
