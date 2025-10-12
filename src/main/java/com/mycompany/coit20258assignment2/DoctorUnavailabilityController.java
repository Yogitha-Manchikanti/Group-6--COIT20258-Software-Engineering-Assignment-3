package com.mycompany.coit20258assignment2;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for managing doctor unavailability periods
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
    
    @FXML
    public void initialize() {
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
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeRange"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        
        unavailabilityTable.setItems(unavailabilityData);
        
        // Load existing unavailabilities
        loadUnavailabilities();
        
        // Update total count
        updateTotalLabel();
    }
    
    @FXML
    public void onAdd() {
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
            
            // Create unavailability
            DoctorUnavailability unavailability;
            String id = UUID.randomUUID().toString().substring(0, 8);
            String doctorId = Session.get().getId();
            
            if (allDayCheckbox.isSelected()) {
                unavailability = new DoctorUnavailability(id, doctorId, startDate, endDate, reason);
            } else {
                // Parse times
                String startTimeStr = startTimeField.getText().trim();
                String endTimeStr = endTimeField.getText().trim();
                
                if (startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                    showError("Please enter both start and end times (HH:mm format)");
                    return;
                }
                
                LocalTime startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);
                LocalTime endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);
                
                if (endTime.isBefore(startTime)) {
                    showError("End time cannot be before start time");
                    return;
                }
                
                unavailability = new DoctorUnavailability(id, doctorId, startDate, endDate, 
                                                         startTime, endTime, reason);
            }
            
            // Add to list
            DoctorUnavailabilityService.addUnavailability(unavailability);
            
            // Add to table
            unavailabilityData.add(new UnavailabilityRow(unavailability));
            
            // Clear form
            clearForm();
            
            showSuccess("Unavailability added successfully!");
            updateTotalLabel();
            
        } catch (Exception e) {
            showError("Error adding unavailability: " + e.getMessage());
        }
    }
    
    @FXML
    public void onDelete() {
        UnavailabilityRow selected = unavailabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an unavailability period to delete");
            return;
        }
        
        // Remove from service
        DoctorUnavailabilityService.deleteUnavailability(selected.getId());
        
        // Remove from table
        unavailabilityData.remove(selected);
        
        showSuccess("Unavailability deleted successfully!");
        updateTotalLabel();
    }
    
    @FXML
    public void onBack() {
        SceneNavigator.getInstance().goToDoctorDashboard();
    }
    
    private void loadUnavailabilities() {
        // Load from service
        unavailabilityData.clear();
        String doctorId = Session.get().getId();
        
        List<UnavailabilityRow> rows = DoctorUnavailabilityService.getUnavailabilities(doctorId).stream()
            .map(UnavailabilityRow::new)
            .collect(Collectors.toList());
        
        unavailabilityData.addAll(rows);
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
     */
    public static class UnavailabilityRow {
        private final String id;
        private final SimpleStringProperty startDate;
        private final SimpleStringProperty endDate;
        private final SimpleStringProperty timeRange;
        private final SimpleStringProperty reason;
        
        public UnavailabilityRow(DoctorUnavailability unavailability) {
            this.id = unavailability.getId();
            this.startDate = new SimpleStringProperty(unavailability.getStartDate().format(DATE_FORMATTER));
            this.endDate = new SimpleStringProperty(unavailability.getEndDate().format(DATE_FORMATTER));
            
            String time;
            if (unavailability.isAllDay()) {
                time = "All Day";
            } else {
                time = unavailability.getStartTime().format(TIME_FORMATTER) + 
                       " - " + 
                       unavailability.getEndTime().format(TIME_FORMATTER);
            }
            this.timeRange = new SimpleStringProperty(time);
            this.reason = new SimpleStringProperty(unavailability.getReason());
        }
        
        public String getId() { return id; }
        public String getStartDate() { return startDate.get(); }
        public String getEndDate() { return endDate.get(); }
        public String getTimeRange() { return timeRange.get(); }
        public String getReason() { return reason.get(); }
    }
}
