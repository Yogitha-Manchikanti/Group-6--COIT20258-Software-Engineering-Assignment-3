package com.mycompany.coit20258assignment2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DoctorDashboardController {

    @FXML private Label welcome;

    @FXML
    public void initialize() {
        if (Session.get() != null && welcome != null) {
            welcome.setText("Welcome, " + Session.get().getName());
        }
    }

    /* ==== Navigation actions wired from doctor_dashboard.fxml ==== */
    @FXML public void openBookingEditor()      { SceneNavigator.getInstance().goToStaffBookingEditor(); }
    @FXML public void openDiagnosisForm()      { SceneNavigator.getInstance().goToDiagnosisForm(); }
    @FXML public void openReferralForm()       { SceneNavigator.getInstance().goToReferralForm(); }
    @FXML public void openPrescriptionReview() { SceneNavigator.getInstance().goToPrescriptionReview(); }
    @FXML public void openMyData()             { SceneNavigator.getInstance().goToMyDataView(); }

    @FXML
    public void onLogout() {
        Session.logout();
        SceneNavigator.getInstance().goToLogin();
    }
}
