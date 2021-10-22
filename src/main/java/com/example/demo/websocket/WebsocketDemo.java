package com.example.demo.websocket;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootApplication
public class WebsocketDemo {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketDemo.class, args);
    }
}

@Log4j2
@Configuration
class GreetingWebSocketConfiguration {

    @Bean
    SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler wsh) {
        return new SimpleUrlHandlerMapping(Map.of("/ws/greetings", wsh), 10);
    }

    @Bean
    WebSocketHandler webSocketHandler(GreetingService greetingService) {

        return session -> {
            var receive =
                    session.receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .map(GreetingRequest::new)
                            .flatMap(greetingService::greet)
                            .map(GreetingResponse::getMessage)
                            .map(session::textMessage)
                            .doOnEach(signal -> log.info(signal.getType()))
                            .doFinally(signalType -> log.info("finally: "+signalType.name()));

            return session.send(receive);
        };

        /*return new WebSocketHandler() {
            @Override
            public Mono<Void> handle(WebSocketSession session) {

                Flux<WebSocketMessage> receive = session.receive();
                Flux<String> names = receive.map(WebSocketMessage::getPayloadAsText);
                Flux<GreetingRequest> requestFlux = names.map(GreetingRequest::new);
                Flux<GreetingResponse> greetingResponseFlux = requestFlux.flatMap(greetingService::greet);
                Flux<String> map = greetingResponseFlux.map(GreetingResponse::getMessage);
                Flux<WebSocketMessage> webSocketMessageFlux = map.map(session::textMessage);

                return session.send(webSocketMessageFlux);
            }
        };*/
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}



@Service
class GreetingService {

    Flux<GreetingResponse> greet(GreetingRequest request) {
        return Flux.
                fromStream(Stream.generate(() -> new GreetingResponse("Hello "+request.getName() + " @ "+ Instant.now())))
                .delayElements(Duration.ofSeconds(1));
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