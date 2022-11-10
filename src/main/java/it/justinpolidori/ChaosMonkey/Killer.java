package it.justinpolidori.ChaosMonkey;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Component
public class Killer {

    private final CoreV1Api api;

    Logger logger = LoggerFactory.getLogger(Killer.class);

    {
        try {
            ApiClient client = Config.defaultClient();
            api = new CoreV1Api(client);
        } catch (IOException e) {
            throw new RuntimeException("Unable to authenticate to the cluster:"+e);
        }
    }

    private String selectRandomPod(String namespace, Integer timeoutInSeconds, String labelSelector) throws ApiException {
        Random rand = new Random();

        String fs = "status.phase=Running";
        V1PodList podList = api.listNamespacedPod(namespace, "false",false,
                null, fs, labelSelector, null,
                null, null, timeoutInSeconds, Boolean.FALSE);

        List<V1Pod> candidateToKillList = podList.getItems();

        if(candidateToKillList.isEmpty()) {
            logger.info("No candidate pod to kill found.");
            System.exit(0);
        }

        V1Pod candidateToKill = candidateToKillList.get(rand.nextInt(podList.getItems().size()));

        logger.info("I will kill: " + candidateToKill.getMetadata().getName());

        return candidateToKill.getMetadata().getName();
    }

    public void killPod(String namespace, Integer timeoutInSeconds, String labelSelector,  Integer gracePeriodSeconds)  {
        logger.info("Parameters: namespace {}, timeoutInSeconds {}, labelSelector {}, gracePeriodInSeconds {}", namespace, timeoutInSeconds, labelSelector, gracePeriodSeconds);
        String candidateToKill = null;
        try {
            candidateToKill = this.selectRandomPod(namespace,timeoutInSeconds, labelSelector);
        } catch (ApiException e) {
            logger.error("Got an error while selecting the pod!", e);
            System.exit(1);
        }

        try {
            api.deleteNamespacedPod(candidateToKill, namespace, "false", null, gracePeriodSeconds, Boolean.FALSE, null, null);
        } catch (ApiException e) {
            logger.error("Got an error while deleting the pod!", e);
            System.exit(1);
        }
        logger.info("Pod Killed");
    }
}
