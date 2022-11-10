package it.justinpolidori.ChaosMonkey;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1Api;
import io.kubernetes.client.openapi.models.V1ClusterRole;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PolicyRule;
import io.kubernetes.client.util.Config;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Autowired
    Killer killer;
    @Test
    public void whenWrongNamespaceThenStatusCode1() throws Exception {
        String wrongNamespace = "kube-system";
        int status = SystemLambda.catchSystemExit(() -> {
            killer.killPod(wrongNamespace, timeoutInSeconds, labelSelector, gracePeriodInSeconds);
        });
        assertEquals(1, status);
    }

	@Test
	void contextLoads() throws IOException {

        ApiClient client = Config.fromToken("https://127.0.0.1:6443", )
        CoreV1Api api = new CoreV1Api(client);
        api.getser
	}

    String getChaosMonkeySaToken() throws IOException {
        ApiClient client = Config.defaultClient();
        CoreV1Api api = new CoreV1Api(client);
    }
    
    void initEnvironment() throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        CoreV1Api api = new CoreV1Api(client);
        
        V1Namespace namespace1 = new V1Namespace();
        V1ObjectMeta metaNamespace1 = new V1ObjectMeta();
        metaNamespace1.setName("chaos-monkey");
        namespace1.setMetadata(metaNamespace1);
        api.createNamespace(namespace1, "false", "false", null, null);

        V1Namespace namespace2 = new V1Namespace();
        V1ObjectMeta metaNamespace2 = new V1ObjectMeta();
        metaNamespace2.setName("chaos-monkey");
        namespace1.setMetadata(metaNamespace2);
        api.createNamespace(namespace2, "false", "false", null, null);

        RbacAuthorizationV1Api rbacApi = new RbacAuthorizationV1Api(client);
        V1ClusterRole clusterRole = new V1ClusterRole();
        V1ObjectMeta clusterRoleMeta = new V1ObjectMeta();
        clusterRoleMeta.setName("chaos-monkey");
        clusterRole.setMetadata(clusterRoleMeta);

        List<String> rules = new ArrayList<>();
        rules.add("get");
        rules.add("list");
        rules.add("delete");

        V1PolicyRule policyRule = new V1PolicyRule().addApiGroupsItem("").addResourcesItem("pods").verbs(rules);å
        
    }


}
