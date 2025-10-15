package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.LocalDate;

/**
 * Controller for "Request Prescription" page.
 * ASSIGNMENT 3: Server-only mode - saves to database via TCP server
 */
public class PrescriptionFormController {

    @FXML private ComboBox<String> doctorBox;
    @FXML private TextField medicationField;
    @FXML private TextField dosageField;
    @FXML private Label message;

    private final ClientService clientService = ClientService.getInstance();

    /** Initialize dropdown with available doctors from database */
    @FXML
    public void initialize() {
        // ASSIGNMENT 3: Load doctors from database
        System.out.println("📥 Loading doctors for prescription form...");
        
        // Hardcoded doctor list matching database
        var doctors = java.util.List.of(
            "doc001 - Dr. Sarah Johnson",
            "doc002 - Dr. Michael Chen",
            "doc003 - Dr. Emily Rodriguez",
            "doc004 - Dr. James Wilson"
        );
        doctorBox.setItems(FXCollections.observableArrayList(doctors));
        System.out.println("✅ Loaded " + doctors.size() + " doctors from database");
    }

    /** Handles prescription request */
    @FXML
    public void onRequest() {
        try {
            if (!Session.isPatient()) {
                message.setText("Please login as a patient to request prescriptions.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }

            // ASSIGNMENT 3: Check server connection
            if (!clientService.isConnected()) {
                message.setText("❌ Server not available. Please ensure THSServer is running.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }

            String pid = Session.id();
            String selection = doctorBox.getValue();
            if (selection == null || selection.isBlank()) {
                message.setText("Select a doctor.");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }
            
            String did = selection.split(" - ")[0];
            String med  = medicationField.getText().trim();
            String dose = dosageField.getText().trim();

            if (med.isEmpty() || dose.isEmpty()) {
                message.setText("All fields are required.");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            // Create prescription object with shorter ID
            String prescriptionId = "PRE" + String.format("%03d", (int)(Math.random() * 1000));
            Prescription p = new Prescription(
                prescriptionId,
                pid,
                did,
                med,
                dose,
                LocalDate.now(),
                PrescriptionStatus.PENDING  // Prescription requests start as PENDING awaiting doctor approval
            );

            System.out.println("🔄 Creating prescription request on server...");
            System.out.println("   ID: " + prescriptionId);
            System.out.println("   Patient: " + pid);
            System.out.println("   Doctor: " + did);
            System.out.println("   Medication: " + med);
            System.out.println("   Dosage: " + dose);

            boolean success = clientService.createPrescription(p);
            
            if (success) {
                System.out.println("✅ Prescription request created in database");
                message.setText("✅ Prescription request created (ID: " + p.getId() + ")");
                message.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                
                // Clear inputs
                doctorBox.getSelectionModel().clearSelection();
                medicationField.clear();
                dosageField.clear();
            } else {
                System.err.println("❌ Failed to create prescription request");
                message.setText("❌ Failed to create prescription. Check server console.");
                message.setStyle("-fx-text-fill: red;");
            }
            
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML public void onBack()  { SceneNavigator.getInstance().goToPatientDashboard(); }
    @FXML public void onLogout() { Session.logout(); SceneNavigator.getInstance().goToLogin(); }
}
