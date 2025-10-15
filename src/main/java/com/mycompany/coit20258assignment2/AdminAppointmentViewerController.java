package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Admin controller to view all appointments
 * ASSIGNMENT 3: Server-only mode - all data from database via TCP server
 */
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
    private final ClientService clientService = ClientService.getInstance();
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    @FXML
    public void initialize() {
        // Check server connection
        if (!clientService.isConnected()) {
            showError("Server not available. Please ensure THSServer is running.");
            return;
        }
        
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
            String patientId = cellData.getValue().getPatientId();
            String patientName = findUserName(patientId);
            return new SimpleStringProperty(patientName != null ? patientName : patientId);
        });
        
        doctorColumn.setCellValueFactory(cellData -> {
            String doctorId = cellData.getValue().getDoctorId();
            String doctorName = findUserName(doctorId);
            return new SimpleStringProperty(doctorName != null ? doctorName : doctorId);
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
                        case "CONFIRMED":
                        case "SCHEDULED":
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case "RESCHEDULED":
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        case "CANCELLED":
                        case "CANCELED":
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        case "COMPLETED":
                            setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
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
        if (!clientService.isConnected()) {
            showError("Server not available");
            return;
        }
        
        try {
            System.out.println("📅 Admin loading all appointments from server...");
            List<Appointment> list = clientService.getAppointments();
            
            appointments.clear();
            appointments.addAll(list);
            
            totalLabel.setText("Total Appointments: " + list.size());
            System.out.println("✅ Loaded " + list.size() + " appointments");
        } catch (Exception e) {
            showError("Failed to load appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Find user name by ID from server
     */
    private String findUserName(String userId) {
        try {
            List<User> users = clientService.getUsers();
            for (User user : users) {
                if (user.getId().equals(userId)) {
                    return user.getName();
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding user name: " + e.getMessage());
        }
        return userId; // Return ID if name not found
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
