package com.springboot.config;

import com.springboot.model.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    /*
    * FlatFileItemReader reads data from a csv file and maps it to the employee object
    * its skips the first line header and maps csv columns to employee fields
    * */
    @Bean
    public FlatFileItemReader<Employee> reader() {
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("src/main/resources/employees.csv"));
        reader.setLinesToSkip(1);

        //define how to map each row in the csv file to an employee object
        reader.setLineMapper(new DefaultLineMapper<Employee>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("id", "firstName", "lastName", "email", "gender", "contact", "country", "dob");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
                setTargetType(Employee.class); //map fileds to employee class
            }});
        }});
        reader.setLinesToSkip(1);//skips the first row of header
        return reader;
    }
/*
JdbcBatchItemWriter  writes employee data  from the reader into  the database
Uses parameterized sql query to insert records into the employees table
* */
    @Bean
    public JdbcBatchItemWriter<Employee> writer() {
        JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);//set database connection
        writer.setSql("INSERT INTO employees (id, first_name, last_name, email, gender, contact, country, dob) VALUES (:id, :firstName, :lastName, :email, :gender, :contact, :country, :dob)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return writer;
    }

    /*
    * Defines a single step in the batch job that processes Employee records in chunks of 5
    * uses reader() to read data  and writer() to write data to the  database*/
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Employee, Employee>chunk(5)//processes data in chunks of 5
                .reader(reader())//Reads from csv
                .writer(writer())//write to database
                .build();
    }

/*
* Defines the batch job that consists of  step1
* the job executes step1 to read and write employee records*/
    @Bean
    public Job importJob() {
        return jobBuilderFactory.get("importJob")
                .flow(step1())//executes step1
                .end()//mark job completion
                .build();
    }
}
