package com.springboot.service;

import com.springboot.model.Employee;
import com.springboot.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Retrieves all employees from the database.
     * @return A list of all employees.
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Retrieves an employee by their ID.
     * @param id The ID of the employee to retrieve.
     * @return An Optional containing the employee if found, otherwise empty.
     */
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    /**
     * Saves a new employee or updates an existing employee in the database.
     * @param employee The employee object to be saved.
     * @return The saved employee object.
     */
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    /**
     * Deletes an employee by ID.
     * @param id The ID of the employee to delete.
     */
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
