package com.mycompany.coit20258assignment2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StaffBookingEditorController {

    @FXML private ListView<Appointment> appointmentList;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private Label message;

    private final AppointmentService svc = new AppointmentService(new DataStore("data"));
    private final DataStore store = new DataStore("data");
    private final DateTimeFormatter time12 = DateTimeFormatter.ofPattern("h:mm a");

    @FXML
    public void initialize() {
        // custom renderer to show PATIENT NAME + friendly time
        appointmentList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Appointment a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) { setText(null); return; }
                String patientName = store.getUsers().stream()
                        .filter(u -> u instanceof Patient && u.getId().equals(a.getPatientId()))
                        .map(User::getName).findFirst().orElse(a.getPatientId());
                setText(patientName + " — " + a.getDate() + ", " + a.getTime().format(time12) + "  ["+a.getStatus()+"]");
            }
        });
        onLoad();
    }

    @FXML public void onLoad() {
        try {
            var list = svc.getByDoctor(Session.id());
            appointmentList.setItems(FXCollections.observableArrayList(list));
            message.setText("Loaded " + list.size() + " appointment(s).");
        } catch (Exception e) { message.setText("Load error: " + e.getMessage()); }
    }

    @FXML public void onReschedule() {
        try {
            var appt = appointmentList.getSelectionModel().getSelectedItem();
            if (appt == null) { message.setText("Select an appointment."); return; }
            LocalDate d = datePicker.getValue();
            String t = timeField.getText().trim();
            if (d == null || t.isEmpty()) { message.setText("Enter date & time."); return; }
            svc.reschedule(appt.getId(), d, LocalTime.parse(t));
            message.setText("Rescheduled " + appt.getId());
            onLoad();
        } catch (Exception e) { message.setText("Reschedule failed: " + e.getMessage()); }
    }

    @FXML public void onCancel() {
        try {
            var appt = appointmentList.getSelectionModel().getSelectedItem();
            if (appt == null) { message.setText("Select an appointment."); return; }
            svc.cancel(appt.getId());
            message.setText("Cancelled " + appt.getId());
            onLoad();
        } catch (Exception e) { message.setText("Cancel failed: " + e.getMessage()); }
    }

    @FXML public void onBack()   { SceneNavigator.getInstance().goToDoctorDashboard(); }
    @FXML public void onLogout() { Session.logout(); SceneNavigator.getInstance().goToLogin(); }
}
