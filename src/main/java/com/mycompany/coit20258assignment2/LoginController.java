package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;

import java.util.Optional;


public class LoginController {

    @FXML private TextField usernameField; // used for email OR username
    @FXML private PasswordField passwordField;
    @FXML private Label message;
    @FXML private CheckBox useServerCheckbox;

    private final AuthService auth = new AuthService(new DataStore("data"));
    private final ClientService clientService = ClientService.getInstance();

    @FXML private void initialize() { 
        if (message!=null) message.setText(""); 
        // Default to server mode if checkbox exists
        if (useServerCheckbox != null) {
            useServerCheckbox.setSelected(true);
        }
    }

    @FXML
    public void onLogin() {
        try {
            String id = usernameField.getText().trim(); // email or username
            String pw = passwordField.getText();
            if (id.isEmpty() || pw.isEmpty()) { 
                setMsg("Enter email/username and password.", true); 
                return; 
            }

            // ASSIGNMENT 3: Always use server authentication
            setMsg("Connecting to server...", false);
            ClientService.LoginResult result = clientService.login(id, pw);
            
            if (result.isSuccess()) {
                Session.set(result.getUser());
                setMsg("Login successful!", false);
                navigateToDashboard();
                return;
            } else {
                setMsg("Server: " + result.getMessage() + "\n\nMake sure THSServer is running!", true);
                return;
            }
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            setMsg("Login error: "+e.getMessage(), true); 
        }
    }
    
    private void navigateToDashboard() {
        if (Session.isPatient()) SceneNavigator.getInstance().goToPatientDashboard();
        else if (Session.isDoctor()) SceneNavigator.getInstance().goToDoctorDashboard();
        else SceneNavigator.getInstance().goToAdminDashboard();
    }

    @FXML public void onSignup()         { SceneNavigator.getInstance().goToSignup(); }
    @FXML public void onForgotPassword() { SceneNavigator.getInstance().goToForgotPassword(); }

    private void setMsg(String t, boolean err) {
        if (message!=null){ 
            message.setText(t); 
            message.setStyle(err?"-fx-text-fill:#cc0000;":"-fx-text-fill:#0a7a22;");
        }
    }
}
