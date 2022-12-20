package com.example.demo.model;

import lombok.*;

@Getter
@Setter
//@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Employee {

    private Integer id;
    private String name;
    private String gender;
    private String address;
    private Integer age;
}
