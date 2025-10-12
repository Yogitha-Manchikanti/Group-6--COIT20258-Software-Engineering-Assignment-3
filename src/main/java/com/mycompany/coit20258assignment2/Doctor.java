package com.mycompany.coit20258assignment2;

public class Doctor extends User {
    private static final long serialVersionUID = 1L;

    private String specialization;

    public Doctor(String id, String name, String email, String username, String password, String specialization) {
        super(id, name, email, username, password);
        this.specialization = specialization;
    }

    // legacy
    public Doctor(String id, String name, String username, String password, String specialization) {
        super(id, name, username, password);
        this.specialization = specialization;
    }

    public String getSpecialization() { return specialization; }
}
