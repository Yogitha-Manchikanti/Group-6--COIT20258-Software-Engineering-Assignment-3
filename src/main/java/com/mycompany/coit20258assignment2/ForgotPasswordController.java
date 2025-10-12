package com.mycompany.coit20258assignment2;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for "Forgot Password" page.
 * Lets user reset password to a temporary default.
 */
public class ForgotPasswordController {

    @FXML private TextField usernameField;
    @FXML private Label message;

    private final DataStore store = new DataStore("data");

    /** Handle password reset */
    @FXML
    public void onReset() {
        try {
            String uname = usernameField.getText().trim();
            var u = store.getUsers().stream()
                    .filter(x -> x.getUsername().equalsIgnoreCase(uname))
                    .findFirst();

            if (u.isEmpty()) {
                message.setText("User not found.");
                return;
            }

            String tempPass = "reset123";
            u.get().setPassword(tempPass);
            store.saveUsers(store.getUsers());
            message.setText("Temporary password: reset123 (use to login)");
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML public void onBack() {
        SceneNavigator.getInstance().goToLogin();
    }
}
