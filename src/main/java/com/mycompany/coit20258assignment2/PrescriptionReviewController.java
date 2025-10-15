package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller for doctors to view, approve, and reject prescriptions.
 * ASSIGNMENT 3: Server-only mode - loads prescriptions from database via TCP server
 */
public class PrescriptionReviewController {

    @FXML private TextField rxIdField;
    @FXML private ListView<String> list;
    @FXML private Label message;

    private final ClientService clientService = ClientService.getInstance();

    @FXML
    public void initialize() {
        if (!clientService.isConnected()) {
            message.setText("Server not available. Please ensure THSServer is running.");
            message.setStyle("-fx-text-fill: red;");
            return;
        }
        refresh();
    }

    /** Refresh list based on current doctor session */
    private void refresh() {
        if (!Session.isDoctor()) {
            message.setText("Login as a doctor to view this page.");
            list.setItems(FXCollections.observableArrayList());
            return;
        }

        if (!clientService.isConnected()) {
            message.setText("Server not available.");
            message.setStyle("-fx-text-fill: red;");
            return;
        }

        String doctorId = Session.id();
        System.out.println("Loading prescriptions for doctor: " + doctorId);

        // Get all prescriptions from server and filter by doctor
        List<Prescription> all = clientService.getPrescriptions().stream()
                .filter(p -> p.getDoctorId().equals(doctorId))
                .toList();

        if (all.isEmpty()) {
            message.setText("No prescriptions found for you.");
            list.setItems(FXCollections.observableArrayList());
            return;
        }

        List<String> rows = all.stream().map(p -> {
            String patientName = findUserName(p.getPatientId());
            return String.format("%s | Patient: %s | %s | %s",
                    p.getId(), 
                    patientName != null ? patientName : p.getPatientId(), 
                    p.getMedication(), 
                    p.getStatus());
        }).toList();

        list.setItems(FXCollections.observableArrayList(rows));
        message.setText(all.size() + " prescription(s) found.");
        message.setStyle("-fx-text-fill: green;");
    }

    @FXML public void onApprove() {
        String id = rxIdField.getText().trim();
        if (id.isEmpty()) {
            message.setText("Enter prescription ID.");
            message.setStyle("-fx-text-fill: orange;");
            return;
        }
        
        if (!clientService.isConnected()) {
            message.setText("Server not available.");
            message.setStyle("-fx-text-fill: red;");
            return;
        }
        
        try {
            System.out.println("Approving prescription refill: " + id);
            boolean success = clientService.refillPrescription(id);
            
            if (success) {
                message.setText("Refill approved " + id);
                message.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                refresh();
            } else {
                message.setText("Failed to approve refill " + id);
                message.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML public void onReject() {
        String id = rxIdField.getText().trim();
        if (id.isEmpty()) {
            message.setText("Enter prescription ID.");
            message.setStyle("-fx-text-fill: orange;");
            return;
        }
        
        if (!clientService.isConnected()) {
            message.setText("Server not available.");
            message.setStyle("-fx-text-fill: red;");
            return;
        }
        
        try {
            System.out.println("Rejecting prescription refill: " + id);
            boolean success = clientService.rejectPrescriptionRefill(id);
            
            if (success) {
                message.setText("Refill rejected " + id);
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                refresh();
            } else {
                message.setText("Failed to reject " + id);
                message.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
    
    /**
     * Find user name by ID from server
     */
    private String findUserName(String userId) {
        try {
            List<User> users = clientService.getUsers();
            for (User user : users) {
                if (user.getId().equals(userId)) {
                    return user.getName();
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding user name: " + e.getMessage());
        }
        return userId; // Return ID if name not found
    }

    @FXML public void onBack() { SceneNavigator.getInstance().goToDoctorDashboard(); }
}
