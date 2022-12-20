package com.example.demo.repository;

import com.example.demo.model.Employee;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Repository
public class EmployeeRepository {

    public CompletionStage<Employee> getEmployeeById(String id) {
        return CompletableFuture.supplyAsync(() -> new Employee(Integer.parseInt(id), "ali", "m", "pune", 25, "123"));
    }
}
