package by.java.enterprise.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanCountRunner implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(BeanCountRunner.class);
    private final ApplicationContext applicationContext;

    public BeanCountRunner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Registered bean count: {}", applicationContext.getBeanDefinitionCount());
    }
}
