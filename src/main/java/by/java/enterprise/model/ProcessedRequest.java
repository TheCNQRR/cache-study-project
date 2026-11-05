package by.java.enterprise.model;

import jakarta.persistence.Id;

import java.time.LocalDateTime;

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
