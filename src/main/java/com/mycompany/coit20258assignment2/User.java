package com.mycompany.coit20258assignment2;

import java.io.Serializable;

/** Base class for all users. */
public class User implements Serializable {
    private static final long serialVersionUID = 2L;

    private String id;
    private String name;
    private String email;     // NEW
    private String username;  // kept for backward compat
    private String password;

    public User(String id, String name, String email, String username, String password) {
        this.id = id; this.name = name; this.email = email;
        this.username = username; this.password = password;
    }

    // legacy ctor support
    public User(String id, String name, String username, String password) {
        this(id, name, null, username, password);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    @Override public String toString() { return name; }
}
