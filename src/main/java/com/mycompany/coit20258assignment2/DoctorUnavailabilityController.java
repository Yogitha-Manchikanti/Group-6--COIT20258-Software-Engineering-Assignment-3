package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for managing doctor unavailability periods
 * ASSIGNMENT 3: Server-only mode - all data saved to database via TCP server
 */
public class DoctorUnavailabilityController {
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private CheckBox allDayCheckbox;
    @FXML private ComboBox<String> reasonComboBox;
    @FXML private TextField customReasonField;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;
    @FXML private Label totalLabel;
    
    @FXML private TableView<UnavailabilityRow> unavailabilityTable;
    @FXML private TableColumn<UnavailabilityRow, String> colStartDate;
    @FXML private TableColumn<UnavailabilityRow, String> colEndDate;
    @FXML private TableColumn<UnavailabilityRow, String> colTime;
    @FXML private TableColumn<UnavailabilityRow, String> colReason;
    
    private ObservableList<UnavailabilityRow> unavailabilityData = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    private final ClientService clientService = ClientService.getInstance();
    
    @FXML
    public void initialize() {
        // Check server connection
        if (!clientService.isConnected()) {
            showError("Server not available. Please ensure THSServer is running.");
            return;
        }
        
        // Setup reason dropdown
        reasonComboBox.setItems(FXCollections.observableArrayList(
            "Vacation",
            "Sick Leave",
            "Conference/Training",
            "Personal Leave",
            "Public Holiday",
            "Custom"
        ));
        reasonComboBox.setValue("Vacation");
        customReasonField.setDisable(true);
        
        // Setup reason selection
        reasonComboBox.setOnAction(e -> {
            boolean isCustom = "Custom".equals(reasonComboBox.getValue());
            customReasonField.setDisable(!isCustom);
            if (isCustom) {
                customReasonField.requestFocus();
            }
        });
        
        // Setup all-day checkbox
        allDayCheckbox.setSelected(true);
        startTimeField.setDisable(true);
        endTimeField.setDisable(true);
        startTimeField.setPromptText("HH:mm (e.g., 09:00)");
        endTimeField.setPromptText("HH:mm (e.g., 17:00)");
        
        allDayCheckbox.setOnAction(e -> {
            boolean allDay = allDayCheckbox.isSelected();
            startTimeField.setDisable(allDay);
            endTimeField.setDisable(allDay);
            if (allDay) {
                startTimeField.clear();
                endTimeField.clear();
            }
        });
        
        // Setup date pickers with default values
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        
        // Setup table
        colStartDate.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        colEndDate.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        colTime.setCellValueFactory(cellData -> cellData.getValue().timeRangeProperty());
        colReason.setCellValueFactory(cellData -> cellData.getValue().reasonProperty());
        
        unavailabilityTable.setItems(unavailabilityData);
        
        // Load existing unavailabilities from server
        loadUnavailabilities();
        
        // Update total count
        updateTotalLabel();
    }
    
