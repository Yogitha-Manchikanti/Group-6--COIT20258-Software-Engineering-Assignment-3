package com.mycompany.coit20258assignment2;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SignupController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;     // NEW
    @FXML private TextField usernameField;
    @FXML private PasswordField passField;
    @FXML private PasswordField confirmField;
    @FXML private Label message;

    private final DataStore store = new DataStore("data");

    @FXML
    public void onSignup() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String user = usernameField.getText().trim();
            String pass = passField.getText();
            String confirm = confirmField.getText();

            if (name.isEmpty() || email.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                message.setText("All fields are required.");
                return;
            }
            if (!pass.equals(confirm)) {
                message.setText("Passwords do not match.");
                return;
            }
            boolean emailExists = store.getUsers().stream()
                    .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
            if (emailExists) { message.setText("Email already registered."); return; }

            boolean userExists = store.getUsers().stream()
                    .anyMatch(u -> user.equalsIgnoreCase(u.getUsername()));
            if (userExists) { message.setText("Username already exists."); return; }

            String id = "P" + System.currentTimeMillis();
            var users = store.getUsers();
            users.add(new Patient(id, name, email, user, pass));
            store.saveUsers(users);

            message.setText("Account created! Please login with your email.");
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML public void onBack() { SceneNavigator.getInstance().goToLogin(); }
}
