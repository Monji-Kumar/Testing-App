package com.example.TestingApp.services.impl;

import com.example.TestingApp.TestContainerConfiguration;
import com.example.TestingApp.dto.EmployeeDto;
import com.example.TestingApp.entities.Employee;
import com.example.TestingApp.repositories.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ImportTestcontainers(TestContainerConfiguration.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeServiceImpl;

    private Employee employee;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp(){
        long id = 1l;
        employee = new Employee(id, "monjikumar00@gmail.com", "Manoj Kumar", 120000l);
        employeeDto = modelMapper.map(employee, EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_WhenEmployeeIdIsPresent_ThenReturnEmployeeDto() {
        //Assign
        long id = employee.getId();
        Employee employee = new Employee(id, "monjikumar00@gmail.com", "Manoj Kumar", 120000l);
        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee)); //stubbing here
        //Act

        EmployeeDto employeeDto = employeeServiceImpl.getEmployeeById(id);

        //Assert
        Assertions.assertThat(employeeDto.getId()).isEqualTo(id);
        Assertions.assertThat(employeeDto.getEmail()).isEqualTo(employee.getEmail());

        Mockito.verify(employeeRepository, Mockito.atLeast(2)).findById(id);
    }

    @Test
    void createNewEmployee_WhenValidEmployee_ThenCreateNewEmployee() {
        //Assign
        Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(List.of());
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);

        //Act
        EmployeeDto dto = employeeServiceImpl.createNewEmployee(employeeDto);

        //assert

        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getEmail()).isEqualTo(employeeDto.getEmail());
        Mockito.verify(employeeRepository, Mockito.atLeast(1)).save(Mockito.any(Employee.class));

        ArgumentCaptor<Employee> argumentCaptor = ArgumentCaptor.forClass(Employee.class);
        Mockito.verify(employeeRepository).save(argumentCaptor.capture());

        Employee capturedEmployee = argumentCaptor.getValue();

        Assertions.assertThat(capturedEmployee.getEmail()).isEqualTo(employee.getEmail());
    }
}