    @FXML
    public void onAdd() {
        if (!clientService.isConnected()) {
            showError("Server not available");
            return;
        }
        
        try {
            // Validate inputs
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            
            if (startDate == null || endDate == null) {
                showError("Please select both start and end dates");
                return;
            }
            
            if (endDate.isBefore(startDate)) {
                showError("End date cannot be before start date");
                return;
            }
            
            // Get reason
            String reason;
            if ("Custom".equals(reasonComboBox.getValue())) {
                reason = customReasonField.getText().trim();
                if (reason.isEmpty()) {
                    showError("Please enter a custom reason");
                    return;
                }
            } else {
                reason = reasonComboBox.getValue();
            }
            
            // Generate ID
            String id = "una" + UUID.randomUUID().toString().substring(0, 8);
            String doctorId = Session.get().getId();
            boolean isAllDay = allDayCheckbox.isSelected();
            
            String startTime = null;
            String endTime = null;
            
            if (!isAllDay) {
                // Parse times
                String startTimeStr = startTimeField.getText().trim();
                String endTimeStr = endTimeField.getText().trim();
                
                if (startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                    showError("Please enter both start and end times (HH:mm format)");
                    return;
                }
                
                try {
                    LocalTime st = LocalTime.parse(startTimeStr, TIME_FORMATTER);
                    LocalTime et = LocalTime.parse(endTimeStr, TIME_FORMATTER);
                    
                    if (et.isBefore(st)) {
                        showError("End time cannot be before start time");
                        return;
                    }
                    
                    startTime = st.toString();
                    endTime = et.toString();
                } catch (Exception e) {
                    showError("Invalid time format. Use HH:mm (e.g., 09:00)");
                    return;
                }
            }
            
            // Send to server
            boolean success = clientService.createUnavailability(
                id, doctorId, 
                startDate.toString(), endDate.toString(),
                startTime, endTime, isAllDay, reason
            );
            
            if (success) {
                // Reload data from server
                loadUnavailabilities();
                clearForm();
                showSuccess("Unavailability added successfully!");
                updateTotalLabel();
            } else {
                showError("Failed to add unavailability");
            }
            
        } catch (Exception e) {
            showError("Error adding unavailability: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void onDelete() {
        if (!clientService.isConnected()) {
            showError("Server not available");
            return;
        }
        
        UnavailabilityRow selected = unavailabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an unavailability period to delete");
            return;
        }
        
        // Delete from server
        boolean success = clientService.deleteUnavailability(selected.getId());
        
        if (success) {
            // Reload data from server
            loadUnavailabilities();
            showSuccess("Unavailability deleted successfully!");
            updateTotalLabel();
        } else {
            showError("Failed to delete unavailability");
        }
    }
    
    @FXML
    public void onBack() {
        SceneNavigator.getInstance().goToDoctorDashboard();
    }
    
    private void loadUnavailabilities() {
        // Load from server
        unavailabilityData.clear();
        String doctorId = Session.get().getId();
        
        System.out.println("📅 Loading unavailabilities for doctor: " + doctorId);
        
        List<Map<String, Object>> unavailabilities = clientService.getUnavailabilities(doctorId);
        
        for (Map<String, Object> data : unavailabilities) {
            unavailabilityData.add(new UnavailabilityRow(data));
        }
        
        System.out.println("✅ Loaded " + unavailabilityData.size() + " unavailability periods");
    }
    
    private void clearForm() {
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        startTimeField.clear();
        endTimeField.clear();
        allDayCheckbox.setSelected(true);
        reasonComboBox.setValue("Vacation");
        customReasonField.clear();
    }
    
    private void updateTotalLabel() {
        if (totalLabel != null) {
            totalLabel.setText("Total: " + unavailabilityData.size() + " period(s)");
        }
    }
    
    private void showError(String message) {
        if (statusLabel != null) {
            statusLabel.setText("❌ " + message);
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }
    
    private void showSuccess(String message) {
        if (statusLabel != null) {
            statusLabel.setText("✅ " + message);
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }
    
    /**
     * Helper class for TableView display
     * Now uses Map<String, Object> from server instead of DoctorUnavailability object
     */
    public static class UnavailabilityRow {
        private final String id;
        private final SimpleStringProperty startDate;
        private final SimpleStringProperty endDate;
        private final SimpleStringProperty timeRange;
        private final SimpleStringProperty reason;
        
        public UnavailabilityRow(Map<String, Object> data) {
            this.id = (String) data.get("id");
            this.startDate = new SimpleStringProperty((String) data.get("startDate"));
            this.endDate = new SimpleStringProperty((String) data.get("endDate"));
            
            Boolean isAllDay = (Boolean) data.get("isAllDay");
            String time;
            
            if (isAllDay != null && isAllDay) {
                time = "All Day";
            } else {
                String startTime = (String) data.get("startTime");
                String endTime = (String) data.get("endTime");
                if (startTime != null && endTime != null) {
                    time = startTime + " - " + endTime;
                } else {
                    time = "All Day";
                }
            }
            
            this.timeRange = new SimpleStringProperty(time);
            this.reason = new SimpleStringProperty((String) data.get("reason"));
        }
        
        public String getId() { return id; }
        public String getStartDate() { return startDate.get(); }
        public String getEndDate() { return endDate.get(); }
        public String getTimeRange() { return timeRange.get(); }
        public String getReason() { return reason.get(); }
        
        public SimpleStringProperty startDateProperty() { return startDate; }
        public SimpleStringProperty endDateProperty() { return endDate; }
        public SimpleStringProperty timeRangeProperty() { return timeRange; }
        public SimpleStringProperty reasonProperty() { return reason; }
    }
}
