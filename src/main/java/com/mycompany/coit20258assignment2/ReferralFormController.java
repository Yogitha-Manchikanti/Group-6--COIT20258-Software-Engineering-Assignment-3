package com.mycompany.coit20258assignment2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

/**
 * Controller for the "Create Referral" page.
 * Doctors select a patient from a dropdown (by name), enter destination and reason,
 * and a referral record is saved.
 */
public class ReferralFormController {

    @FXML private ComboBox<String> patientBox;   // items like "P001 - Alice Patient"
    @FXML private TextField destinationField;
    @FXML private TextArea reasonArea;
    @FXML private Label message;

    private final DataStore store = new DataStore("data");

    @FXML
    public void initialize() {
        // populate patient names (ID - Name)
        var patients = store.getUsers().stream()
                .filter(u -> u instanceof Patient)
                .map(u -> u.getId() + " - " + u.getName())
                .toList();
        patientBox.setItems(FXCollections.observableArrayList(patients));
    }

    @FXML
    public void onRefer() {
        try {
            if (!Session.isDoctor()) {
                message.setText("Only doctors can create referrals.");
                return;
            }
            String selected = patientBox.getValue();
            if (selected == null || selected.isBlank()) {
                message.setText("Please select a patient.");
                return;
            }
            String patientId = selected.split(" - ")[0];

            String destination = destinationField.getText().trim();
            String reason = reasonArea.getText().trim();

            if (destination.isEmpty() || reason.isEmpty()) {
                message.setText("Destination and reason are required.");
                return;
            }

            // Build and persist referral
            String id = "R" + java.util.UUID.randomUUID().toString().substring(0, 7);
            String doctorId = Session.id();
            Referral ref = new Referral(
                    id,
                    patientId,
                    doctorId,
                    destination,
                    reason,
                    LocalDate.now()
            );

            var list = store.getReferrals();
            list.add(ref);
            store.saveReferrals(list);

            message.setText("Referral created for " + store.findPatientName(patientId) + ".");
            // clear inputs
            destinationField.clear();
            reasonArea.clear();
            patientBox.getSelectionModel().clearSelection();
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML public void onBack()   { SceneNavigator.getInstance().goToDoctorDashboard(); }
    @FXML public void onLogout() { Session.logout(); SceneNavigator.getInstance().goToLogin(); }
}
