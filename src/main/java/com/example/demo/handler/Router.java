package com.example.demo.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class Router {

    @Autowired
    private Handler handler;

    @Bean
    public RouterFunction<ServerResponse> routes2() {
        return RouterFunctions.route()
                .GET("/v1/employees/{id}", accept(APPLICATION_JSON), handler::getEmployees)
                .POST("/v1/employees", accept(APPLICATION_JSON), handler::createEmployee)
                .build();
    }
}
