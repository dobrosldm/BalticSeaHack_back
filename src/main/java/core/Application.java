package core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
// @EnableSwagger2
// @EnableMongoRepositories
// @ComponentScan(basePackages = "repos")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}