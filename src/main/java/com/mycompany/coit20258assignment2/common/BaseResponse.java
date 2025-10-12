package com.mycompany.coit20258assignment2.common;

import java.io.Serializable;

/**
 * Base class for all server responses to clients
 * Server Lead responsibility: Define response protocol
 */
public abstract class BaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String requestId;
    private String responseType;
    private boolean success;
    private String message;
    private long timestamp;
    private Object data;
    
    public BaseResponse(String requestId, String responseType, boolean success, String message) {
        this.requestId = requestId;
        this.responseType = responseType;
        this.success = success;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and setters
    public String getRequestId() { return requestId; }
    public String getResponseType() { return responseType; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public Object getData() { return data; }
    
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setData(Object data) { this.data = data; }
}