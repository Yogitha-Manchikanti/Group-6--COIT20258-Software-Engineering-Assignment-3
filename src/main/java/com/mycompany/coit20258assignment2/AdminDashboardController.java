package com.mycompany.coit20258assignment2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML private Label welcome;

    @FXML
    public void initialize() {
        if (Session.get() != null && welcome != null) {
            welcome.setText("Welcome, " + Session.get().getName());
        }
    }


    @FXML 
    public void onViewAppointments() {
        SceneNavigator.getInstance().goToAdminAppointments();
    }

    @FXML 
    public void onLogout() {
        Session.logout();
        SceneNavigator.getInstance().goToLogin();
    }
}
