package com.mycompany.coit20258assignment2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Doctor version of "My Data" with the same look/feel as the patient page.
 * Shows the logged-in doctor's appointments and prescriptions for THEIR patients.
 */
public class DoctorDataViewController {

    @FXML private ListView<String> appointmentsList;
    @FXML private ListView<String> prescriptionsList;
    @FXML private Label message;

    private final DataStore store = new DataStore("data");

    @FXML
    public void initialize() {
        loadData();
    }

    private void loadData() {
        if (!Session.isDoctor()) {
            message.setText("Login as a doctor to view this page.");
            appointmentsList.setItems(FXCollections.observableArrayList());
            prescriptionsList.setItems(FXCollections.observableArrayList());
            return;
        }

        String doctorId = Session.id();
        String doctorName = store.findDoctorName(doctorId);

        // Appointments for this doctor – show with patient name and friendly date/time
        var apptFmtDate = DateTimeFormatter.ISO_LOCAL_DATE;
        var apptFmtTime = DateTimeFormatter.ofPattern("HH:mm");

        var apptLines = store.getAppointments().stream()
                .filter(a -> doctorId.equals(a.getDoctorId()))
                .map(a -> {
                    String pName = store.findPatientName(a.getPatientId());
                    return a.getId() + " on " + a.getDate().format(apptFmtDate) +
                           " at " + a.getTime().format(apptFmtTime) +
                           " with " + pName + " — " + a.getStatus();
                })
                .toList();

        // Prescriptions that belong to this doctor – show patient name and status
        var rxFmtDate = DateTimeFormatter.ISO_LOCAL_DATE;
        var rxLines = store.getPrescriptions().stream()
                .filter(p -> doctorId.equals(p.getDoctorId()))
                .map(p -> {
                    String pName = store.findPatientName(p.getPatientId());
                    return p.getDate().format(rxFmtDate) + " " + doctorName + " — " +
                           pName + " — " + p.getMedication() + " (" + p.getStatus() + ")";
                })
                .toList();

        appointmentsList.setItems(FXCollections.observableArrayList(apptLines));
        prescriptionsList.setItems(FXCollections.observableArrayList(rxLines));
        message.setText("");
    }

    /* ===== Top bar actions ===== */

    @FXML
    public void onRefresh() {
        loadData();
        message.setText("Refreshed.");
    }

    @FXML
    public void onExportCsv() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("Section,Details");

            for (String row : appointmentsList.getItems()) {
                lines.add("Appointment," + row.replace(",", " "));
            }
            for (String row : prescriptionsList.getItems()) {
                lines.add("Prescription," + row.replace(",", " "));
            }

            File out = new File("doctor_data.csv");
            try (BufferedWriter w = new BufferedWriter(new FileWriter(out))) {
                for (String s : lines) {
                    w.write(s);
                    w.newLine();
                }
            }
            message.setText("Exported to " + out.getAbsolutePath());
        } catch (Exception e) {
            message.setText("Export failed: " + e.getMessage());
        }
    }

    @FXML
    public void onBack() {
        SceneNavigator.getInstance().goToDoctorDashboard();
    }

    @FXML
    public void onLogout() {
        Session.logout();
        SceneNavigator.getInstance().goToLogin();
    }
}
