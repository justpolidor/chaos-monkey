package it.justinpolidori.ChaosMonkey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChaosMonkeyApplication implements CommandLineRunner {

	@Value("${chaos-monkey.timeoutInSeconds:30}")
	private Integer timeoutInSeconds;
	@Value("${chaos-monkey.gracePeriodInSeconds:30}")
	private Integer gracePeriodInSeconds;
	@Value("${chaos-monkey.labelSelector:#{null}}")
	private String labelSelector;

	public static void main(String[] args) {
		SpringApplication.run(ChaosMonkeyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Killer killer = new Killer();
		String hardcodedNamespace = "workloads";
		killer.killPod(hardcodedNamespace, timeoutInSeconds, labelSelector, gracePeriodInSeconds);
	}
}
