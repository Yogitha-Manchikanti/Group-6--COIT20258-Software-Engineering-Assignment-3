package com.mycompany.coit20258assignment2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/** Doctor-facing page: load prescriptions, select a row, then approve/refill. */
public class PrescriptionEditorController {

    @FXML private TextField doctorIdField;     // same pattern as booking editor
    @FXML private ListView<String> listView;   // shows "<id> | Rx ..."
    @FXML private Label message;               // status/errors

    private final PrescriptionService svc = new PrescriptionService(new DataStore("data"));

    @FXML
    public void initialize() {
        // Pre-fill doctor ID for doctors; admins can type any doctor ID then click Load
        if (Session.isDoctor() && Session.id() != null) {
            doctorIdField.setText(Session.id());
        }
        onLoad(); // initial load
    }

    /** Load prescriptions for the doctor ID in the field. */
    @FXML
    public void onLoad() {
        String did = doctorIdField.getText() == null ? "" : doctorIdField.getText().trim();
        List<Prescription> list = svc.getByDoctor(did);
        listView.setItems(FXCollections.observableArrayList(
            list.stream().map(p -> p.getId() + " | " + p).toList()
        ));
        message.setText("Loaded " + list.size() + " prescription(s).");
    }

    /** Approve the currently selected prescription. */
    @FXML
    public void onApproveSelected() {
        String sel = listView.getSelectionModel().getSelectedItem();
        if (sel == null || !sel.contains(" | ")) {
            message.setText("Select a prescription row first.");
            return;
        }
        String id = sel.substring(0, sel.indexOf(" | "));
        try {
            svc.approve(id);
            message.setText("Approved: " + id);
            onLoad(); // refresh list to show updated status
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    /** Refill the currently selected prescription. */
    @FXML
    public void onRefillSelected() {
        String sel = listView.getSelectionModel().getSelectedItem();
        if (sel == null || !sel.contains(" | ")) {
            message.setText("Select a prescription row first.");
            return;
        }
        String id = sel.substring(0, sel.indexOf(" | "));
        try {
            svc.refill(id);
            message.setText("Refilled: " + id);
            onLoad(); // refresh list
        } catch (Exception e) {
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onBack() {
        if (Session.isDoctor()) SceneNavigator.getInstance().goToDoctorDashboard();
        else if (Session.isAdmin()) SceneNavigator.getInstance().goToAdminDashboard();
        else SceneNavigator.getInstance().goToPatientDashboard();
    }
}
