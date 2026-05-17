package com.example.TestingApp.controllers;

import com.example.TestingApp.TestContainerConfiguration;
import com.example.TestingApp.dto.EmployeeDto;
import com.example.TestingApp.entities.Employee;
import com.example.TestingApp.repositories.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

class EmployeeControllerTestIT extends AbstractIntegrationTestClass {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Employee employee;

    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        employee = new Employee(null, "monjikkumar00@gmail.com", "Manoj Kumar", 120000l);
    }

    @AfterEach
    void destroy() {
        employeeRepository.deleteAll();
    }

    @Test
    void testGetEmployeeById_WhenEmployeeIsPresent_ThenReturnEmployeeDto() {
       Employee savedEmployee = employeeRepository.save(employee);
       employeeDto = modelMapper.map(savedEmployee, EmployeeDto.class);
       webTestClient.get().uri("/employees/{id}", savedEmployee.getId())
               .exchange()
               .expectStatus().isOk()
               .expectBody(EmployeeDto.class)
               .isEqualTo(employeeDto)
               .value(employeeDto1 -> {
                   assert employeeDto1 != null;
                   Assertions.assertThat(employeeDto1.getEmail()).isEqualTo(employeeDto.getEmail());
                   Assertions.assertThat(employeeDto1.getId()).isEqualTo(employeeDto.getId());
               });
    }

    @Test
    void testGetEmployeeById_WhenEmployeeIsNotPresent_ThenExpectException() {
        Employee savedEmployee = employeeRepository.save(employee);
        employeeDto = modelMapper.map(savedEmployee, EmployeeDto.class);
        webTestClient.get().uri("/employees/{id}", 2)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(EmployeeDto.class);
    }

    @Test
    void testCreateNewEmployee_WhenEmployeeAlreadyExists_ThenExpectException() {
        Employee savedEmployee = employeeRepository.save(employee);
        employeeDto = modelMapper.map(savedEmployee, EmployeeDto.class);
        webTestClient.post().uri("/employees")
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testCreateNewEmployee_WhenEmployeeDoesNotExist_ThenReturnEmployeeDto() {
        employeeDto = modelMapper.map(employee, EmployeeDto.class);
        webTestClient.post().uri("/employees")
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().isCreated()

                //Serializes the body into our DTO using Jackson
//                .expectBody(EmployeeDto.class)
//                .value(employeeDto1 -> {
//                    assert employeeDto1 != null;
//                    Assertions.assertThat(employeeDto1.getEmail()).isEqualTo(employeeDto.getEmail());
//                });
                .expectBody()
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());
    }

    @Test
    void testUpdateNewEmployee_WhenEmployeeDoesNotExists_ThenExpectException () {
        Employee savedEmployee = employeeRepository.save(employee);
        employeeDto = modelMapper.map(savedEmployee, EmployeeDto.class);
        employeeDto.setName("MONJI KUMAR");
        webTestClient.put().uri("/employees/{id}", 2)
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateNewEmployee_WhenEmployeeExistsButWeUpdatesTheEmail_ThenExpectException () {
        Employee savedEmployee = employeeRepository.save(employee);
        employeeDto = modelMapper.map(savedEmployee, EmployeeDto.class);
        employeeDto.setName("MONJI KUMAR");
        employeeDto.setEmail("monji@gmail.com");
        webTestClient.put().uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateNewEmployee_WhenValidEmployee_ThenReturnEmployeeDto () {
        Employee savedEmployee = employeeRepository.save(employee);
        employeeDto = modelMapper.map(savedEmployee, EmployeeDto.class);
        employeeDto.setName("MONJI KUMAR");
        webTestClient.put().uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(employeeDto.getName());
    }

    @Test
    void testDeleteEmployee_WhenEmployeeDoesNotExist_ThenExpectException () {
//        Employee savedEmployee = employeeRepository.save(employee);
//        employeeDto = modelMapper.map(savedEmployee, EmployeeDto.class);
//        employeeDto.setName("MONJI KUMAR");
        webTestClient.delete().uri("/employees/{id}", 1)
//                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_WhenValidEmployee_ThenDeleted () {
        Employee savedEmployee = employeeRepository.save(employee);
        webTestClient.delete().uri("/employees/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }
}