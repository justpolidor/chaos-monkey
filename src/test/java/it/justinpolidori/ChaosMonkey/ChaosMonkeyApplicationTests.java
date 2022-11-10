package it.justinpolidori.ChaosMonkey;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        properties = {"SPR_PROFILE=test"},
        classes = ChaosMonkeyApplicationTests.class
)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CommandLineRunner.class))
class ChaosMonkeyApplicationTests {

    Integer timeoutInSeconds = 30;
    private Integer gracePeriodInSeconds = 30;
    private String labelSelector = null;
    String hardcodedNamespace = "workloads";
    CoreV1Api api;

    {
        try {
            ApiClient localClient = Config.defaultClient();
            CoreV1Api localApi = new CoreV1Api(localClient);
            V1Secret secret = localApi.readNamespacedSecret("chaos-monkey", "chaos-monkey", "false");
            String token = new String(secret.getData().get("token"));

            ApiClient client = Config.fromToken("https://127.0.0.1:57401",token, false);
            this.api = new CoreV1Api(client);

        } catch (IOException | ApiException e) {
            throw new RuntimeException("Unable to authenticate to the cluster:"+e);
        }
    }

    @Test
    public void whenWrongNamespaceThenStatusCode1() throws Exception {
        String wrongNamespace = "kube-system";
        Killer killer = new Killer(this.api);
        int status = SystemLambda.catchSystemExit(() -> {
            killer.killPod(wrongNamespace, timeoutInSeconds, labelSelector, gracePeriodInSeconds);
        });
        assertEquals(1, status);
    }

    @Test
    public void whenRightNamespaceThenStatusCode0() throws Exception {
        String rightNamespace = "workloads";
        Killer killer = new Killer(this.api);
        int status = SystemLambda.catchSystemExit(() -> {
            killer.killPod(rightNamespace, timeoutInSeconds, labelSelector, gracePeriodInSeconds);
        });
        assertEquals(0, status);
    }
}
