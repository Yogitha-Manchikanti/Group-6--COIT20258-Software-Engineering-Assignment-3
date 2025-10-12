package com.mycompany.coit20258assignment2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class AdminAppointmentViewerController {

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> idColumn;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> timeColumn;
    @FXML private TableColumn<Appointment, String> patientColumn;
    @FXML private TableColumn<Appointment, String> doctorColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private Label totalLabel;
    
    private ObservableList<Appointment> appointments;
    private DataStore dataStore;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    @FXML
    public void initialize() {
        dataStore = new DataStore("data");
        appointments = FXCollections.observableArrayList();
        
        setupTableColumns();
        loadAppointments();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        dateColumn.setCellValueFactory(cellData -> {
            String formatted = cellData.getValue().getDate().format(DATE_FORMATTER);
            return new SimpleStringProperty(formatted);
        });
        
        timeColumn.setCellValueFactory(cellData -> {
            String formatted = cellData.getValue().getTime().format(TIME_FORMATTER);
            return new SimpleStringProperty(formatted);
        });
        
        patientColumn.setCellValueFactory(cellData -> {
            String patientName = dataStore.findPatientName(cellData.getValue().getPatientId());
            return new SimpleStringProperty(patientName);
        });
        
        doctorColumn.setCellValueFactory(cellData -> {
            String doctorName = dataStore.findDoctorName(cellData.getValue().getDoctorId());
            return new SimpleStringProperty(doctorName);
        });
        
        statusColumn.setCellValueFactory(cellData -> {
            String status = cellData.getValue().getStatus().toString();
            return new SimpleStringProperty(status);
        });
        
        // Color-code status
        statusColumn.setCellFactory(column -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "BOOKED":
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case "RESCHEDULED":
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        case "CANCELED":
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: gray;");
                    }
                }
            }
        });
        
        appointmentTable.setItems(appointments);
    }

    private void loadAppointments() {
        try {
            List<Appointment> list = dataStore.getAppointments();
            appointments.clear();
            appointments.addAll(list);
            totalLabel.setText("Total Appointments: " + list.size());
        } catch (Exception e) {
            showError("Failed to load appointments: " + e.getMessage());
        }
    }

    @FXML
    private void onRefresh() {
        loadAppointments();
    }

    @FXML
    private void onBack() {
        SceneNavigator.getInstance().goToAdminDashboard();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
