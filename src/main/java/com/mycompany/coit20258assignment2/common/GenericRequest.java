package com.mycompany.coit20258assignment2.common;

public class GenericRequest extends BaseRequest {
    
    public GenericRequest(String requestType, String userId) {
        super(requestType, userId);
    }
    
    public void addData(String key, Object value) {
        super.addData(key, value);
    }
}
