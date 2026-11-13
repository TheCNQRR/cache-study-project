package by.java.enterprise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.graphql.autoconfigure.data.GraphQlQueryByExampleAutoConfiguration;

@SpringBootApplication(excludeName = "org.springframework.boot.graphql.autoconfigure.data.GraphQlQueryByExampleAutoConfiguration")
@ConfigurationPropertiesScan
public class CacheApplication {
    public static void main(String[] args) {
        SpringApplication.run(CacheApplication.class, args);
    }
}
