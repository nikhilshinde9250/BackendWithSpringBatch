package com.springboot.service;

import com.springboot.model.Employee;
import com.springboot.repository.EmployeeRepository;
//import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
//import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    EmployeeService employeeService ;

    @Test
    void getAllEmployeesShouldGetSuccessfully(){
        employeeService.getAllEmployees();
    }


    @Test
    void saveEmployeeShouldSaveSuccessfully(){
        Employee emp=new Employee();
        emp.setId(1L);
        emp.setFirstName("vivke");
        emp.setLastName("more");
        emp.setEmail("vivek@gmail.com");
        emp.setGender("mail");
        emp.setContact("90223456");
        emp.setCountry("india");
        emp.setDob("12/23/4556");
        when(employeeRepository.save(emp)).thenReturn(emp);
      Employee empAdded=  employeeService.saveEmployee(emp);
//        System.out.println("Unit test for saveEmployee");
        assertNotNull(empAdded);
        assertEquals(emp.getId(),empAdded.getId());
        System.out.println("test passes successfully");
    }

}
