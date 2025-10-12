package com.mycompany.coit20258assignment2.common;

import com.mycompany.coit20258assignment2.User;

/**
 * Login response from server to client
 */
public class LoginResponse extends BaseResponse {
    private User user;
    private String sessionToken;
    
    public LoginResponse(String requestId, boolean success, String message, User user, String sessionToken) {
        super(requestId, "LOGIN_RESPONSE", success, message);
        this.user = user;
        this.sessionToken = sessionToken;
    }
    
    public User getUser() { return user; }
    public String getSessionToken() { return sessionToken; }
    
    public void setUser(User user) { this.user = user; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
}