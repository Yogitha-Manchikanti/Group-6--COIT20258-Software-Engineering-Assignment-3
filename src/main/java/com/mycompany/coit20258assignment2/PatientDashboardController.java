package com.mycompany.coit20258assignment2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PatientDashboardController {

    @FXML private Label welcome;

    @FXML
    public void initialize() {
        if (Session.get() != null && welcome != null) {
            welcome.setText("Welcome, " + Session.get().getName());
        }
    }

    @FXML public void openBookAppointment()  { SceneNavigator.getInstance().goToAppointmentForm(); }
    @FXML public void openRecordVitals()     { SceneNavigator.getInstance().goToVitalsForm(); }
    @FXML public void openRequestRx()        { SceneNavigator.getInstance().goToPrescriptionForm(); }
    @FXML public void openMyData()           { SceneNavigator.getInstance().goToMyDataView(); }
    @FXML public void openHealthTips()       { SceneNavigator.getInstance().goToHealthTips(); }

    @FXML public void onLogout() {
        Session.logout();
        SceneNavigator.getInstance().goToLogin();
    }
}
