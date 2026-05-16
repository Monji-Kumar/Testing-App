package com.example.TestingApp.services.impl;

import com.example.TestingApp.TestContainerConfiguration;
import com.example.TestingApp.dto.EmployeeDto;
import com.example.TestingApp.entities.Employee;
import com.example.TestingApp.exceptions.ResourceNotFoundException;
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

        Mockito.verify(employeeRepository, Mockito.atLeast(1)).findById(id);
    }

    @Test
    void testGetEmployeeById_WhenEmployeeIsNotPresent_ThenThrowException() {
        //Arrage/Assign
        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        //Act //Assert
        Assertions.assertThatThrownBy(() -> employeeServiceImpl.getEmployeeById(1l))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");
        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).findById(1l);

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

    @Test
    void createNewEmployee_WhenEmployeeNotValid_ThenThrowException() {
        //Assign/Arage
        Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(List.of(employee));
        //Act //Assert
        Assertions.assertThatThrownBy(() -> employeeServiceImpl.createNewEmployee(employeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: " + employee.getEmail());
        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).findByEmail(employee.getEmail());


    }

    @Test
    void update_WhenEmployeeIsNotPresent_ThenThrowException() {
        //Assign/Arage
        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        //Act //Assert
        Assertions.assertThatThrownBy(() -> employeeServiceImpl.updateEmployee(2l, employeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 2");
        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).findById(2l);
        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any(Employee.class));


    }

    @Test
    void update_WhenEmployeeIsPresentButDifferentEmail_ThenThrowException() {
        //Assign/Arage
        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(employee));
        //Act //Assert
        employeeDto.setEmail("notpersent@gmail.com");
        Assertions.assertThatThrownBy(() -> employeeServiceImpl.updateEmployee(1l, employeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");
        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).findById(1l);
//        Mockito.verify(employeeRepository, Mockito.never()).findByEmail(2l);
        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any(Employee.class));
    }

    @Test
    void update_WhenEmployeeIsPresentAndValid_ThenReturnEmployeeDto() {
        //Assign/Arage
        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);
        //Act

        EmployeeDto mockEmployeeDto = employeeServiceImpl.updateEmployee(1l, employeeDto);
        // Assert
        Assertions.assertThat(mockEmployeeDto.getEmail()).isEqualTo(employeeDto.getEmail());
        Assertions.assertThat(mockEmployeeDto.getId()).isEqualTo(employeeDto.getId());

        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).findById(1l);
        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).save(Mockito.any(Employee.class));
    }

    @Test
    void deleteEmployee_WhenEmployeeNotPresent_ThenThrowException() {
        Mockito.when(employeeRepository.existsById(2l)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> employeeServiceImpl.deleteEmployee(2l))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 2");

        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).existsById(2l);
    }

    @Test
    void deleteEmployee_WhenEmployeeIsPresent_ThenDeleteEmployee() {
        Mockito.when(employeeRepository.existsById(1l)).thenReturn(true);

        employeeServiceImpl.deleteEmployee(1l);

        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).existsById(1l);
        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).deleteById(1l);
    }
}