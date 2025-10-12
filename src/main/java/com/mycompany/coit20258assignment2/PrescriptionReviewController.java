package com.mycompany.coit20258assignment2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller for doctors to view, approve, and refill prescriptions.
 * Each doctor sees only prescriptions assigned to them.
 */
public class PrescriptionReviewController {

    @FXML private TextField rxIdField;
    @FXML private ListView<String> list;
    @FXML private Label message;

    private final DataStore store = new DataStore("data");
    private final PrescriptionService svc = new PrescriptionService(store);

    @FXML
    public void initialize() {
        refresh();
    }

    /** Refresh list based on current doctor session */
    private void refresh() {
        if (!Session.isDoctor()) {
            message.setText("Login as a doctor to view this page.");
            list.setItems(FXCollections.observableArrayList());
            return;
        }

        String doctorId = Session.id();

        // Only prescriptions belonging to this doctor
        var all = store.getPrescriptions().stream()
                .filter(p -> p.getDoctorId().equals(doctorId))
                .toList();

        if (all.isEmpty()) {
            message.setText("No prescriptions found for you.");
            list.setItems(FXCollections.observableArrayList());
            return;
        }

        List<String> rows = all.stream().map(p -> {
            String patientName = store.findPatientName(p.getPatientId());
            return String.format("%s | %s | %s | %s",
                    p.getId(), patientName, p.getMedication(), p.getStatus());
        }).toList();

        list.setItems(FXCollections.observableArrayList(rows));
        message.setText(all.size() + " record(s) found.");
    }

    @FXML public void onApprove() {
        String id = rxIdField.getText().trim();
        if (id.isEmpty()) {
            message.setText("Enter prescription ID.");
            return;
        }
        try {
            svc.approve(id);
            message.setText("Approved " + id);
            refresh();
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML public void onRefill() {
        String id = rxIdField.getText().trim();
        if (id.isEmpty()) {
            message.setText("Enter prescription ID.");
            return;
        }
        try {
            svc.refill(id);
            message.setText("Refilled " + id);
            refresh();
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML public void onBack() { SceneNavigator.getInstance().goToDoctorDashboard(); }
}
