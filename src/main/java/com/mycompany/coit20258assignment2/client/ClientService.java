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
     * Check if connected to server
     */
    public boolean isConnected() {
        return connection != null && connection.isConnected();
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
                    String email = (String) data.get("email");
                    String specialization = (String) data.get("specialization");
                    
                    // Create appropriate user type object
                    currentUser = switch (userType) {
                        case "PATIENT" -> new Patient(userId, fullName, email != null ? email : username + "@email.com", username, password);
                        case "DOCTOR" -> new Doctor(userId, fullName, email != null ? email : username + "@email.com", username, password, specialization);
                        case "ADMINISTRATOR" -> new Administrator(userId, fullName, email != null ? email : username + "@email.com", username, password);
                        default -> new User(userId, fullName, username, password);
                    };
                    
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
     * Sign up a new user
     */
    public SignupResult signup(String name, String email, String username, String password, String userType, String specialization) {
        try {
            GenericRequest request = new GenericRequest("SIGNUP", null);
            request.addData("name", name);
            request.addData("email", email);
            request.addData("username", username);
            request.addData("password", password);
            request.addData("userType", userType != null ? userType : "PATIENT");
            if (specialization != null && !specialization.isEmpty()) {
                request.addData("specialization", specialization);
            }
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                System.out.println("✅ Signup successful");
                return new SignupResult(true, response.getMessage());
            }
            
            return new SignupResult(false, response != null ? response.getMessage() : "Signup failed");
            
        } catch (Exception e) {
            System.err.println("Signup error: " + e.getMessage());
            return new SignupResult(false, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Reset password for a user
     */
    public ResetPasswordResult resetPassword(String identifier) {
        try {
            GenericRequest request = new GenericRequest("RESET_PASSWORD", null);
            request.addData("identifier", identifier);
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                System.out.println("✅ Password reset successful");
                return new ResetPasswordResult(true, response.getMessage());
            }
            
            return new ResetPasswordResult(false, response != null ? response.getMessage() : "Password reset failed");
            
        } catch (Exception e) {
            System.err.println("Password reset error: " + e.getMessage());
            return new ResetPasswordResult(false, "Error: " + e.getMessage());
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
            
            System.out.println("📤 Sending CREATE_APPOINTMENT request to server...");
            BaseResponse response = connection.sendRequest(request);
            
            if (response == null) {
                System.err.println("❌ Server returned null response");
                return false;
            }
            
            System.out.println("📥 Server response: " + response.getMessage() + " (success=" + response.isSuccess() + ")");
            return response.isSuccess();
            
        } catch (Exception e) {
            System.err.println("❌ Error creating appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update appointment status
     */
    public boolean updateAppointmentStatus(String appointmentId, AppointmentStatus status) {
        try {
            GenericRequest request = new GenericRequest("UPDATE_APPOINTMENT_STATUS", currentUser.getId());
            request.addData("appointmentId", appointmentId);
            request.addData("status", status.toString());
            
            BaseResponse response = connection.sendRequest(request);
            return response != null && response.isSuccess();
            
        } catch (Exception e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update appointment date and time (reschedule)
     */
    public boolean updateAppointment(String appointmentId, java.time.LocalDate date, java.time.LocalTime time, AppointmentStatus status) {
        try {
            GenericRequest request = new GenericRequest("UPDATE_APPOINTMENT", currentUser.getId());
            request.addData("appointmentId", appointmentId);
            request.addData("date", date.toString());
            request.addData("time", time.toString());
            request.addData("status", status.toString());
            
            System.out.println("🔄 Updating appointment: " + appointmentId);
            System.out.println("   New date: " + date);
            System.out.println("   New time: " + time);
            System.out.println("   Status: " + status);
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                System.out.println("✅ Appointment updated successfully");
                return true;
            } else {
                System.err.println("❌ Failed to update appointment");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error updating appointment: " + e.getMessage());
            e.printStackTrace();
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
     * Update prescription status (for doctor approval)
     */
    public boolean updatePrescriptionStatus(String prescriptionId, PrescriptionStatus status) {
        try {
            System.out.println("Updating prescription status: " + prescriptionId + " to " + status);
            GenericRequest request = new GenericRequest("UPDATE_PRESCRIPTION_STATUS", currentUser.getId());
            request.addData("prescriptionId", prescriptionId);
            request.addData("status", status.toString());
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                System.out.println("Prescription status updated successfully");
                return true;
            } else {
                System.err.println("Failed to update prescription status");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error updating prescription status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Refill prescription (approve refill request)
     */
    public boolean refillPrescription(String prescriptionId) {
        try {
            System.out.println("Refilling prescription: " + prescriptionId);
            GenericRequest request = new GenericRequest("REFILL_PRESCRIPTION", currentUser.getId());
            request.addData("prescriptionId", prescriptionId);
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                System.out.println("Prescription refilled successfully");
                return true;
            } else {
                System.err.println("Failed to refill prescription");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error refilling prescription: " + e.getMessage());
            e.printStackTrace();
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
    
    // ==================== Doctor Unavailability Methods ====================
    
    /**
     * Create unavailability period for a doctor
     */
    public boolean createUnavailability(String id, String doctorId, String startDate, String endDate,
                                       String startTime, String endTime, boolean isAllDay, String reason) {
        try {
            System.out.println("Creating unavailability period...");
            GenericRequest request = new GenericRequest("CREATE_UNAVAILABILITY", currentUser.getId());
            request.addData("id", id);
            request.addData("doctorId", doctorId);
            request.addData("startDate", startDate);
            request.addData("endDate", endDate);
            request.addData("startTime", startTime);
            request.addData("endTime", endTime);
            request.addData("isAllDay", isAllDay);
            request.addData("reason", reason);
            
            GenericResponse response = (GenericResponse) connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                System.out.println("✅ Unavailability created successfully");
                return true;
            } else {
                System.err.println("❌ Failed to create unavailability: " + 
                    (response != null ? response.getMessage() : "No response"));
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error creating unavailability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get unavailability periods for a doctor
     */
    public List<Map<String, Object>> getUnavailabilities(String doctorId) {
        try {
            GenericRequest request = new GenericRequest("GET_UNAVAILABILITIES", currentUser.getId());
            request.addData("doctorId", doctorId);
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess() && response instanceof GenericResponse) {
                GenericResponse genResp = (GenericResponse) response;
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> data = (List<Map<String, Object>>) genResp.getData().get("unavailabilities");
                System.out.println("✅ Loaded " + (data != null ? data.size() : 0) + " unavailability periods");
                return data != null ? data : new java.util.ArrayList<>();
            } else {
                System.err.println("❌ Failed to get unavailabilities");
                return new java.util.ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Error getting unavailabilities: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Get all unavailability periods (for patients to check doctor availability)
     */
    public List<Map<String, Object>> getAllUnavailabilities() {
        try {
            GenericRequest request = new GenericRequest("GET_ALL_UNAVAILABILITIES", currentUser.getId());
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess() && response instanceof GenericResponse) {
                GenericResponse genResp = (GenericResponse) response;
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> data = (List<Map<String, Object>>) genResp.getData().get("unavailabilities");
                System.out.println("✅ Loaded " + (data != null ? data.size() : 0) + " total unavailability periods");
                return data != null ? data : new java.util.ArrayList<>();
            } else {
                System.err.println("❌ Failed to get all unavailabilities");
                return new java.util.ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Error getting all unavailabilities: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Delete an unavailability period
     */
    public boolean deleteUnavailability(String id) {
        try {
            System.out.println("Deleting unavailability: " + id);
            GenericRequest request = new GenericRequest("DELETE_UNAVAILABILITY", currentUser.getId());
            request.addData("id", id);
            
            GenericResponse response = (GenericResponse) connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                System.out.println("✅ Unavailability deleted successfully");
                return true;
            } else {
                System.err.println("❌ Failed to delete unavailability");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting unavailability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== End Unavailability Methods ====================
    
    /**
     * Get all users (for admin purposes)
     */
    public java.util.List<User> getUsers() {
        try {
            System.out.println("Getting all users from server...");
            GenericRequest request = new GenericRequest("GET_USERS", currentUser.getId());
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess() && response instanceof GenericResponse) {
                GenericResponse genResp = (GenericResponse) response;
                @SuppressWarnings("unchecked")
                java.util.List<java.util.Map<String, Object>> usersData = 
                    (java.util.List<java.util.Map<String, Object>>) genResp.getData().get("users");
                
                if (usersData != null) {
                    // Convert Map data to User objects
                    java.util.List<User> users = new java.util.ArrayList<>();
                    for (java.util.Map<String, Object> data : usersData) {
                        String userType = (String) data.get("user_type");
                        String id = (String) data.get("id");
                        String name = (String) data.get("name");
                        String email = (String) data.get("email");
                        String username = (String) data.get("username");
                        String specialization = (String) data.get("specialization");
                        
                        // Create appropriate user type
                        User user = switch (userType) {
                            case "PATIENT" -> new Patient(id, name, email, username, "");
                            case "DOCTOR" -> new Doctor(id, name, email, username, "", specialization);
                            case "ADMINISTRATOR" -> new Administrator(id, name, email, username, "");
                            default -> null;
                        };
                        
                        if (user != null) {
                            users.add(user);
                        }
                    }
                    System.out.println("✅ Retrieved " + users.size() + " users");
                    return users;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting users: " + e.getMessage());
            e.printStackTrace();
        }
        return new java.util.ArrayList<>();
    }
    
    /**
     * Create a referral
     */
    public boolean createReferral(String referralId, String patientId, String doctorId,
                                 String destination, String specialty, String reason, String referralDate) {
        try {
            System.out.println("Sending referral to server...");
            GenericRequest request = new GenericRequest("CREATE_REFERRAL", currentUser.getId());
            request.addData("referralId", referralId);
            request.addData("patientId", patientId);
            request.addData("doctorId", doctorId);
            request.addData("destination", destination);
            request.addData("specialty", specialty);
            request.addData("reason", reason);
            request.addData("referralDate", referralDate);
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                System.out.println("Referral created successfully");
                return true;
            } else {
                System.err.println("Server returned error: " + (response != null ? response.getMessage() : "No response"));
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error creating referral: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get referrals from server
     * @param patientId Filter by patient (null for no filter)
     * @param doctorId Filter by referring doctor (null for no filter)
     * @return List of referral data as maps
     */
    public java.util.List<java.util.Map<String, Object>> getReferrals(String patientId, String doctorId) {
        try {
            System.out.println("Getting referrals from server...");
            GenericRequest request = new GenericRequest("GET_REFERRALS", currentUser.getId());
            if (patientId != null) {
                request.addData("patientId", patientId);
            }
            if (doctorId != null) {
                request.addData("doctorId", doctorId);
            }
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess() && response instanceof GenericResponse) {
                GenericResponse genResp = (GenericResponse) response;
                @SuppressWarnings("unchecked")
                java.util.List<java.util.Map<String, Object>> referrals = 
                    (java.util.List<java.util.Map<String, Object>>) genResp.getData().get("referrals");
                
                if (referrals != null) {
                    System.out.println("✅ Retrieved " + referrals.size() + " referrals");
                    return referrals;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting referrals: " + e.getMessage());
            e.printStackTrace();
        }
        return new java.util.ArrayList<>();
    }
    
    /**
     * Create a diagnosis record (legacy - uses old Diagnosis class)
     */
    public boolean createDiagnosis(Diagnosis diagnosis) {
        try {
            System.out.println("Sending diagnosis to server...");
            GenericRequest request = new GenericRequest("CREATE_DIAGNOSIS", currentUser.getId());
            request.addData("diagnosisId", diagnosis.getId());
            request.addData("patientId", diagnosis.getPatientId());
            request.addData("doctorId", diagnosis.getDoctorId());
            request.addData("notes", diagnosis.getNotes());
            request.addData("treatmentPlan", diagnosis.getTreatmentPlan());
            request.addData("timestamp", diagnosis.getTimestamp().toString());
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                System.out.println("Diagnosis created successfully");
                return true;
            } else {
                System.err.println("Server returned error: " + (response != null ? response.getMessage() : "No response"));
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error creating diagnosis: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Create a diagnosis record with full database fields
     */
    public boolean createDiagnosisExtended(String diagnosisId, String patientId, String doctorId,
                                          String diagnosisCode, String diagnosisDescription,
                                          String severity, String notes, String timestamp) {
        try {
            System.out.println("Sending extended diagnosis to server...");
            GenericRequest request = new GenericRequest("CREATE_DIAGNOSIS_EXTENDED", currentUser.getId());
            request.addData("diagnosisId", diagnosisId);
            request.addData("patientId", patientId);
            request.addData("doctorId", doctorId);
            request.addData("diagnosisCode", diagnosisCode);
            request.addData("diagnosisDescription", diagnosisDescription);
            request.addData("severity", severity);
            request.addData("notes", notes);
            request.addData("timestamp", timestamp);
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess()) {
                System.out.println("Extended diagnosis created successfully");
                return true;
            } else {
                System.err.println("Server returned error: " + (response != null ? response.getMessage() : "No response"));
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error creating extended diagnosis: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get diagnoses for a patient or doctor
     */
    public java.util.List<java.util.Map<String, Object>> getDiagnoses(String patientId, String doctorId) {
        try {
            System.out.println("📥 Requesting diagnoses from server...");
            GenericRequest request = new GenericRequest("GET_DIAGNOSES", currentUser.getId());
            
            if (patientId != null) {
                request.addData("patientId", patientId);
            }
            if (doctorId != null) {
                request.addData("doctorId", doctorId);
            }
            
            BaseResponse response = connection.sendRequest(request);
            
            if (response != null && response.isSuccess() && response instanceof GenericResponse) {
                GenericResponse genResp = (GenericResponse) response;
                @SuppressWarnings("unchecked")
                java.util.List<java.util.Map<String, Object>> diagnoses = 
                    (java.util.List<java.util.Map<String, Object>>) genResp.getData().get("diagnoses");
                
                System.out.println("✅ Retrieved " + (diagnoses != null ? diagnoses.size() : 0) + " diagnoses");
                return diagnoses != null ? diagnoses : new java.util.ArrayList<>();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error retrieving diagnoses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new java.util.ArrayList<>();
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
    
    public static class SignupResult {
        private boolean success;
        private String message;
        
        public SignupResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public static class ResetPasswordResult {
        private boolean success;
        private String message;
        
        public ResetPasswordResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
