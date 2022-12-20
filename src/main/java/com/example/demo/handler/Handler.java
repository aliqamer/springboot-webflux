package com.example.demo.handler;

import com.example.demo.common.logging.ContextHelper;
import com.example.demo.common.logging.LogStashLogger;
import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.example.demo.common.logging.LogStashLogger.*;
import static com.example.demo.common.logging.LogStashLogger.logOnNext;

@Component
public class Handler {

    @Autowired
    private EmployeeService service;

    Logger log = LoggerFactory.getLogger(Handler.class);

    public Mono<ServerResponse> getEmployees(ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .doOnEach(logOnNext(req -> log.info("Request id: {}", req.pathVariable("id"))))
                .flatMap(request -> {
//                    validator.validate(request);
                    return service.getEmployeeById(serverRequest.pathVariable("id"));
                })
                .doOnEach(logOnNext(res -> log.info("employee response: {}", res)))
                .doOnEach(logOnError(ex -> log.error("Excepton: {}", ex)))
                .flatMap(res -> ServerResponse.status(HttpStatus.OK).body(Mono.just(res), Employee.class));
    }

    public Mono<ServerResponse> createEmployee(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(Employee.class)
                .flatMap(dto -> {
                     return createEmployeeWithContext(dto, serverRequest);
                });
    }

    private Mono<? extends ServerResponse> createEmployeeWithContext(Employee dto, ServerRequest serverRequest) {

        return Mono.just(dto)
                .doOnEach(logOnNext(req -> log.info("Request received: {}", req.toString())))
                .flatMap(employeedto -> {
                    return service.createEmployee(employeedto);
                })
                .doOnEach(logOnError(ex -> log.error("Error occurred: {}", ex)))
                .doOnEach(logOnNext(res -> log.info("Response: {}", res)))
                .flatMap(res -> ServerResponse.status(HttpStatus.CREATED)
                        .header("correlationId", "123")
                        .body(Mono.just(res), String.class))
                .contextWrite(ctx -> ContextHelper.addRequestBodyToContext(dto, ctx));

    }
}
