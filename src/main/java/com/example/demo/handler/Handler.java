package com.example.demo.handler;

import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class Handler {

    @Autowired
    private EmployeeService service;

    public Mono<ServerResponse> getEmployees(ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .flatMap(request -> {
//                    validator.validate(request);
                    return service.getEmployeeById(serverRequest.pathVariable("id"));
                })
//                .doOnEach(logOnNext())
                .flatMap(res -> ServerResponse.status(HttpStatus.OK).body(Mono.just(res), Employee.class));
    }

    public Mono<ServerResponse> createEmployee(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(Employee.class)
                .flatMap(dto -> {
                    return service.createEmployee(dto);
                })
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED).body(Mono.just("ok"), String.class));
//        return null;
    }
}
