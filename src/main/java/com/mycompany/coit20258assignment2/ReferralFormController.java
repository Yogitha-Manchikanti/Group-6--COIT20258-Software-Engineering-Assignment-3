package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for the "Create Referral" page.
 * ASSIGNMENT 3: Server-only mode - loads patients from database, saves referrals to database
 */
public class ReferralFormController {

    @FXML private ComboBox<String> patientBox;   // items like "pat001"
    @FXML private TextField destinationField;
    @FXML private TextField specialtyField;
    @FXML private TextArea reasonArea;
    @FXML private Label message;

    private final ClientService clientService = ClientService.getInstance();

    @FXML
    public void initialize() {
        if (!clientService.isConnected()) {
            message.setText("Server not available. Please ensure THSServer is running.");
            message.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Load patients from server by getting appointments
        System.out.println("Loading patients from appointments...");
        List<Appointment> appointments = clientService.getAppointments();
        
        // Get unique patient IDs from appointments
        List<String> patientIds = appointments.stream()
                .map(Appointment::getPatientId)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        System.out.println("Found " + patientIds.size() + " patients");
        
        patientBox.setItems(FXCollections.observableArrayList(patientIds));
    }

    @FXML
    public void onRefer() {
        try {
            if (!Session.isDoctor()) {
                message.setText("Only doctors can create referrals.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            if (!clientService.isConnected()) {
                message.setText("Server not available.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            String patientId = patientBox.getValue();
            if (patientId == null || patientId.isBlank()) {
                message.setText("Please select a patient.");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            String destination = destinationField.getText().trim();
            String specialty = specialtyField.getText().trim();
            String reason = reasonArea.getText().trim();

            if (destination.isEmpty() || reason.isEmpty()) {
                message.setText("Destination and reason are required.");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            // Build referral
            String id = "REF" + UUID.randomUUID().toString().substring(0, 7);
            String doctorId = Session.id();
            
            System.out.println("Creating referral...");
            System.out.println("   ID: " + id);
            System.out.println("   Patient: " + patientId);
            System.out.println("   Doctor: " + doctorId);
            System.out.println("   Destination: " + destination);
            System.out.println("   Specialty: " + specialty);
            
            boolean success = clientService.createReferral(
                id, patientId, doctorId, destination, specialty, reason, LocalDate.now().toString()
            );
            
            if (success) {
                System.out.println("Referral created successfully");
                message.setText("Referral created (ID: " + id + ")");
                message.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                
                // clear inputs
                destinationField.clear();
                specialtyField.clear();
                reasonArea.clear();
                patientBox.getSelectionModel().clearSelection();
            } else {
                System.err.println("Failed to create referral");
                message.setText("Failed to create referral. Check server console.");
                message.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML public void onBack() { SceneNavigator.getInstance().goToDoctorDashboard(); }
    @FXML public void onLogout() { Session.logout(); SceneNavigator.getInstance().goToLogin(); }
}
