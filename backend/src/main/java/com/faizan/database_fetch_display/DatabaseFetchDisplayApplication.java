package com.faizan.database_fetch_display;

import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication
@EnableMongoRepositories()
public class DatabaseFetchDisplayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseFetchDisplayApplication.class, args);
    }
}
