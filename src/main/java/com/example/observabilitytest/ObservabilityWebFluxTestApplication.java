package com.example.observabilitytest;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

@SpringBootApplication
public class ObservabilityWebFluxTestApplication {

	public static void main(String[] args) throws IOException {
		bufferingStartup(args);
	}

	@Bean(destroyMethod = "stop")
	WireMockServer wireMockServer() {
		WireMockServer wireMockServer =  new WireMockServer(0);
		wireMockServer.start();
		wireMockServer.givenThat(WireMock.get(WireMock.anyUrl()).willReturn(WireMock.aResponse().withStatus(200)));
		return wireMockServer;
	}

	@Bean
	WebClient restTemplate() {
		return WebClient.create();
	}

	private static void bufferingStartup(String[] args) {
		new SpringApplicationBuilder(ObservabilityWebFluxTestApplication.class)
				.applicationStartup(new BufferingApplicationStartup(10_000))
				.run(args);
	}

	@Bean
	CommandLineRunner myCommandLineRunner() {
		return args -> {
			/*String object = restTemplate.getForObject("https://httpbin.org/headers", String.class);
			if (!object.contains("B3")) {
				throw new IllegalStateException("No B3 header propagated");
			}*/
		};
	}

}
