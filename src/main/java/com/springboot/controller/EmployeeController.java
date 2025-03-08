package com.springboot.controller;

import com.springboot.model.Employee;
import com.springboot.service.EmployeeService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;
/**
 * API endpoint to trigger the Spring Batch job that imports employee data from a CSV file.
 * @return A success message if the job starts, otherwise an error message.
 */
    @GetMapping("/import")
    public String importCsvToDb() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())// Ensures job uniqueness
                    .toJobParameters();
            jobLauncher.run(job, jobParameters); // Runs the batch job
            return "Batch job started";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    /**
     * Fetches all employees from the database.
     * @return A list of all employees.
     */
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
    /**
     * Fetches a specific employee by ID.
     * @param id Employee ID.
     * @return The employee details if found, otherwise null.
     */
    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable Long id) {
        return employeeService.getEmployeeById(id).orElse(null);
    }
    /**
     * Adds a new employee to the database.
     * @param employee Employee details received from the frontend.
     * @return The saved employee object.
     */
    @PostMapping
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeService.saveEmployee(employee);
    }
    /**
     * Updates an existing employee in the database.
     * @param id Employee ID to update.
     * @param employee Updated employee details.
     * @return The updated employee object.
     */
    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        employee.setId(id);// Ensure correct ID assignment
        return employeeService.saveEmployee(employee);
    }

    /**
     * Deletes an employee by ID.
     * @param id Employee ID to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
}