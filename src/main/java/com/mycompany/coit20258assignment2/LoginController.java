package com.mycompany.coit20258assignment2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

public class LoginController {

    @FXML private TextField usernameField; // used for email OR username
    @FXML private PasswordField passwordField;
    @FXML private Label message;

    private final AuthService auth = new AuthService(new DataStore("data"));

    @FXML private void initialize() { if (message!=null) message.setText(""); }

    @FXML
    public void onLogin() {
        try {
            String id = usernameField.getText().trim(); // email or username
            String pw = passwordField.getText();
            if (id.isEmpty() || pw.isEmpty()) { setMsg("Enter email/username and password.", true); return; }

            Optional<User> user = auth.login(id, pw);
            if (user.isEmpty()) { setMsg("Invalid credentials.", true); return; }

            Session.set(user.get());
            if (Session.isPatient()) SceneNavigator.getInstance().goToPatientDashboard();
            else if (Session.isDoctor()) SceneNavigator.getInstance().goToDoctorDashboard();
            else SceneNavigator.getInstance().goToAdminDashboard();
        } catch (Exception e) { e.printStackTrace(); setMsg("Login error: "+e.getMessage(), true); }
    }

    @FXML public void onSignup()         { SceneNavigator.getInstance().goToSignup(); }
    @FXML public void onForgotPassword() { SceneNavigator.getInstance().goToForgotPassword(); }

    private void setMsg(String t, boolean err) {
        if (message!=null){ message.setText(t); message.setStyle(err?"-fx-text-fill:#cc0000;":"-fx-text-fill:#0a7a22;");}
    }
}
