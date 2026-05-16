package com.example.TestingApp.repositories;

import com.example.TestingApp.TestContainerConfiguration;
import com.example.TestingApp.entities.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

//import org.springframework.boot.test.autoconfigure.orm.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestContainerConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee(null,"monjikumar00@gmail.com","Manoj Kumar", 120000l);
    }

    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmployee() {
        //Arrange, Given

        //First save the employee to set up the test
        employeeRepository.save(employee);

        //Act, When
        List<Employee> employeeList = employeeRepository.findByEmail(employee.getEmail());

        //Assert, Then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isNotEmpty();
        assertThat(employeeList.get(0).getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyEmployeeList() {
        //Arrange, Given

        String email = "not_present@gmail.com";

        //Act, When
        List<Employee> employeeList = employeeRepository.findByEmail(email);

        //Assert, Then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isEmpty();
    }
}