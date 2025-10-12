package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Patient records their vital signs.
 * ASSIGNMENT 3: Server-only mode - saves to database via TCP server
 */
public class VitalsFormController {

    @FXML private TextField pulseField;
    @FXML private TextField tempField;
    @FXML private TextField respField;
    @FXML private TextField bpField;
    @FXML private Label message;

    private final ClientService clientService = ClientService.getInstance();

    @FXML
    public void onSubmit() {
        try {
            if (!Session.isPatient()) { 
                message.setText("Please login as a patient."); 
                return; 
            }
            
            // ASSIGNMENT 3: Check server connection
            if (!clientService.isConnected()) {
                message.setText("❌ Server not available. Please ensure THSServer is running.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            String pid = Session.id();

            int pulse = Integer.parseInt(pulseField.getText().trim());
            double temp = Double.parseDouble(tempField.getText().trim());
            int resp = Integer.parseInt(respField.getText().trim());
            String bp = bpField.getText().trim();

            String id = "VS" + UUID.randomUUID().toString().substring(0, 8);
            VitalSigns v = new VitalSigns(id, pid, pulse, temp, resp, bp, LocalDateTime.now());
            
            System.out.println("🔄 Saving vital signs to server...");
            System.out.println("   ID: " + id);
            System.out.println("   Patient: " + pid);
            System.out.println("   Pulse: " + pulse + ", Temp: " + temp + "°C, Resp: " + resp + ", BP: " + bp);
            
            boolean success = clientService.recordVitalSigns(v);
            
            if (success) {
                System.out.println("✅ Vital signs saved to database");
                message.setText("✅ Vitals recorded successfully.");
                message.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                
                // Clear fields
                pulseField.clear();
                tempField.clear();
                respField.clear();
                bpField.clear();
            } else {
                System.err.println("❌ Failed to save vital signs");
                message.setText("❌ Failed to record vitals. Check server console.");
                message.setStyle("-fx-text-fill: red;");
            }
            
        } catch (NumberFormatException nfe) {
            message.setText("Please enter numeric values for pulse, temp, respiration.");
            message.setStyle("-fx-text-fill: orange;");
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML public void onBack()   { SceneNavigator.getInstance().goToPatientDashboard(); }
    @FXML public void onLogout() { Session.logout(); SceneNavigator.getInstance().goToLogin(); }
}
