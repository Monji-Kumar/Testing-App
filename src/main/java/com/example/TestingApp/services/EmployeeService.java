package com.example.TestingApp.services;


import com.example.TestingApp.dto.EmployeeDto;

public interface EmployeeService {

    com.example.TestingApp.dto.EmployeeDto getEmployeeById(Long id);
    EmployeeDto createNewEmployee(EmployeeDto employeeDto);
    EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto);
    void deleteEmployee(Long id);
}
