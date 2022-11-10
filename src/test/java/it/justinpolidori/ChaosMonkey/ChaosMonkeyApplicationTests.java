package it.justinpolidori.ChaosMonkey;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@SpringBootTest(
        properties = {"SPR_PROFILE=test"},
        classes = ChaosMonkeyApplicationTests.class
)
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CommandLineRunner.class))
class ChaosMonkeyApplicationTests {

   @Autowired
   Killer killer;
   @Test
   public void wip() throws IOException, ApiException {
   }

	@Test
	void contextLoads() {
	}


}
