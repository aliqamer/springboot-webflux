package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

/*
sample class
 */

@SpringBootApplication
public class DemoApplication {

	// client part start

	@Bean
	WebClient webClient(WebClient.Builder builder) {
		return builder.baseUrl("http://localhost:8080")
//				.filter(ExchangeFilterFunctions.basicAuthentication())
		.build();
	}

@RequiredArgsConstructor
@Component
//@Log4j2
class Client {

	private final WebClient webClient;
	private Logger log = LoggerFactory.getLogger(DemoApplication.class);

	@EventListener(ApplicationReadyEvent.class)
	public void ready() {

		var name = "Spring Fans";

		this.webClient
				.get()
				.uri("/greeting/{name}", name)
				.retrieve()
				.bodyToMono(GreetingResponse.class)
				.map(GreetingResponse::getMessage)
				.retryWhen(Retry.backoff(3, Duration.ofSeconds(3)))
				.onErrorMap(throwable -> new IllegalArgumentException("original exception was " + throwable.toString()+ " wrong! "))
				.onErrorResume(IllegalArgumentException.class, ex -> Mono.just(ex.toString()))
				.subscribe(gr -> log.info("Mono: "+ gr));

		/*this.webClient
				.get()
				.uri("/greetings/{name}", name)
				.retrieve()
				.bodyToFlux(GreetingResponse.class)
				.subscribe(gr -> log.info("Flux: "+ gr.getMessage()));*/
	}
}
// client part end

	@Bean
	RouterFunction<ServerResponse> routes(GreetingService greetingService) {

		return RouterFunctions.route()
//				.POST()
				.GET("/greetings/{name}",
						r -> ServerResponse.ok()
								.contentType(MediaType.TEXT_EVENT_STREAM)
				.body(greetingService.greetMany(new GreetingRequest(r.pathVariable("name"))), GreetingResponse.class))
				.GET("/greeting/{name}",
						r -> ServerResponse.ok()
								.body(greetingService.greetOnce(new GreetingRequest(r.pathVariable("name"))),
										GreetingResponse.class))
								.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

@Service
class GreetingService {

	private GreetingResponse greet(String name) {
		return new GreetingResponse("Hello " + name + " @ " + Instant.now());
	}

	Flux<GreetingResponse> greetMany(GreetingRequest request) {
		return Flux
				.fromStream(Stream.generate(() -> greet(request.getName())))
				.delayElements(Duration.ofSeconds(1));
	}

	Mono<GreetingResponse> greetOnce(GreetingRequest request) {
		return Mono.just(greet(request.getName()));
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingResponse {
	private String message;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingRequest {
	private String name;
}
