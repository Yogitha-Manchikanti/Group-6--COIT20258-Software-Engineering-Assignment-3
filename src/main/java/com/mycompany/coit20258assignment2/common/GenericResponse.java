package com.mycompany.coit20258assignment2.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic response class for flexible server-client communication
 * Server Lead responsibility: Communication protocol
 */
public class GenericResponse extends BaseResponse {
    private Map<String, Object> data;
    
    public GenericResponse(String requestId, String responseType, boolean success, String message) {
        super(requestId, responseType, success, message);
        this.data = new HashMap<>();
    }
    
    public void addData(String key, Object value) {
        data.put(key, value);
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public Object getData(String key) {
        return data.get(key);
    }
}
