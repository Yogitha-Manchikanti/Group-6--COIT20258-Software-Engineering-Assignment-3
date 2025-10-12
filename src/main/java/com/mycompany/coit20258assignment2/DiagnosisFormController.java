package com.mycompany.coit20258assignment2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class DiagnosisFormController {

    @FXML private ChoiceBox<String> patientChoice; // "P123 - Alice Patient"
    @FXML private TextArea notesArea;
    @FXML private TextArea planArea;
    @FXML private Label message;

    private final DataStore store = new DataStore("data");

    @FXML
    public void initialize() {
        var patients = store.getUsers().stream()
                .filter(u -> u instanceof Patient)
                .map(u -> u.getId() + " - " + u.getName())
                .toList();
        patientChoice.setItems(FXCollections.observableArrayList(patients));
    }

    @FXML
    public void onSave() {
        try {
            if (!Session.isDoctor()) { message.setText("Please login as a doctor."); return; }
            String sel = patientChoice.getValue();
            if (sel == null) { message.setText("Select patient."); return; }
            String pid = sel.split(" - ")[0];

            String notes = notesArea.getText().trim();
            String plan = planArea.getText().trim();
            if (notes.isEmpty() || plan.isEmpty()) { message.setText("Notes and plan are required."); return; }

            String id = "DX" + UUID.randomUUID().toString().substring(0,8);
            String did = Session.id();
            var list = store.getDiagnoses();
            list.add(new Diagnosis(id, pid, did, notes, plan, LocalDateTime.now()));
            store.saveDiagnoses(list);

            message.setText("Diagnosis saved (ID: "+id+").");
        } catch (Exception e) { message.setText("Error: " + e.getMessage()); }
    }

    @FXML public void onBack()   { SceneNavigator.getInstance().goToDoctorDashboard(); }
    @FXML public void onLogout() { Session.logout(); SceneNavigator.getInstance().goToLogin(); }
}
