package com.example.demo.model;

import lombok.*;

@Getter
@Setter
//@RequiredArgsConstructor
//@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Employee implements IEmployee {

    private Integer id;
    private String name;
    private String gender;
    private String address;
    private Integer age;
    private String correlationId;

    public Employee() {
        super();
    }

    @Override
    public String getCorrelationIdFromRequest() {
        return this.correlationId;
    }
}
