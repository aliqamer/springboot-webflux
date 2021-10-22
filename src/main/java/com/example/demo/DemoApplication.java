package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

//@SpringBootApplication
public class DemoApplication {

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
		/*return RouterFunctions.route()
				.GET("/greeting/{name}", new HandlerFunction<ServerResponse>() {
					@Override
					public Mono<ServerResponse> handle(ServerRequest request) {
						GreetingRequest greetingRequest = new GreetingRequest(request.pathVariable("name"));
						Mono<GreetingResponse> greet = greetingService.greet(greetingRequest);
						return ServerResponse.ok().body(greet, GreetingResponse.class);
					}
				})
				.build();*/
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}



/*
@RestController
@RequiredArgsConstructor
class GreetingRestController {

	private final GreetingService greetingService;

	@GetMapping("/greeting/{name}")
	Mono<GreetingResponse> greet(@PathVariable String name) {
		return this.greetingService.greet(new GreetingRequest(name));
	}
}*/

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
