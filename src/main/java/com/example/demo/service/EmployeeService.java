package com.example.demo.service;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Mono<Employee> getEmployeeById(String id) {

//        return Mono.just(new Employee(Integer.parseInt(id), "ali", "m", "pune",20));
        return Mono.fromCompletionStage(employeeRepository.getEmployeeById(id))
                .onErrorResume(NullPointerException.class,
                        throwable -> Mono.just(new Employee()));
    }

    public Mono<?> createEmployee(Employee dto) {
        return Mono.just(new Employee());
    }
}
