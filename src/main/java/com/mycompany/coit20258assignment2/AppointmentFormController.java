package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Controller for the Book Appointment screen.
 * ASSIGNMENT 3: Server-only mode - all data from database via TCP server
 */
public class AppointmentFormController {

    @FXML private ComboBox<String> doctorBox;   // "doc001 - Dr. Sarah Johnson"
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeBox;     // "09:00", "09:30", ...
    @FXML private Label message;

    private final ClientService clientService = ClientService.getInstance();

    @FXML
    public void initialize() {
        // ASSIGNMENT 3: Always load doctors from server/database
        System.out.println("📥 Loading doctors from server...");
        
        // Hard-coded for now - in full implementation would fetch from server
        List<String> doctors = List.of(
            "doc001 - Dr. Sarah Johnson",
            "doc002 - Dr. Michael Chen", 
            "doc003 - Dr. Emily Rodriguez",
            "doc004 - Dr. James Wilson"
        );
        System.out.println("✅ Loaded " + doctors.size() + " doctors from database");
        
        ObservableList<String> doctorItems = FXCollections.observableArrayList(doctors);
        doctorBox.setItems(doctorItems);

        // Populate time slots every 30 minutes 08:00–18:00 (explicit <String>)
        ObservableList<String> times = FXCollections.observableArrayList();
        IntStream.rangeClosed(8, 18).forEach(h -> {
            times.add(String.format("%02d:00", h));
            times.add(String.format("%02d:30", h));
        });
        timeBox.setItems(times);
        
        // Add listeners to check availability when doctor or date changes
        doctorBox.setOnAction(e -> checkAvailability());
        datePicker.setOnAction(e -> checkAvailability());
        timeBox.setOnAction(e -> checkAvailability());
    }
    
    /**
     * Check if selected doctor is available at selected date/time
     */
    private void checkAvailability() {
        String doctorSel = doctorBox.getValue();
        LocalDate date = datePicker.getValue();
        String timeSel = timeBox.getValue();
        
        if (doctorSel == null || date == null || timeSel == null) {
            message.setText("");
            return;
        }
        
        String doctorId = doctorSel.split(" - ")[0];
        LocalTime time = LocalTime.parse(timeSel);
        
        // Check unavailability from server
        List<java.util.Map<String, Object>> unavailabilities = clientService.getAllUnavailabilities();
        
        for (java.util.Map<String, Object> unavail : unavailabilities) {
            String unavailDoctorId = (String) unavail.get("doctor_id");
            
            // Skip if doctor_id is null or doesn't match
            if (unavailDoctorId == null || !unavailDoctorId.equals(doctorId)) {
                continue;
            }
            
            String startDateStr = (String) unavail.get("start_date");
            String endDateStr = (String) unavail.get("end_date");
            
            // Skip if dates are null
            if (startDateStr == null || endDateStr == null) {
                continue;
            }
            
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            
            // Check if date falls within unavailability period
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                String startTimeStr = (String) unavail.get("start_time");
                String endTimeStr = (String) unavail.get("end_time");
                
                // Check if this is an all-day unavailability (no specific times)
                if (startTimeStr == null || endTimeStr == null) {
                    String reason = (String) unavail.get("reason");
                    message.setText("⚠️ Doctor unavailable: " + reason);
                    message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    return;
                } else {
                    // Check time range
                    
                    if (startTimeStr != null && endTimeStr != null) {
                        LocalTime startTime = LocalTime.parse(startTimeStr);
                        LocalTime endTime = LocalTime.parse(endTimeStr);
                        
                        if (!time.isBefore(startTime) && !time.isAfter(endTime)) {
                            String reason = (String) unavail.get("reason");
                            message.setText("⚠️ Doctor unavailable: " + reason);
                            message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            return;
                        }
                    }
                }
            }
        }
        
