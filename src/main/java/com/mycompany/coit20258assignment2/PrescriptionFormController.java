package com.mycompany.coit20258assignment2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for "Request Prescription" page.
 * Patient selects a doctor from dropdown and enters medication details.
 */
public class PrescriptionFormController {

    // Changed to ComboBox<String> to support promptText
    @FXML private ComboBox<String> doctorBox;
    @FXML private TextField medicationField;
    @FXML private TextField dosageField;
    @FXML private Label message;

    private final DataStore store = new DataStore("data");
    private final PrescriptionService svc = new PrescriptionService(store);

    /** Initialize dropdown with available doctors */
    @FXML
    public void initialize() {
        var doctors = store.getUsers().stream()
                .filter(u -> u instanceof Doctor)
                .map(u -> u.getId() + " - " + u.getName())
                .toList();
        doctorBox.setItems(FXCollections.observableArrayList(doctors));
    }

    /** Handles prescription request */
    @FXML
    public void onRequest() {
        try {
            if (!Session.isPatient()) {
                message.setText("Please login as a patient to request prescriptions.");
                return;
            }

            String pid = Session.id(); // logged-in patient
            String selection = doctorBox.getValue();
            if (selection == null || selection.isBlank()) {
                message.setText("Select a doctor."); return;
            }
            String did = selection.split(" - ")[0];
            String med  = medicationField.getText().trim();
            String dose = dosageField.getText().trim();

            if (med.isEmpty() || dose.isEmpty()) {
                message.setText("All fields are required."); return;
            }

            Prescription p = svc.request(pid, did, med, dose);
            message.setText("Prescription request created (ID: " + p.getId() + ")");
            // Optional: clear inputs
            doctorBox.getSelectionModel().clearSelection();
            medicationField.clear();
            dosageField.clear();
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML public void onBack()  { SceneNavigator.getInstance().goToPatientDashboard(); }
    @FXML public void onLogout() { Session.logout(); SceneNavigator.getInstance().goToLogin(); }
}
