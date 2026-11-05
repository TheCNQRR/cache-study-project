package by.java.enterprise.repository;

import by.java.enterprise.model.ProcessedRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedRequestRepository extends JpaRepository<ProcessedRequest, String> {
}
