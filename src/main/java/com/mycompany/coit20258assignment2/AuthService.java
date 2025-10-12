package com.mycompany.coit20258assignment2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Authentication service: login by email OR username.
 * Also seeds a few demo users (with emails) on first run.
 */
public class AuthService {

    private final DataStore store;

    public AuthService(DataStore store) {
        this.store = store;
        ensureSeedUsers();
    }

    /** Login by email OR username + password. */
    public Optional<User> login(String identifier, String password) {
        return store.getUsers().stream()
                .filter(u -> (identifier.equalsIgnoreCase(nz(u.getEmail()))
                           || identifier.equalsIgnoreCase(nz(u.getUsername())))
                        && password.equals(u.getPassword()))
                .findFirst();
    }

    private String nz(String s) { return s == null ? "" : s; }

    /** Helper: does a username already exist in the list? */
    private boolean hasUsername(List<User> users, String uname) {
        String u = nz(uname);
        return users.stream().anyMatch(x -> u.equalsIgnoreCase(nz(x.getUsername())));
    }

    /** Ensure demo users exist (with emails) so the app is usable on first run. */
    private void ensureSeedUsers() {
        List<User> users = new ArrayList<>(store.getUsers());
        boolean changed = false;

        if (!hasUsername(users, "patient")) {
            users.add(new Patient("P1", "Patient One", "patient@example.com", "patient", "pass"));
            changed = true;
        }
        if (!hasUsername(users, "doctor")) {
            users.add(new Doctor("D0", "Dr. Demo", "doctor@example.com", "doctor", "pass", "General Practice"));
            changed = true;
        }
        if (!hasUsername(users, "admin")) {
            users.add(new Administrator("A1", "Admin User", "admin@example.com", "admin", "pass"));
            changed = true;
        }
        if (!hasUsername(users, "drjames")) {
            users.add(new Doctor("D1", "Dr. James Smith", "drjames@clinic.com", "drjames", "pass", "Cardiology"));
            changed = true;
        }
        if (!hasUsername(users, "dranita")) {
            users.add(new Doctor("D2", "Dr. Anita Rao", "dranita@clinic.com", "dranita", "pass", "Pediatrics"));
            changed = true;
        }
        if (!hasUsername(users, "drmarcus")) {
            users.add(new Doctor("D3", "Dr. Marcus Lee", "drmarcus@clinic.com", "drmarcus", "pass", "Dermatology"));
            changed = true;
        }

        if (changed) {
            store.saveUsers(users);
        }
    }
}
