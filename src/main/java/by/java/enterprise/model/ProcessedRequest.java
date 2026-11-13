package by.java.enterprise.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_request")
public class ProcessedRequest {
    @Id
    private String requestId;
    private LocalDateTime createdAt;
    private String result;

    public ProcessedRequest() {
    }

    public ProcessedRequest(String requestId, LocalDateTime createdAt, String result) {
        this.requestId = requestId;
        this.createdAt = createdAt;
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
