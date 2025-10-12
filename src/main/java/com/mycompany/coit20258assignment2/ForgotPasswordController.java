package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for "Forgot Password" page.
 * Lets user reset password to a temporary default.
 */
public class ForgotPasswordController {

    @FXML private TextField usernameField;
    @FXML private Label message;

    private final ClientService clientService = ClientService.getInstance();

    /** Handle password reset */
    @FXML
    public void onReset() {
        try {
            String identifier = usernameField.getText().trim();
            
            if (identifier.isEmpty()) {
                message.setText("Please enter your username or email.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            // Try to reset password - connection will be established automatically
            ClientService.ResetPasswordResult result = clientService.resetPassword(identifier);
            
            if (result.isSuccess()) {
                message.setText("✅ " + result.getMessage());
                message.setStyle("-fx-text-fill: green;");
                usernameField.clear();
            } else {
                message.setText("❌ " + result.getMessage());
                message.setStyle("-fx-text-fill: red;");
            }
            
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML public void onBack() {
        SceneNavigator.getInstance().goToLogin();
    }
}
