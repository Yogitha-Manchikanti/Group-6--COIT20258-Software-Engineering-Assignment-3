package com.mycompany.coit20258assignment2.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all client-server communication requests
 * Server Lead responsibility: Define communication protocol
 */
public abstract class BaseRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String requestId;
    private String requestType;
    private String userId;
    private long timestamp;
    protected Map<String, Object> data = new HashMap<>();
    
    public BaseRequest(String requestType, String userId) {
        this.requestType = requestType;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
        this.requestId = generateRequestId();
    }
    
    private String generateRequestId() {
        return "REQ_" + System.currentTimeMillis() + "_" + Math.random();
    }
    
    // Getters and setters
    public String getRequestId() { return requestId; }
    public String getRequestType() { return requestType; }
    public String getUserId() { return userId; }
    public long getTimestamp() { return timestamp; }
    public Map<String, Object> getData() { return data; }
    
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public void addData(String key, Object value) {
        data.put(key, value);
    }
}