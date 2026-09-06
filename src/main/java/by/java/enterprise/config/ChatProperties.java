package by.java.enterprise.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chat")
public record ChatProperties(
        int defaultPageLimit
) {
}