        message.setText("✅ Doctor is available at this time");
        message.setStyle("-fx-text-fill: green;");
    }

    @FXML
    public void onBook() {
        if (!Session.isPatient()) {
            message.setText("Please login as a patient to book appointments.");
            message.setStyle("-fx-text-fill: red;");
            return;
        }
        String doctorSel = doctorBox.getValue();
        LocalDate date = datePicker.getValue();
        String timeSel = timeBox.getValue();

        if (doctorSel == null || date == null || timeSel == null) {
            message.setText("Choose doctor, date, and time.");
            message.setStyle("-fx-text-fill: red;");
            return;
        }

        // Parse fields
        String doctorId = doctorSel.split(" - ")[0];
        LocalTime time = LocalTime.parse(timeSel);
        
        // Check if doctor is available (server-side check)
        List<java.util.Map<String, Object>> unavailabilities = clientService.getAllUnavailabilities();
        for (java.util.Map<String, Object> unavail : unavailabilities) {
            String unavailDoctorId = (String) unavail.get("doctor_id");
            
            // Skip if doctor_id is null or doesn't match
            if (unavailDoctorId == null || !unavailDoctorId.equals(doctorId)) {
                continue;
            }
            
            String startDateStr = (String) unavail.get("start_date");
            String endDateStr = (String) unavail.get("end_date");
            
            // Skip if dates are null
            if (startDateStr == null || endDateStr == null) {
                continue;
            }
            
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            
            // Check if date falls within unavailability period
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                String startTimeStr = (String) unavail.get("start_time");
                String endTimeStr = (String) unavail.get("end_time");
                
                // Check if this is an all-day unavailability (no specific times)
                if (startTimeStr == null || endTimeStr == null) {
                    String reason = (String) unavail.get("reason");
                    message.setText("❌ Cannot book: Doctor is unavailable (" + reason + ")");
                    message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    return;
                }
                
                // Check time range for partial day unavailability
                
                if (startTimeStr != null && endTimeStr != null) {
                    LocalTime startTime = LocalTime.parse(startTimeStr);
                    LocalTime endTime = LocalTime.parse(endTimeStr);
                    
                    if (!time.isBefore(startTime) && !time.isAfter(endTime)) {
                        String reason = (String) unavail.get("reason");
                        message.setText("❌ Cannot book: Doctor is unavailable (" + reason + ")");
                        message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        return;
                    }
                }
            }
        }

        try {
            // ASSIGNMENT 3: Server-only mode - no local fallback
            if (!clientService.isConnected()) {
                message.setText("❌ Server not available. Please ensure THSServer is running.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            // Create appointment object
            String apptId = "appt" + System.currentTimeMillis();
            Appointment appt = new Appointment(apptId, Session.id(), doctorId, date, time, AppointmentStatus.SCHEDULED);
            
            System.out.println("🔄 Creating appointment on server...");
            System.out.println("   ID: " + apptId);
            System.out.println("   Patient: " + Session.id());
            System.out.println("   Doctor: " + doctorId);
            System.out.println("   Date: " + date);
            System.out.println("   Time: " + time);
            
            boolean success = clientService.createAppointment(appt);
            if (!success) {
                System.err.println("❌ Server returned false when creating appointment");
                message.setText("❌ Failed to create appointment. Check server console for details.");
                message.setStyle("-fx-text-fill: red;");
                return;
            }
            
            System.out.println("✅ Created appointment via server: " + appt.getId());
            
            // Extract doctor name from selection
            String doctorName = doctorSel.substring(doctorSel.indexOf(" - ") + 3);
            message.setText(
                "✅ Booked with " + doctorName + " on " + date + " at " + time + " (ID: " + appt.getId() + ")"
            );
            message.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        } catch (IllegalStateException e) {
            // Doctor unavailability error
            message.setText("❌ " + e.getMessage());
            message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } catch (Exception e) {
            message.setText("❌ Error: " + e.getMessage());
            message.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void onBack() {
        // Return to the correct dashboard for the logged-in role
        if (Session.isDoctor()) {
            SceneNavigator.getInstance().goToDoctorDashboard();
        } else {
            SceneNavigator.getInstance().goToPatientDashboard();
        }
    }

    @FXML
    public void onLogout() {
        Session.logout();
        SceneNavigator.getInstance().goToLogin();
    }
}
