package com.mycompany.coit20258assignment2;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Patient records their vital signs.
 */
public class VitalsFormController {

    @FXML private TextField pulseField;
    @FXML private TextField tempField;
    @FXML private TextField respField;
    @FXML private TextField bpField;
    @FXML private Label message;

    private final VitalSignsService svc = new VitalSignsService(new DataStore("data"));

    @FXML
    public void onSubmit() {
        try {
            if (!Session.isPatient()) { message.setText("Please login as a patient."); return; }
            String pid = Session.id();

            int pulse = Integer.parseInt(pulseField.getText().trim());
            double temp = Double.parseDouble(tempField.getText().trim());
            int resp = Integer.parseInt(respField.getText().trim());
            String bp = bpField.getText().trim();

            String id = "VS" + UUID.randomUUID().toString().substring(0, 8);
            VitalSigns v = new VitalSigns(id, pid, pulse, temp, resp, bp, LocalDateTime.now());
            svc.save(v);

            message.setText("Vitals recorded successfully.");
        } catch (NumberFormatException nfe) {
            message.setText("Please enter numeric values for pulse, temp, respiration.");
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML public void onBack()   { SceneNavigator.getInstance().goToPatientDashboard(); }
    @FXML public void onLogout() { Session.logout(); SceneNavigator.getInstance().goToLogin(); }
}
