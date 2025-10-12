package com.mycompany.coit20258assignment2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller for the Book Appointment screen.
 * Lets a patient pick a doctor, date and time, then creates an appointment.
 */
public class AppointmentFormController {

    @FXML private ComboBox<String> doctorBox;   // "D001 - Dr. James"
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeBox;     // "09:00", "09:30", ...
    @FXML private Label message;

    private final DataStore store = new DataStore("data");
    private final AppointmentService svc = new AppointmentService(store);

    @FXML
    public void initialize() {
        // Populate doctors (force List<String> for clean generics)
        List<String> doctors = store.getUsers().stream()
                .filter(u -> u instanceof Doctor)
                .map(u -> u.getId() + " - " + u.getName())
                .collect(Collectors.toList());
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
        
        if (!DoctorUnavailabilityService.isDoctorAvailable(doctorId, date, time)) {
            String reason = DoctorUnavailabilityService.getUnavailabilityReason(doctorId, date, time);
            message.setText("⚠️ Doctor unavailable: " + reason);
            message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
        } else {
            message.setText("✅ Doctor is available at this time");
            message.setStyle("-fx-text-fill: green;");
        }
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

        try {
            Appointment appt = svc.book(Session.id(), doctorId, date, time);

            // appt will be created with BOOKED status
            if (appt.getStatus() == AppointmentStatus.BOOKED) {
                String doctorName = store.findDoctorName(doctorId);
                message.setText(
                        "✅ Booked with " + doctorName + " on " + date + " at " + time + " (ID: " + appt.getId() + ")"
                );
                message.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                message.setText("Appointment created (status: " + appt.getStatus() + ", ID: " + appt.getId() + ")");
                message.setStyle("-fx-text-fill: blue;");
            }
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
