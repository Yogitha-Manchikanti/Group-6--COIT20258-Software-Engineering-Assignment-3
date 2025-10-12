package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Doctor's appointment management screen
 * ASSIGNMENT 3: Server-only mode - loads appointments from database via TCP server
 */
public class StaffBookingEditorController {

    @FXML private ListView<Appointment> appointmentList;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private Label message;

    private final ClientService clientService = ClientService.getInstance();
    private final DateTimeFormatter time12 = DateTimeFormatter.ofPattern("h:mm a");

    @FXML
    public void initialize() {
        // ASSIGNMENT 3: Check server connection
        if (!clientService.isConnected()) {
            message.setText("❌ Server not available. Please ensure THSServer is running.");
            message.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Custom renderer to show PATIENT ID + time + status
        appointmentList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Appointment a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) { 
                    setText(null); 
                    return; 
                }
                // Show patient ID directly (no name lookup needed)
                setText(a.getPatientId() + " — " + a.getDate() + ", " + 
                       a.getTime().format(time12) + "  [" + a.getStatus() + "]");
            }
        });
        onLoad();
    }

    @FXML 
    public void onLoad() {
        try {
            if (!clientService.isConnected()) {
                message.setText("❌ Server not available.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            System.out.println("📥 Loading appointments for doctor: " + Session.id());
            
            // Get all appointments from server and filter by doctor
            List<Appointment> allAppointments = clientService.getAppointments();
            List<Appointment> doctorAppointments = allAppointments.stream()
                    .filter(a -> a.getDoctorId().equals(Session.id()))
                    .toList();
            
            System.out.println("✅ Loaded " + doctorAppointments.size() + " appointments for doctor");
            
            appointmentList.setItems(FXCollections.observableArrayList(doctorAppointments));
            message.setText("Loaded " + doctorAppointments.size() + " appointment(s).");
            message.setStyle("-fx-text-fill: green;");
            
        } catch (Exception e) { 
            message.setText("Load error: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML 
    public void onReschedule() {
        try {
            var appt = appointmentList.getSelectionModel().getSelectedItem();
            if (appt == null) { 
                message.setText("Select an appointment."); 
                message.setStyle("-fx-text-fill: orange;");
                return; 
            }
            
            LocalDate d = datePicker.getValue();
            String t = timeField.getText().trim();
            if (d == null || t.isEmpty()) { 
                message.setText("Enter date & time."); 
                message.setStyle("-fx-text-fill: orange;");
                return; 
            }
            
            if (!clientService.isConnected()) {
                message.setText("❌ Server not available.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            LocalTime newTime = LocalTime.parse(t);
            
            System.out.println("🔄 Rescheduling appointment: " + appt.getId());
            System.out.println("   From: " + appt.getDate() + " " + appt.getTime());
            System.out.println("   To: " + d + " " + newTime);
            
            // Update appointment on server
            boolean success = clientService.updateAppointment(
                appt.getId(), 
                d, 
                newTime, 
                AppointmentStatus.RESCHEDULED
            );
            
            if (success) {
                message.setText("✅ Rescheduled " + appt.getId());
                message.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                onLoad(); // Reload list
            } else {
                message.setText("❌ Failed to reschedule appointment");
                message.setStyle("-fx-text-fill: red;");
            }
            
        } catch (Exception e) { 
            message.setText("Reschedule failed: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML 
    public void onCancel() {
        try {
            var appt = appointmentList.getSelectionModel().getSelectedItem();
            if (appt == null) { 
                message.setText("Select an appointment."); 
                message.setStyle("-fx-text-fill: orange;");
                return; 
            }
            
            if (!clientService.isConnected()) {
                message.setText("❌ Server not available.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            System.out.println("🚫 Cancelling appointment: " + appt.getId());
            
            // Update appointment status to CANCELLED on server
            boolean success = clientService.updateAppointmentStatus(
                appt.getId(), 
                AppointmentStatus.CANCELLED
            );
            
            if (success) {
                message.setText("✅ Cancelled " + appt.getId());
                message.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                onLoad(); // Reload list
            } else {
                message.setText("❌ Failed to cancel appointment");
                message.setStyle("-fx-text-fill: red;");
            }
            
        } catch (Exception e) { 
            message.setText("Cancel failed: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML public void onBack()   { SceneNavigator.getInstance().goToDoctorDashboard(); }
    @FXML public void onLogout() { Session.logout(); SceneNavigator.getInstance().goToLogin(); }
}
