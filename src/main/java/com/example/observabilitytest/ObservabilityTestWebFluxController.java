package com.example.observabilitytest;


import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import reactor.core.publisher.Mono;

@RestController
public class ObservabilityTestWebFluxController {

	private static final Logger log = LoggerFactory.getLogger(ObservabilityTestWebFluxController.class);

	private final ObjectProvider<MyService> myService;

	private final WebClient webClient;

	private final WireMockServer wireMockServer;

	public ObservabilityTestWebFluxController(ObjectProvider<MyService> myService, WebClient webClient,
			WireMockServer wireMockServer) {
		this.myService = myService;
		this.webClient = webClient;
		this.wireMockServer = wireMockServer;
	}

	@GetMapping("/foo")
	Mono<String> foo() {
		return myService.getIfUnique().bar();
	}

	@GetMapping("/test")
	Mono<String> test() {
		log.info("HELLO");
//		this.customizer.customize(restTemplate); // WTF
		return webClient.get().uri(URI.create(wireMockServer.baseUrl() + "/")).exchangeToMono(r -> {
			wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/")).withHeader("X-B3-TraceId",
					WireMock.matching(".*")));
			return r.bodyToMono(String.class);
		});
	}
}


@Component
@Lazy
class MyService {
	private static final Logger log = LoggerFactory.getLogger(MyService.class);

	MyService() {
		log.info("BOOOOOM");
	}

	Mono<String> bar() {
		log.info("Hello from service");
		return Mono
					.just("alsdjasd")
				.doOnNext(s -> {
					log.info("I'm here");
				});
	}
}