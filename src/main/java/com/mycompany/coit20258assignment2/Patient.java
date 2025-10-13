package com.mycompany.coit20258assignment2;

public class Patient extends User {
    private static final long serialVersionUID = 1L;

    public Patient(String id, String name, String email, String username, String password) {
        super(id, name, email, username, password);
        setUserType(UserType.PATIENT);
    }

    public Patient(String id, String name, String username, String password) {
        super(id, name, username, password);
        setUserType(UserType.PATIENT);
    }
}
