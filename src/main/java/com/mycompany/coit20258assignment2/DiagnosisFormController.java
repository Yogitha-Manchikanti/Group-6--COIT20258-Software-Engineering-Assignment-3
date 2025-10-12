package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Doctor records diagnosis for a patient
 * ASSIGNMENT 3: Server-only mode - loads patients from database, saves diagnosis to database
 */
public class DiagnosisFormController {

    @FXML private ChoiceBox<String> patientChoice;
    @FXML private TextField diagnosisCodeField;
    @FXML private ChoiceBox<String> severityChoice;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea notesArea;
    @FXML private Label message;

    private final ClientService clientService = ClientService.getInstance();

    @FXML
    public void initialize() {
        // ASSIGNMENT 3: Check server connection
        if (!clientService.isConnected()) {
            message.setText("Server not available. Please ensure THSServer is running.");
            message.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Populate severity choices
        severityChoice.setItems(FXCollections.observableArrayList(
            "MILD", "MODERATE", "SEVERE", "CRITICAL"
        ));
        severityChoice.setValue("MILD"); // Default
        
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
        
        patientChoice.setItems(FXCollections.observableArrayList(patientIds));
    }

    @FXML
    public void onSave() {
        try {
            if (!Session.isDoctor()) { 
                message.setText("Please login as a doctor."); 
                message.setStyle("-fx-text-fill: red;");
                return; 
            }
            
            if (!clientService.isConnected()) {
                message.setText("Server not available.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            String patientId = patientChoice.getValue();
            if (patientId == null) { 
                message.setText("Select patient."); 
                message.setStyle("-fx-text-fill: orange;");
                return; 
            }

            String description = descriptionArea.getText().trim();
            if (description.isEmpty()) { 
                message.setText("Diagnosis description is required."); 
                message.setStyle("-fx-text-fill: orange;");
                return; 
            }
            
            String diagnosisCode = diagnosisCodeField.getText().trim();
            String severity = severityChoice.getValue();
            String notes = notesArea.getText().trim();

            String id = "DX" + UUID.randomUUID().toString().substring(0,8);
            String doctorId = Session.id();
            
            System.out.println("Saving diagnosis...");
            System.out.println("   ID: " + id);
            System.out.println("   Patient: " + patientId);
            System.out.println("   Doctor: " + doctorId);
            System.out.println("   Code: " + diagnosisCode);
            System.out.println("   Severity: " + severity);
            System.out.println("   Description: " + description.substring(0, Math.min(50, description.length())) + "...");
            
            // Send diagnosis to server with all fields
            boolean success = clientService.createDiagnosisExtended(
                id, patientId, doctorId, 
                diagnosisCode, description, severity, notes,
                LocalDateTime.now().toString()
            );
            
            if (success) {
                System.out.println("Diagnosis saved to database");
                message.setText("Diagnosis saved (ID: " + id + ").");
                message.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                
                // Clear form
                diagnosisCodeField.clear();
                descriptionArea.clear();
                notesArea.clear();
                severityChoice.setValue("MILD");
            } else {
                System.err.println("Failed to save diagnosis");
                message.setText("Failed to save diagnosis. Check server console.");
                message.setStyle("-fx-text-fill: red;");
            }
            
        } catch (Exception e) { 
            message.setText("Error: " + e.getMessage()); 
            message.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML public void onBack()   { SceneNavigator.getInstance().goToDoctorDashboard(); }
    @FXML public void onLogout() { Session.logout(); SceneNavigator.getInstance().goToLogin(); }
}
