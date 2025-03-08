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

    @Bean
    public FlatFileItemReader<Employee> reader() {
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("src/main/resources/employees.csv"));
        reader.setLinesToSkip(1);

        reader.setLineMapper(new DefaultLineMapper<Employee>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("id", "firstName", "lastName", "email", "gender", "contact", "country", "dob");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
                setTargetType(Employee.class);
            }});
        }});
        reader.setLinesToSkip(1);
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<Employee> writer() {
        JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("INSERT INTO employees (id, first_name, last_name, email, gender, contact, country, dob) VALUES (:id, :firstName, :lastName, :email, :gender, :contact, :country, :dob)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Employee, Employee>chunk(5)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public Job importJob() {
        return jobBuilderFactory.get("importJob")
                .flow(step1())
                .end()
                .build();
    }
}
