package by.java.enterprise.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MockPrototypeService {
    public void test() {
        System.out.println("Test");
    }
}
