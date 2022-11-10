package it.justinpolidori.ChaosMonkey;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ChaosMonkeyApplication implements CommandLineRunner {

	@Value("${chaos-monkey.timeoutInSeconds:30}")
	private Integer timeoutInSeconds;
	@Value("${chaos-monkey.gracePeriodInSeconds:30}")
	private Integer gracePeriodInSeconds;
	@Value("${chaos-monkey.labelSelector:#{null}}")
	private String labelSelector;

	private final CoreV1Api api;

	public static void main(String[] args) {
		SpringApplication.run(ChaosMonkeyApplication.class, args);
	}

	{
		try {
			ApiClient client = Config.defaultClient();
			api = new CoreV1Api(client);
		} catch (IOException e) {
			throw new RuntimeException("Unable to authenticate to the cluster:"+e);
		}
	}

	@Override
	public void run(String... args) throws Exception {
		Killer killer = new Killer(api);
		String hardcodedNamespace = "workloads";
		killer.killPod(hardcodedNamespace, timeoutInSeconds, labelSelector, gracePeriodInSeconds);
	}
}
