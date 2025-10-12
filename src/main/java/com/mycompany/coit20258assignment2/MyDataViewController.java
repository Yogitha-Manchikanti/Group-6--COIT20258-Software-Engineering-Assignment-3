package com.mycompany.coit20258assignment2;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * One controller for both Patients and Doctors.
 * - Patients see their own appointments/prescriptions
 * - Doctors see only the items where they are the doctor
 * Back button returns to the correct dashboard based on the active role.
 */
public class MyDataViewController {

    @FXML private Button backBtn;
    @FXML private Label  title;
    @FXML private Label  message;
    @FXML private ListView<String> appointmentsList;
    @FXML private ListView<String> prescriptionsList;

    private final DataStore store = new DataStore("data");
    private final DateTimeFormatter D = DateTimeFormatter.ISO_LOCAL_DATE;

    // ---- lifecycle ---------------------------------------------------------

    @FXML
    public void initialize() {
        if (Session.isPatient()) {
            title.setText("My Data (Patient)");
            loadPatientData(Session.id());
            backBtn.setOnAction(e -> SceneNavigator.getInstance().goToPatientDashboard());
        } else if (Session.isDoctor()) {
            title.setText("My Data (Doctor)");
            loadDoctorData(Session.id());
            backBtn.setOnAction(e -> SceneNavigator.getInstance().goToDoctorDashboard());
        } else {
            message.setText("Please log in.");
            backBtn.setOnAction(e -> SceneNavigator.getInstance().goToLogin());
        }
    }

    // ---- data loaders ------------------------------------------------------

    private void loadPatientData(String patientId) {
        // Appointments (patient view)
        List<String> appts = store.getAppointments().stream()
                .filter(a -> a.getPatientId().equals(patientId))
                .map(a -> String.format("%s on %s at %s with %s",
                        a.getId(),
                        a.getDate().format(D),
                        a.getTime(),
                        store.findDoctorName(a.getDoctorId())))
                .toList();

        // Prescriptions (patient view)
        List<String> rxs = store.getPrescriptions().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .map(p -> String.format("%s %s — %s (%s)",
                        p.getDate().format(D),
                        store.findDoctorName(p.getDoctorId()),
                        p.getMedication(),
                        p.getStatus()))
                .toList();

        appointmentsList.getItems().setAll(appts);
        prescriptionsList.getItems().setAll(rxs);
        message.setText("");
    }

    private void loadDoctorData(String doctorId) {
        // Appointments (doctor view)
        List<String> appts = store.getAppointments().stream()
                .filter(a -> a.getDoctorId().equals(doctorId))
                .map(a -> String.format("%s on %s at %s with %s",
                        a.getId(),
                        a.getDate().format(D),
                        a.getTime(),
                        store.findPatientName(a.getPatientId())))
                .toList();

        // Prescriptions (doctor view)
        List<String> rxs = store.getPrescriptions().stream()
                .filter(p -> p.getDoctorId().equals(doctorId))
                .map(p -> String.format("%s %s — %s (%s)",
                        p.getDate().format(D),
                        store.findPatientName(p.getPatientId()),
                        p.getMedication(),
                        p.getStatus()))
                .toList();

        appointmentsList.getItems().setAll(appts);
        prescriptionsList.getItems().setAll(rxs);
        message.setText("");
    }

    // ---- actions -----------------------------------------------------------

    @FXML
    public void onBack() {
        // handler is replaced in initialize() to the right destination,
        // but keep a safe default:
        if (Session.isDoctor()) {
            SceneNavigator.getInstance().goToDoctorDashboard();
        } else if (Session.isPatient()) {
            SceneNavigator.getInstance().goToPatientDashboard();
        } else {
            SceneNavigator.getInstance().goToLogin();
        }
    }

    @FXML
    public void refresh() {
        if (Session.isDoctor()) {
            loadDoctorData(Session.id());
        } else if (Session.isPatient()) {
            loadPatientData(Session.id());
        } else {
            message.setText("Please log in.");
        }
    }

    @FXML
    public void exportCsv() {
        try {
            String apptCsv = appointmentsList.getItems().stream()
                    .map(s -> escapeCsv(s))
                    .collect(Collectors.joining("\n"));

            String rxCsv = prescriptionsList.getItems().stream()
                    .map(s -> escapeCsv(s))
                    .collect(Collectors.joining("\n"));

            String all = "Appointments\n" + apptCsv + "\n\nPrescriptions\n" + rxCsv + "\n";
            Path out = Path.of(System.getProperty("user.home"),
                               Session.isDoctor() ? "doctor_data.csv" : "patient_data.csv");

            try (FileWriter fw = new FileWriter(out.toFile(), false)) {
                fw.write(all);
            }
            message.setText("Exported to: " + out);
        } catch (Exception e) {
            message.setText("Export failed: " + e.getMessage());
        }
    }

    @FXML
    public void onLogout() {
        Session.logout();
        SceneNavigator.getInstance().goToLogin();
    }

    // ---- helpers -----------------------------------------------------------

    private static String escapeCsv(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
