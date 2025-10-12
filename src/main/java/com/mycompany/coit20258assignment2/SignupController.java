package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SignupController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passField;
    @FXML private PasswordField confirmField;
    @FXML private Label message;

    private final ClientService clientService = ClientService.getInstance();

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
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            if (!pass.equals(confirm)) {
                message.setText("Passwords do not match.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            // Try to signup - connection will be established automatically
            ClientService.SignupResult result = clientService.signup(name, email, user, pass, "PATIENT", null);
            
            if (result.isSuccess()) {
                message.setText("✅ " + result.getMessage());
                message.setStyle("-fx-text-fill: green;");
                // Clear fields
                nameField.clear();
                emailField.clear();
                usernameField.clear();
                passField.clear();
                confirmField.clear();
            } else {
                message.setText("❌ " + result.getMessage());
                message.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML public void onBack() { SceneNavigator.getInstance().goToLogin(); }
}

