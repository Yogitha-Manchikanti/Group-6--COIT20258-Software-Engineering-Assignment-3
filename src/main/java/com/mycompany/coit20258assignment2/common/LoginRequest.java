package com.mycompany.coit20258assignment2.common;

/**
 * Login request from client to server
 */
public class LoginRequest extends BaseRequest {
    private String identifier; // username or email
    private String password;
    
    public LoginRequest(String identifier, String password) {
        super("LOGIN", null);
        this.identifier = identifier;
        this.password = password;
    }
    
    public String getIdentifier() { return identifier; }
    public String getPassword() { return password; }
    
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public void setPassword(String password) { this.password = password; }
}