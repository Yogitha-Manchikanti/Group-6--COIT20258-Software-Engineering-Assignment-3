package com.mycompany.coit20258assignment2;

import com.mycompany.coit20258assignment2.client.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;

/**
 * Admin controller to view all users
 * ASSIGNMENT 3: Server-only mode - all data from database via TCP server
 */
public class AdminUsersViewController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> userTypeColumn;
    @FXML private TableColumn<User, String> specializationColumn;
    @FXML private Label totalLabel;
    
    private ObservableList<User> users;
    private final ClientService clientService = ClientService.getInstance();

    @FXML
    public void initialize() {
        // Check server connection
        if (!clientService.isConnected()) {
            showError("Server not available. Please ensure THSServer is running.");
            return;
        }
        
        users = FXCollections.observableArrayList();
        
        setupTableColumns();
        loadUsers();
    }

    private void setupTableColumns() {
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        // User type column - different property access based on user type
        userTypeColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String userType = "UNKNOWN";
            if (user instanceof Patient) {
                userType = "PATIENT";
            } else if (user instanceof Doctor) {
                userType = "DOCTOR";
            } else if (user instanceof Administrator) {
                userType = "ADMINISTRATOR";
            }
            return new SimpleStringProperty(userType);
        });
        
        // Specialization column - only for doctors
        specializationColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String specialization = "";
            if (user instanceof Doctor) {
                specialization = ((Doctor) user).getSpecialization();
                if (specialization == null) specialization = "";
            }
            return new SimpleStringProperty(specialization);
        });

        // Set row factory for alternate row colors and user type styling
        usersTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldUser, newUser) -> {
                if (newUser == null) {
                    row.setStyle("");
                } else {
                    String baseStyle = row.getIndex() % 2 == 0 ? 
                        "-fx-background-color: #f8f9fa;" : "-fx-background-color: white;";
                    
                    // Add user type indicator
                    if (newUser instanceof Administrator) {
                        baseStyle += " -fx-border-color: #e74c3c; -fx-border-width: 0 0 0 3;";
                    } else if (newUser instanceof Doctor) {
                        baseStyle += " -fx-border-color: #3498db; -fx-border-width: 0 0 0 3;";
                    } else if (newUser instanceof Patient) {
                        baseStyle += " -fx-border-color: #2ecc71; -fx-border-width: 0 0 0 3;";
                    }
                    
                    row.setStyle(baseStyle);
                }
            });
            return row;
        });

        usersTable.setItems(users);
    }

    private void loadUsers() {
        if (!clientService.isConnected()) {
            showError("Server not available");
            return;
        }
        
        try {
            System.out.println("👥 Admin loading all users from server...");
            List<User> list = clientService.getUsers();
            
            users.clear();
            users.addAll(list);
            
            totalLabel.setText("Total Users: " + list.size());
            System.out.println("✅ Loaded " + list.size() + " users");
        } catch (Exception e) {
            showError("Failed to load users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onRefresh() {
        loadUsers();
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