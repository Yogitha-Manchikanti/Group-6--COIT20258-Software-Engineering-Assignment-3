package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * One controller for both Patients and Doctors.
 * ASSIGNMENT 3: Server-only mode - all data from database via TCP server
 * - Patients see their own appointments/prescriptions
 * - Doctors see only the items where they are the doctor
 */
public class MyDataViewController {

    @FXML private Button backBtn;
    @FXML private Label  title;
    @FXML private Label  message;
    @FXML private ListView<String> appointmentsList;
    @FXML private ListView<String> prescriptionsList;
    @FXML private ListView<String> diagnosesList;
    @FXML private ListView<String> referralsList;

    private final ClientService clientService = ClientService.getInstance();
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
        // ASSIGNMENT 3: Server-only mode
        if (!clientService.isConnected()) {
            message.setText("❌ Server not available. Please ensure THSServer is running.");
            return;
        }
        
        System.out.println("� Loading patient data from server...");
        List<Appointment> appointments = clientService.getAppointments();
        List<Prescription> prescriptions = clientService.getPrescriptions();
        
        // Appointments (patient view)
        List<String> appts = appointments.stream()
                .filter(a -> a.getPatientId().equals(patientId))
                .map(a -> String.format("%s on %s at %s - Status: %s",
                        a.getId(),
                        a.getDate().format(D),
                        a.getTime(),
                        a.getStatus()))
                .toList();

        // Prescriptions (patient view)
        List<String> rxs = prescriptions.stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .map(p -> String.format("%s — %s (%s)",
                        p.getDate().format(D),
                        p.getMedication(),
                        p.getStatus()))
                .toList();

        appointmentsList.getItems().setAll(appts);
        prescriptionsList.getItems().setAll(rxs);
        
        // Get diagnoses from server
        java.util.List<java.util.Map<String, Object>> diagnoses = clientService.getDiagnoses(patientId, null);
        List<String> diags = diagnoses.stream()
                .map(d -> {
                    String date = (String) d.get("diagnosisDate");
                    String description = (String) d.get("diagnosisDescription");
                    String severity = (String) d.get("severity");
                    String code = (String) d.get("diagnosisCode");
                    String doctorId = (String) d.get("doctorId");
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(date != null ? date : "Unknown Date");
                    sb.append(" - ");
                    
                    if (code != null && !code.isEmpty()) {
                        sb.append("[").append(code).append("] ");
                    }
                    
                    sb.append(description != null ? description : "No description");
                    
                    if (severity != null && !severity.isEmpty()) {
                        sb.append(" (").append(severity).append(")");
                    }
                    
                    sb.append(" by Dr. ").append(doctorId);
                    
                    return sb.toString();
                })
                .toList();
        diagnosesList.getItems().setAll(diags);
        
        // Get referrals from server
        java.util.List<java.util.Map<String, Object>> referrals = clientService.getReferrals(patientId, null);
        List<String> refs = referrals.stream()
                .map(r -> {
                    String date = (String) r.get("referral_date");
                    String specialty = (String) r.get("specialty_required");
                    String reason = (String) r.get("reason");
                    String status = (String) r.get("status");
                    String doctorId = (String) r.get("referring_doctor_id");
                    
                    return String.format("%s - %s | %s | Status: %s (by Dr. %s)",
                            date, specialty, reason, status, doctorId);
                })
                .toList();
        referralsList.getItems().setAll(refs);
        
        message.setText(appts.size() + " appointments, " + rxs.size() + " prescriptions, " + 
                       diags.size() + " diagnoses, " + refs.size() + " referrals");
    }

    private void loadDoctorData(String doctorId) {
        // ASSIGNMENT 3: Server-only mode
        if (!clientService.isConnected()) {
            message.setText("❌ Server not available. Please ensure THSServer is running.");
            return;
        }
        
        System.out.println("� Loading doctor data from server...");
        List<Appointment> appointments = clientService.getAppointments();
        List<Prescription> prescriptions = clientService.getPrescriptions();
        
        // Appointments (doctor view)
        List<String> appts = appointments.stream()
                .filter(a -> a.getDoctorId().equals(doctorId))
                .map(a -> String.format("%s on %s at %s - Patient: %s - Status: %s",
                        a.getId(),
                        a.getDate().format(D),
                        a.getTime(),
                        a.getPatientId(),
                        a.getStatus()))
                .toList();

        // Prescriptions (doctor view)
        List<String> rxs = prescriptions.stream()
                .filter(p -> p.getDoctorId().equals(doctorId))
                .map(p -> String.format("%s Patient: %s — %s (%s)",
                        p.getDate().format(D),
                        p.getPatientId(),
                        p.getMedication(),
                        p.getStatus()))
                .toList();

        appointmentsList.getItems().setAll(appts);
        prescriptionsList.getItems().setAll(rxs);
        
        // Get diagnoses from server
        java.util.List<java.util.Map<String, Object>> diagnoses = clientService.getDiagnoses(null, doctorId);
        List<String> diags = diagnoses.stream()
                .map(d -> {
                    String date = (String) d.get("diagnosisDate");
                    String description = (String) d.get("diagnosisDescription");
                    String severity = (String) d.get("severity");
                    String code = (String) d.get("diagnosisCode");
                    String patientId = (String) d.get("patientId");
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(date != null ? date : "Unknown Date");
                    sb.append(" - Patient: ").append(patientId).append(" - ");
                    
                    if (code != null && !code.isEmpty()) {
                        sb.append("[").append(code).append("] ");
                    }
                    
                    sb.append(description != null ? description : "No description");
                    
                    if (severity != null && !severity.isEmpty()) {
                        sb.append(" (").append(severity).append(")");
                    }
                    
                    return sb.toString();
                })
                .toList();
        diagnosesList.getItems().setAll(diags);
        
        // Get referrals created by this doctor
        java.util.List<java.util.Map<String, Object>> referrals = clientService.getReferrals(null, doctorId);
        List<String> refs = referrals.stream()
                .map(r -> {
                    String date = (String) r.get("referral_date");
                    String specialty = (String) r.get("specialty_required");
                    String reason = (String) r.get("reason");
                    String status = (String) r.get("status");
                    String patientId = (String) r.get("patient_id");
                    
                    return String.format("%s - Patient: %s | %s | %s | Status: %s",
                            date, patientId, specialty, reason, status);
                })
                .toList();
        referralsList.getItems().setAll(refs);
        
        message.setText(appts.size() + " appointments, " + rxs.size() + " prescriptions, " + 
                       diags.size() + " diagnoses, " + refs.size() + " referrals");
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
