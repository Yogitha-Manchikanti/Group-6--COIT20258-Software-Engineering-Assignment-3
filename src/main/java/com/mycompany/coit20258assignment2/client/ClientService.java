package com.mycompany.coit20258assignment2.client;

import com.mycompany.coit20258assignment2.*;
import com.mycompany.coit20258assignment2.common.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ClientService {
    private static ClientService instance;
    private ServerConnection connection;
    private User currentUser;
    
    private ClientService() {
        connection = ServerConnection.getInstance();
    }
    
    public static ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }
    
    /**
     * Authenticate user with server
     */
    public LoginResult login(String username, String password) {
        try {
            GenericRequest request = new GenericRequest("LOGIN", null);
            request.addData("username", username);
            request.addData("password", password);
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                // Extract user data from response
                if (response instanceof GenericResponse) {
                    GenericResponse genResp = (GenericResponse) response;
                    Map<String, Object> data = genResp.getData();
                    
                    String userId = (String) data.get("userId");
                    String fullName = (String) data.get("fullName");
                    String userType = (String) data.get("userType");
                    
                    // Create user object
                    currentUser = new User(userId, fullName, username, password);
                    currentUser.setUserType(UserType.valueOf(userType));
                    
                    return new LoginResult(true, "Login successful", currentUser);
                }
            }
            
            return new LoginResult(false, response != null ? response.getMessage() : "Login failed", null);
            
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return new LoginResult(false, "Error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Get appointments for current user
     */
    public List<Appointment> getAppointments() {
        if (currentUser == null) return new ArrayList<>();
        
        try {
            GenericRequest request = new GenericRequest("GET_APPOINTMENTS", currentUser.getId());
            request.addData("userType", currentUser.getUserType().toString());
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess() && response instanceof GenericResponse) {
                GenericResponse genResp = (GenericResponse) response;
                Object appointmentsObj = genResp.getData("appointments");
                
                if (appointmentsObj instanceof List) {
                    return (List<Appointment>) appointmentsObj;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error getting appointments: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Create new appointment
     */
    public boolean createAppointment(Appointment appointment) {
        try {
            GenericRequest request = new GenericRequest("CREATE_APPOINTMENT", currentUser.getId());
            request.addData("id", appointment.getId());
            request.addData("patientId", appointment.getPatientId());
            request.addData("doctorId", appointment.getDoctorId());
            request.addData("date", appointment.getDate().toString());
            request.addData("time", appointment.getTime().toString());
            request.addData("status", appointment.getStatus().toString());
            
            BaseResponse response = connection.sendRequest(request);
            return response != null && response.isSuccess();
            
        } catch (Exception e) {
            System.err.println("Error creating appointment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update appointment status
     */
    public boolean updateAppointmentStatus(String appointmentId, AppointmentStatus status) {
        try {
            GenericRequest request = new GenericRequest("UPDATE_APPOINTMENT", currentUser.getId());
            request.addData("appointmentId", appointmentId);
            request.addData("status", status.toString());
            
            BaseResponse response = connection.sendRequest(request);
            return response != null && response.isSuccess();
            
        } catch (Exception e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete appointment
     */
    public boolean deleteAppointment(String appointmentId) {
        try {
            GenericRequest request = new GenericRequest("DELETE_APPOINTMENT", currentUser.getId());
            request.addData("appointmentId", appointmentId);
            
            BaseResponse response = connection.sendRequest(request);
            return response != null && response.isSuccess();
            
        } catch (Exception e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get prescriptions for current user
     */
    public List<Prescription> getPrescriptions() {
        if (currentUser == null) return new ArrayList<>();
        
        try {
            GenericRequest request = new GenericRequest("GET_PRESCRIPTIONS", currentUser.getId());
            request.addData("userType", currentUser.getUserType().toString());
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess() && response instanceof GenericResponse) {
                GenericResponse genResp = (GenericResponse) response;
                Object prescriptionsObj = genResp.getData("prescriptions");
                
                if (prescriptionsObj instanceof List) {
                    return (List<Prescription>) prescriptionsObj;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error getting prescriptions: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Create new prescription
     */
    public boolean createPrescription(Prescription prescription) {
        try {
            GenericRequest request = new GenericRequest("CREATE_PRESCRIPTION", currentUser.getId());
            request.addData("id", prescription.getId());
            request.addData("patientId", prescription.getPatientId());
            request.addData("doctorId", prescription.getDoctorId());
            request.addData("medication", prescription.getMedication());
            request.addData("dosage", prescription.getDosage());
            request.addData("date", prescription.getDate().toString());
            request.addData("status", prescription.getStatus().toString());
            
            BaseResponse response = connection.sendRequest(request);
            return response != null && response.isSuccess();
            
        } catch (Exception e) {
            System.err.println("Error creating prescription: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Request prescription refill
     */
    public boolean requestRefill(String prescriptionId) {
        try {
            GenericRequest request = new GenericRequest("REQUEST_REFILL", currentUser.getId());
            request.addData("prescriptionId", prescriptionId);
            
            BaseResponse response = connection.sendRequest(request);
            return response != null && response.isSuccess();
            
        } catch (Exception e) {
            System.err.println("Error requesting refill: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Record vital signs
     */
    public boolean recordVitalSigns(VitalSigns vitals) {
        try {
            GenericRequest request = new GenericRequest("RECORD_VITALS", currentUser.getId());
            request.addData("id", vitals.getId());
            request.addData("patientId", vitals.getPatientId());
            request.addData("pulse", vitals.getPulse());
            request.addData("temperature", vitals.getTemperature());
            request.addData("respiration", vitals.getRespiration());
            request.addData("bloodPressure", vitals.getBloodPressure());
            
            BaseResponse response = connection.sendRequest(request);
            return response != null && response.isSuccess();
            
        } catch (Exception e) {
            System.err.println("Error recording vitals: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get vital signs for patient
     */
    public List<VitalSigns> getVitalSigns(String patientId) {
        try {
            GenericRequest request = new GenericRequest("GET_VITALS", currentUser.getId());
            request.addData("patientId", patientId);
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess() && response instanceof GenericResponse) {
                GenericResponse genResp = (GenericResponse) response;
                Object vitalsObj = genResp.getData("vitals");
                
                if (vitalsObj instanceof List) {
                    return (List<VitalSigns>) vitalsObj;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error getting vital signs: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Get vital signs trend analysis
     */
    public String getVitalSignsTrend(String patientId, int daysBack) {
        try {
            GenericRequest request = new GenericRequest("GET_VITALS_TREND", currentUser.getId());
            request.addData("patientId", patientId);
            request.addData("daysBack", daysBack);
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess() && response instanceof GenericResponse) {
                GenericResponse genResp = (GenericResponse) response;
                return (String) genResp.getData("analysis");
            }
            
        } catch (Exception e) {
            System.err.println("Error getting vital signs trend: " + e.getMessage());
        }
        
        return "No data available";
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        currentUser = null;
        connection.disconnect();
    }
    
    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Test server connection
     */
    public boolean testConnection() {
        return connection.testConnection();
    }
    
    /**
     * Login result container
     */
    public static class LoginResult {
        private boolean success;
        private String message;
        private User user;
        
        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }
}
