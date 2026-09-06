package by.java.enterprise.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class MockSingletonService {
    private final ObjectProvider<MockPrototypeService> mockPrototypeServiceObjectProvider;
    private final Logger log = LoggerFactory.getLogger(MockSingletonService.class);


    public MockSingletonService(ObjectProvider<MockPrototypeService> mockPrototypeServiceObjectProvider) {
        this.mockPrototypeServiceObjectProvider = mockPrototypeServiceObjectProvider;
    }

    public void printMockServiceHash() {
        MockPrototypeService mockPrototypeService = mockPrototypeServiceObjectProvider.getObject();
        log.info("mock prototype hash: {}", System.identityHashCode(mockPrototypeService));
    }

    @PostConstruct
    public void init() {
        System.out.println("Хранилище готово (PostConstruct)");
    }
}
