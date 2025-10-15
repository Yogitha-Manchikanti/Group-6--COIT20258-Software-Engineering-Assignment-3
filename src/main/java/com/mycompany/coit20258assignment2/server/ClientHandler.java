package com.mycompany.coit20258assignment2.server;

import com.mycompany.coit20258assignment2.*;
import com.mycompany.coit20258assignment2.common.*;
import com.mycompany.coit20258assignment2.server.dao.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles individual client connections and processes requests
 * Server Lead responsibility: Process client requests and send responses
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final THSServer server;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private String clientId;
    
    // DAO instances
    private final AuthDAO authDAO;
    private final AppointmentDAO appointmentDAO;
    private final PrescriptionDAO prescriptionDAO;
    private final VitalSignsDAO vitalSignsDAO;
    
    public ClientHandler(Socket clientSocket, THSServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.clientId = "Client-" + clientSocket.getInetAddress() + ":" + clientSocket.getPort();
        
        // Initialize DAOs
        this.authDAO = new AuthDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.prescriptionDAO = new PrescriptionDAO();
        this.vitalSignsDAO = new VitalSignsDAO();
    }
    
    @Override
    public void run() {
        try {
            // Initialize streams
            objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectIn = new ObjectInputStream(clientSocket.getInputStream());
            
            System.out.println("📡 Client handler started for: " + clientId);
            
            // Main message processing loop
            while (!clientSocket.isClosed()) {
                try {
                    Object message = objectIn.readObject();
                    
                    if (message instanceof BaseRequest) {
                        processRequest((BaseRequest) message);
                    } else {
                        System.err.println("❌ Unknown message type from " + clientId + ": " + message.getClass());
                    }
                    
                } catch (EOFException e) {
                    // Client disconnected normally
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("❌ Unknown class received from " + clientId + ": " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("❌ Connection error with " + clientId + ": " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    private void processRequest(BaseRequest request) {
        System.out.println("📨 Processing request: " + request.getRequestType() + " from " + clientId);
        
        try {
            BaseResponse response = null;
            
            switch (request.getRequestType()) {
                case "LOGIN":
                    response = handleLogin(request);
                    break;
                case "GET_APPOINTMENTS":
                    response = handleGetAppointments(request);
                    break;
                case "CREATE_APPOINTMENT":
                    response = handleCreateAppointment(request);
                    break;
                case "UPDATE_APPOINTMENT":
                    response = handleUpdateAppointment(request);
                    break;
                case "UPDATE_APPOINTMENT_STATUS":
                    response = handleUpdateAppointmentStatus(request);
                    break;
                case "DELETE_APPOINTMENT":
                    response = handleDeleteAppointment(request);
                    break;
                case "GET_PRESCRIPTIONS":
                    response = handleGetPrescriptions(request);
                    break;
                case "CREATE_PRESCRIPTION":
                    response = handleCreatePrescription(request);
                    break;
                case "UPDATE_PRESCRIPTION":
                    response = handleUpdatePrescription(request);
                    break;
                case "UPDATE_PRESCRIPTION_STATUS":
                    response = handleUpdatePrescriptionStatus(request);
                    break;
                case "REQUEST_REFILL":
                    response = handleRequestRefill(request);
                    break;
                case "REFILL_PRESCRIPTION":
                    response = handleRefillPrescription(request);
                    break;
                case "REJECT_PRESCRIPTION_REFILL":
                    response = handleRejectPrescriptionRefill(request);
                    break;
                case "RECORD_VITALS":
                    response = handleRecordVitals(request);
                    break;
                case "GET_VITALS":
                    response = handleGetVitals(request);
                    break;
                case "GET_VITALS_TREND":
                    response = handleGetVitalsTrend(request);
                    break;
                case "CREATE_DIAGNOSIS":
                    response = handleCreateDiagnosis(request);
                    break;
                case "CREATE_DIAGNOSIS_EXTENDED":
                    response = handleCreateDiagnosisExtended(request);
                    break;
                case "GET_DIAGNOSES":
                    response = handleGetDiagnoses(request);
                    break;
                case "CREATE_REFERRAL":
                    response = handleCreateReferral(request);
                    break;
                case "GET_REFERRALS":
                    response = handleGetReferrals(request);
                    break;
                case "CREATE_UNAVAILABILITY":
                    response = handleCreateUnavailability(request);
                    break;
                case "GET_UNAVAILABILITIES":
                    response = handleGetUnavailabilities(request);
                    break;
                case "GET_ALL_UNAVAILABILITIES":
                    response = handleGetAllUnavailabilities(request);
                    break;
                case "DELETE_UNAVAILABILITY":
                    response = handleDeleteUnavailability(request);
                    break;
                case "GET_USERS":
                    response = handleGetUsers(request);
                    break;
                case "SIGNUP":
                    response = handleSignup(request);
                    break;
                case "RESET_PASSWORD":
                    response = handleResetPassword(request);
                    break;
                case "PING":
                    response = handlePing(request);
                    break;
                default:
                    response = new GenericResponse(request.getRequestId(), "UNKNOWN", false, 
                                                 "Unknown request type: " + request.getRequestType());
            }
            
            if (response != null) {
                sendResponse(response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error processing request: " + e.getMessage());
            e.printStackTrace();
            BaseResponse errorResponse = new GenericResponse(request.getRequestId(), "ERROR", false, 
                                                           "Server error: " + e.getMessage());
            sendResponse(errorResponse);
        }
    }
    
    private BaseResponse handleLogin(BaseRequest request) {
        System.out.println("🔐 Processing login request");
        
        Map<String, Object> data = request.getData();
        String username = (String) data.get("username");
        String password = (String) data.get("password");
        
        Optional<User> userOpt = authDAO.authenticateUser(username, password);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("✅ Login successful for: " + username);
            
            GenericResponse response = new GenericResponse(
                request.getRequestId(), 
                "LOGIN_RESPONSE", 
                true, 
                "Login successful"
            );
            response.addData("userId", user.getId());
            response.addData("username", user.getUsername());
            response.addData("userType", user.getUserType().toString());
            response.addData("fullName", user.getFullName());
            response.addData("email", user.getEmail());
            
            // Add specialization for doctors
            if (user instanceof Doctor) {
                response.addData("specialization", ((Doctor) user).getSpecialization());
            }
            
            return response;
        } else {
            System.out.println("❌ Login failed for: " + username);
            return new GenericResponse(
                request.getRequestId(), 
                "LOGIN_RESPONSE", 
                false, 
                "Invalid username or password"
            );
        }
    }
    
    /**
     * Handle SIGNUP request
     */
    private BaseResponse handleSignup(BaseRequest request) {
        System.out.println("📝 Processing signup request");
        
        Map<String, Object> data = request.getData();
        String name = (String) data.get("name");
        String email = (String) data.get("email");
        String username = (String) data.get("username");
        String password = (String) data.get("password");
        String userType = (String) data.get("userType"); // "PATIENT", "DOCTOR", etc.
        String specialization = (String) data.get("specialization"); // for doctors
        
        // Validate required fields
        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            return new GenericResponse(
                request.getRequestId(),
                "SIGNUP_RESPONSE",
                false,
                "All fields are required"
            );
        }
        
        // Check if email or username already exists
        if (authDAO.userExists(username, email)) {
            return new GenericResponse(
                request.getRequestId(),
                "SIGNUP_RESPONSE",
                false,
                "Username or email already exists"
            );
        }
        
        // Create user ID
        String userId = generateUserId(userType);
        
        // Create appropriate user object
        User newUser;
        if ("DOCTOR".equals(userType)) {
            newUser = new Doctor(userId, name, email, username, password, specialization);
        } else if ("ADMINISTRATOR".equals(userType)) {
            newUser = new Administrator(userId, name, email, username, password);
        } else {
            // Default to PATIENT
            newUser = new Patient(userId, name, email, username, password);
            userType = "PATIENT";
        }
        
        // Create user in database
        boolean success = authDAO.createUser(newUser, userType);
        
        if (success) {
            System.out.println("✅ Account created successfully: " + username + " (" + userId + ")");
            return new GenericResponse(
                request.getRequestId(),
                "SIGNUP_RESPONSE",
                true,
                "Account created successfully. Please login with your credentials."
            );
        } else {
            System.err.println("❌ Failed to create account: " + username);
            return new GenericResponse(
                request.getRequestId(),
                "SIGNUP_RESPONSE",
                false,
                "Failed to create account. Please try again."
            );
        }
    }
    
    /**
     * Generate unique user ID based on type
     */
    private String generateUserId(String userType) {
        String prefix;
        switch (userType) {
            case "DOCTOR" -> prefix = "doc";
            case "ADMINISTRATOR" -> prefix = "adm";
            default -> prefix = "pat";
        }
        return prefix + String.format("%03d", (int)(Math.random() * 1000));
    }
    
    /**
     * Handle password reset request
     */
    private BaseResponse handleResetPassword(BaseRequest request) {
        System.out.println("🔑 Handling password reset");
        
        try {
            if (!(request instanceof GenericRequest)) {
                return new GenericResponse(request.getRequestId(), "RESET_PASSWORD_RESPONSE", 
                    false, "Invalid request format");
            }
            
            GenericRequest genReq = (GenericRequest) request;
            Map<String, Object> data = genReq.getData();
            
            String identifier = (String) data.get("identifier"); // username or email
            String newPassword = "reset123"; // Temporary default password
            
            if (identifier == null || identifier.trim().isEmpty()) {
                return new GenericResponse(request.getRequestId(), "RESET_PASSWORD_RESPONSE", 
                    false, "Username or email is required");
            }
            
            AuthDAO authDAO = new AuthDAO();
            boolean success = authDAO.resetPassword(identifier.trim(), newPassword);
            
            if (success) {
                return new GenericResponse(request.getRequestId(), "RESET_PASSWORD_RESPONSE", 
                    true, "Password reset successfully. Temporary password: reset123");
            } else {
                return new GenericResponse(request.getRequestId(), "RESET_PASSWORD_RESPONSE", 
                    false, "User not found");
            }
            
        } catch (Exception e) {
            System.err.println("Error resetting password: " + e.getMessage());
            e.printStackTrace();
            return new GenericResponse(request.getRequestId(), "RESET_PASSWORD_RESPONSE", 
                false, "Error resetting password: " + e.getMessage());
        }
    }
    
    private BaseResponse handleGetAppointments(BaseRequest request) {
        System.out.println("📅 Getting appointments");
        
        String userId = request.getUserId();
        String userType = (String) request.getData().get("userType");
        
        List<Appointment> appointments;
        
        if ("PATIENT".equals(userType)) {
            appointments = appointmentDAO.getAppointmentsByPatient(userId);
        } else if ("DOCTOR".equals(userType)) {
            appointments = appointmentDAO.getAppointmentsByDoctor(userId);
        } else {
            appointments = appointmentDAO.getAllAppointments();
        }
        
        GenericResponse response = new GenericResponse(
            request.getRequestId(), 
            "APPOINTMENTS_RESPONSE", 
            true, 
            "Appointments retrieved"
        );
        response.addData("appointments", appointments);
        response.addData("count", appointments.size());
        
        return response;
    }
    
    private BaseResponse handleCreateAppointment(BaseRequest request) {
        System.out.println("➕ Creating appointment");
        
        Map<String, Object> data = request.getData();
        
        String doctorId = (String) data.get("doctorId");
        LocalDate date = LocalDate.parse((String) data.get("date"));
        LocalTime time = LocalTime.parse((String) data.get("time"));
        
        // Check if doctor is available
        DoctorUnavailabilityDAO unavailabilityDAO = new DoctorUnavailabilityDAO();
        java.util.List<java.util.Map<String, Object>> unavailabilities = 
            unavailabilityDAO.getUnavailabilitiesByDoctor(doctorId);
        
        for (java.util.Map<String, Object> unavail : unavailabilities) {
            String startDateStr = (String) unavail.get("start_date");
            String endDateStr = (String) unavail.get("end_date");
            
            // Skip if dates are null
            if (startDateStr == null || endDateStr == null) {
                System.err.println("⚠️ Skipping unavailability record with null dates");
                continue;
            }
            
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            
            // Check if date falls within unavailability period
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                String startTimeStr = (String) unavail.get("start_time");
                String endTimeStr = (String) unavail.get("end_time");
                
                // Check if this is an all-day unavailability (no specific times)
                if (startTimeStr == null || endTimeStr == null) {
                    String reason = (String) unavail.get("reason");
                    System.err.println("❌ Cannot create appointment: Doctor is unavailable (all day) - " + reason);
                    return new GenericResponse(
                        request.getRequestId(),
                        "CREATE_APPOINTMENT_RESPONSE",
                        false,
                        "Cannot book: Doctor is unavailable on " + date + " (" + reason + ")"
                    );
                }
                
                // Check time range for partial day unavailability
                if (startTimeStr != null && endTimeStr != null) {
                    LocalTime startTime = LocalTime.parse(startTimeStr);
                    LocalTime endTime = LocalTime.parse(endTimeStr);
                    
                    if (!time.isBefore(startTime) && !time.isAfter(endTime)) {
                        String reason = (String) unavail.get("reason");
                        System.err.println("❌ Cannot create appointment: Doctor is unavailable during time slot - " + reason);
                        return new GenericResponse(
                            request.getRequestId(),
                            "CREATE_APPOINTMENT_RESPONSE",
                            false,
                            "Cannot book: Doctor is unavailable at " + time + " (" + reason + ")"
                        );
                    }
                }
            }
        }
        
        // Doctor is available, proceed with creating appointment
        Appointment appointment = new Appointment(
            (String) data.get("id"),
            (String) data.get("patientId"),
            doctorId,
            date,
            time,
            AppointmentStatus.valueOf((String) data.get("status"))
        );
        
        boolean success = appointmentDAO.createAppointment(appointment);
        
        return new GenericResponse(
            request.getRequestId(), 
            "CREATE_APPOINTMENT_RESPONSE", 
            success, 
            success ? "Appointment created successfully" : "Failed to create appointment"
        );
    }
    
    private BaseResponse handleUpdateAppointment(BaseRequest request) {
        System.out.println("🔄 Updating appointment (reschedule)");
        
        Map<String, Object> data = request.getData();
        String appointmentId = (String) data.get("appointmentId");
        LocalDate newDate = LocalDate.parse((String) data.get("date"));
        LocalTime newTime = LocalTime.parse((String) data.get("time"));
        String statusStr = (String) data.get("status");
        
        System.out.println("   Appointment ID: " + appointmentId);
        System.out.println("   New Date: " + newDate);
        System.out.println("   New Time: " + newTime);
        System.out.println("   New Status: " + statusStr);
        
        // Update both date/time AND status
        boolean success = appointmentDAO.updateAppointment(
            appointmentId, 
            newDate, 
            newTime, 
            AppointmentStatus.valueOf(statusStr)
        );
        
        if (success) {
            System.out.println("✅ Appointment rescheduled successfully");
        } else {
            System.err.println("❌ Failed to reschedule appointment");
        }
        
        return new GenericResponse(
            request.getRequestId(), 
            "UPDATE_APPOINTMENT_RESPONSE", 
            success, 
            success ? "Appointment updated successfully" : "Failed to update appointment"
        );
    }
    
    private BaseResponse handleUpdateAppointmentStatus(BaseRequest request) {
        System.out.println("🔄 Updating appointment status only");
        
        Map<String, Object> data = request.getData();
        String appointmentId = (String) data.get("appointmentId");
        String statusStr = (String) data.get("status");
        
        System.out.println("   Appointment ID: " + appointmentId);
        System.out.println("   New Status: " + statusStr);
        
        boolean success = appointmentDAO.updateAppointmentStatus(
            appointmentId, 
            AppointmentStatus.valueOf(statusStr)
        );
        
        if (success) {
            System.out.println("✅ Appointment status updated successfully");
        } else {
            System.err.println("❌ Failed to update appointment status");
        }
        
        return new GenericResponse(
            request.getRequestId(), 
            "UPDATE_APPOINTMENT_STATUS_RESPONSE", 
            success, 
            success ? "Appointment status updated successfully" : "Failed to update appointment status"
        );
    }
    
    private BaseResponse handleDeleteAppointment(BaseRequest request) {
        System.out.println("🗑️ Deleting appointment");
        
        String appointmentId = (String) request.getData().get("appointmentId");
        boolean success = appointmentDAO.deleteAppointment(appointmentId);
        
        return new GenericResponse(
            request.getRequestId(), 
            "DELETE_APPOINTMENT_RESPONSE", 
            success, 
            success ? "Appointment deleted successfully" : "Failed to delete appointment"
        );
    }
    
    private BaseResponse handleGetPrescriptions(BaseRequest request) {
        System.out.println("💊 Getting prescriptions");
        
        String userId = request.getUserId();
        String userType = (String) request.getData().get("userType");
        
        List<Prescription> prescriptions;
        
        if ("PATIENT".equals(userType)) {
            prescriptions = prescriptionDAO.getPrescriptionsByPatient(userId);
        } else {
            prescriptions = prescriptionDAO.getPrescriptionsByDoctor(userId);
        }
        
        GenericResponse response = new GenericResponse(
            request.getRequestId(), 
            "PRESCRIPTIONS_RESPONSE", 
            true, 
            "Prescriptions retrieved"
        );
        response.addData("prescriptions", prescriptions);
        response.addData("count", prescriptions.size());
        
        return response;
    }
    
    private BaseResponse handleCreatePrescription(BaseRequest request) {
        System.out.println("➕ Creating prescription");
        
        Map<String, Object> data = request.getData();
        
        Prescription prescription = new Prescription(
            (String) data.get("id"),
            (String) data.get("patientId"),
            (String) data.get("doctorId"),
            (String) data.get("medication"),
            (String) data.get("dosage"),
            LocalDate.parse((String) data.get("date")),
            PrescriptionStatus.valueOf((String) data.get("status"))
        );
        
        boolean success = prescriptionDAO.createPrescription(prescription);
        
        return new GenericResponse(
            request.getRequestId(), 
            "CREATE_PRESCRIPTION_RESPONSE", 
            success, 
            success ? "Prescription created successfully" : "Failed to create prescription"
        );
    }
    
    private BaseResponse handleUpdatePrescription(BaseRequest request) {
        System.out.println("Updating prescription");
        
        Map<String, Object> data = request.getData();
        String prescriptionId = (String) data.get("prescriptionId");
        String statusStr = (String) data.get("status");
        
        boolean success = prescriptionDAO.updatePrescriptionStatus(prescriptionId, PrescriptionStatus.valueOf(statusStr));
        
        return new GenericResponse(
            request.getRequestId(), 
            "UPDATE_PRESCRIPTION_RESPONSE", 
            success, 
            success ? "Prescription updated successfully" : "Failed to update prescription"
        );
    }
    
    private BaseResponse handleUpdatePrescriptionStatus(BaseRequest request) {
        System.out.println("Updating prescription status");
        
        Map<String, Object> data = request.getData();
        String prescriptionId = (String) data.get("prescriptionId");
        String statusStr = (String) data.get("status");
        
        System.out.println("   Prescription ID: " + prescriptionId);
        System.out.println("   New Status: " + statusStr);
        
        boolean success = prescriptionDAO.updatePrescriptionStatus(prescriptionId, PrescriptionStatus.valueOf(statusStr));
        
        if (success) {
            System.out.println("Prescription status updated successfully");
        } else {
            System.err.println("Failed to update prescription status");
        }
        
        return new GenericResponse(
            request.getRequestId(), 
            "UPDATE_PRESCRIPTION_STATUS_RESPONSE", 
            success, 
            success ? "Prescription status updated successfully" : "Failed to update prescription status"
        );
    }
    
    private BaseResponse handleRequestRefill(BaseRequest request) {
        System.out.println("Requesting prescription refill");
        
        String prescriptionId = (String) request.getData().get("prescriptionId");
        boolean success = prescriptionDAO.requestRefill(prescriptionId);
        
        return new GenericResponse(
            request.getRequestId(), 
            "REQUEST_REFILL_RESPONSE", 
            success, 
            success ? "Refill requested successfully" : "Failed to request refill (no refills remaining)"
        );
    }
    
    private BaseResponse handleRefillPrescription(BaseRequest request) {
        System.out.println("Approving prescription refill");
        
        String prescriptionId = (String) request.getData().get("prescriptionId");
        
        System.out.println("   Prescription ID: " + prescriptionId);
        
        boolean success = prescriptionDAO.approveRefill(prescriptionId);
        
        if (success) {
            System.out.println("Prescription refilled successfully");
        } else {
            System.err.println("Failed to refill prescription");
        }
        
        return new GenericResponse(
            request.getRequestId(), 
            "REFILL_PRESCRIPTION_RESPONSE", 
            success, 
            success ? "Prescription refilled successfully" : "Failed to refill prescription"
        );
    }
    
    private BaseResponse handleRejectPrescriptionRefill(BaseRequest request) {
        System.out.println("Rejecting prescription refill");
        
        String prescriptionId = (String) request.getData().get("prescriptionId");
        
        System.out.println("   Prescription ID: " + prescriptionId);
        
        boolean success = prescriptionDAO.rejectRefill(prescriptionId);
        
        if (success) {
            System.out.println("Prescription refill rejected successfully");
        } else {
            System.err.println("Failed to reject prescription refill");
        }
        
        return new GenericResponse(
            request.getRequestId(), 
            "REJECT_PRESCRIPTION_REFILL_RESPONSE", 
            success, 
            success ? "Prescription refill rejected successfully" : "Failed to reject prescription refill"
        );
    }
    
    private BaseResponse handleRecordVitals(BaseRequest request) {
        System.out.println("📊 Recording vital signs");
        
        Map<String, Object> data = request.getData();
        
        VitalSigns vitals = new VitalSigns(
            (String) data.get("id"),
            (String) data.get("patientId"),
            (Integer) data.get("pulse"),
            (Double) data.get("temperature"),
            (Integer) data.get("respiration"),
            (String) data.get("bloodPressure"),
            LocalDateTime.now()
        );
        
        boolean success = vitalSignsDAO.recordVitalSigns(vitals);
        
        return new GenericResponse(
            request.getRequestId(), 
            "RECORD_VITALS_RESPONSE", 
            success, 
            success ? "Vital signs recorded successfully" : "Failed to record vital signs"
        );
    }
    
    private BaseResponse handleGetVitals(BaseRequest request) {
        System.out.println("📊 Getting vital signs");
        
        String patientId = (String) request.getData().get("patientId");
        List<VitalSigns> vitals = vitalSignsDAO.getVitalSignsByPatient(patientId);
        
        GenericResponse response = new GenericResponse(
            request.getRequestId(), 
            "VITALS_RESPONSE", 
            true, 
            "Vital signs retrieved"
        );
        response.addData("vitals", vitals);
        response.addData("count", vitals.size());
        
        return response;
    }
    
    private BaseResponse handleGetVitalsTrend(BaseRequest request) {
        System.out.println("📈 Analyzing vital signs trend");
        
        String patientId = (String) request.getData().get("patientId");
        int daysBack = (Integer) request.getData().getOrDefault("daysBack", 30);
        
        String analysis = vitalSignsDAO.analyzeVitalSignsTrend(patientId, daysBack);
        
        GenericResponse response = new GenericResponse(
            request.getRequestId(), 
            "VITALS_TREND_RESPONSE", 
            true, 
            "Vital signs trend analysis complete"
        );
        response.addData("analysis", analysis);
        
        return response;
    }
    
    private BaseResponse handleCreateDiagnosis(BaseRequest request) {
        System.out.println("🩺 Creating diagnosis record");
        
        Map<String, Object> data = request.getData();
        
        String diagnosisId = (String) data.get("diagnosisId");
        String patientId = (String) data.get("patientId");
        String doctorId = (String) data.get("doctorId");
        String notes = (String) data.get("notes");
        String treatmentPlan = (String) data.get("treatmentPlan");
        String timestamp = (String) data.get("timestamp");
        
        System.out.println("   Diagnosis ID: " + diagnosisId);
        System.out.println("   Patient: " + patientId);
        System.out.println("   Doctor: " + doctorId);
        
        // Create DiagnosisDAO if needed
        DiagnosisDAO diagnosisDAO = new DiagnosisDAO();
        
        boolean success = diagnosisDAO.createDiagnosis(
            diagnosisId, 
            patientId, 
            doctorId, 
            notes, 
            treatmentPlan, 
            timestamp
        );
        
        if (success) {
            System.out.println("✅ Diagnosis saved to database");
        } else {
            System.err.println("❌ Failed to save diagnosis");
        }
        
        return new GenericResponse(
            request.getRequestId(), 
            "CREATE_DIAGNOSIS_RESPONSE", 
            success, 
            success ? "Diagnosis created successfully" : "Failed to create diagnosis"
        );
    }
    
    private BaseResponse handleCreateDiagnosisExtended(BaseRequest request) {
        System.out.println("Creating extended diagnosis record");
        
        Map<String, Object> data = request.getData();
        
        String diagnosisId = (String) data.get("diagnosisId");
        String patientId = (String) data.get("patientId");
        String doctorId = (String) data.get("doctorId");
        String diagnosisCode = (String) data.get("diagnosisCode");
        String diagnosisDescription = (String) data.get("diagnosisDescription");
        String severity = (String) data.get("severity");
        String notes = (String) data.get("notes");
        String timestamp = (String) data.get("timestamp");
        
        System.out.println("   Diagnosis ID: " + diagnosisId);
        System.out.println("   Patient: " + patientId);
        System.out.println("   Doctor: " + doctorId);
        System.out.println("   Code: " + diagnosisCode);
        System.out.println("   Severity: " + severity);
        
        // Create DiagnosisDAO
        DiagnosisDAO diagnosisDAO = new DiagnosisDAO();
        
        boolean success = diagnosisDAO.createDiagnosisExtended(
            diagnosisId, 
            patientId, 
            doctorId, 
            diagnosisCode,
            diagnosisDescription,
            severity,
            notes, 
            timestamp
        );
        
        if (success) {
            System.out.println("Extended diagnosis saved to database");
        } else {
            System.err.println("Failed to save extended diagnosis");
        }
        
        return new GenericResponse(
            request.getRequestId(), 
            "CREATE_DIAGNOSIS_EXTENDED_RESPONSE", 
            success, 
            success ? "Diagnosis created successfully" : "Failed to create diagnosis"
        );
    }
    
    private BaseResponse handleGetDiagnoses(BaseRequest request) {
        System.out.println("🩺 Getting diagnoses");
        
        Map<String, Object> data = request.getData();
        String userId = request.getUserId();
        
        DiagnosisDAO diagnosisDAO = new DiagnosisDAO();
        java.util.List<java.util.Map<String, Object>> diagnoses;
        
        // Check if filtering by specific patient or doctor
        String patientId = (String) data.get("patientId");
        String doctorId = (String) data.get("doctorId");
        
        if (patientId != null) {
            diagnoses = diagnosisDAO.getDiagnosesByPatient(patientId);
            System.out.println("   Retrieved for patient: " + patientId);
        } else if (doctorId != null) {
            diagnoses = diagnosisDAO.getDiagnosesByDoctor(doctorId);
            System.out.println("   Retrieved for doctor: " + doctorId);
        } else {
            // Default: get for the requesting user
            // Try to determine if user is patient or doctor
            diagnoses = diagnosisDAO.getDiagnosesByPatient(userId);
            System.out.println("   Retrieved for user: " + userId);
        }
        
        GenericResponse response = new GenericResponse(
            request.getRequestId(), 
            "DIAGNOSES_RESPONSE", 
            true, 
            "Diagnoses retrieved"
        );
        response.addData("diagnoses", diagnoses);
        response.addData("count", diagnoses.size());
        
        return response;
    }
    
    private BaseResponse handleCreateReferral(BaseRequest request) {
        System.out.println("Creating referral");
        
        Map<String, Object> data = request.getData();
        
        String referralId = (String) data.get("referralId");
        String patientId = (String) data.get("patientId");
        String doctorId = (String) data.get("doctorId");
        String destination = (String) data.get("destination");
        String specialty = (String) data.get("specialty");
        String reason = (String) data.get("reason");
        String referralDate = (String) data.get("referralDate");
        
        System.out.println("   Referral ID: " + referralId);
        System.out.println("   Patient: " + patientId);
        System.out.println("   Doctor: " + doctorId);
        System.out.println("   Destination: " + destination);
        System.out.println("   Specialty: " + specialty);
        
        // Create ReferralDAO
        ReferralDAO referralDAO = new ReferralDAO();
        
        boolean success = referralDAO.createReferral(
            referralId, patientId, doctorId, destination, specialty, reason, referralDate
        );
        
        if (success) {
            System.out.println("Referral saved to database");
        } else {
            System.err.println("Failed to save referral");
        }
        
        return new GenericResponse(
            request.getRequestId(), 
            "CREATE_REFERRAL_RESPONSE", 
            success, 
            success ? "Referral created successfully" : "Failed to create referral"
        );
    }
    
    /**
     * Handle GET_REFERRALS request
     */
    private BaseResponse handleGetReferrals(BaseRequest request) {
        System.out.println("Getting referrals");
        
        Map<String, Object> data = request.getData();
        String patientId = (String) data.get("patientId");
        String doctorId = (String) data.get("doctorId");
        
        System.out.println("   Patient ID filter: " + patientId);
        System.out.println("   Doctor ID filter: " + doctorId);
        
        ReferralDAO referralDAO = new ReferralDAO();
        java.util.List<java.util.Map<String, Object>> referrals = 
            referralDAO.getReferrals(patientId, doctorId);
        
        GenericResponse response = new GenericResponse(
            request.getRequestId(),
            "REFERRALS_RESPONSE",
            true,
            "Retrieved " + referrals.size() + " referrals"
        );
        response.addData("referrals", referrals);
        
        return response;
    }
    
    // ==================== Unavailability Handlers ====================
    
    private BaseResponse handleCreateUnavailability(BaseRequest request) {
        System.out.println("Creating doctor unavailability");
        
        Map<String, Object> data = request.getData();
        
        String id = (String) data.get("id");
        String doctorId = (String) data.get("doctorId");
        String startDate = (String) data.get("startDate");
        String endDate = (String) data.get("endDate");
        String startTime = (String) data.get("startTime");
        String endTime = (String) data.get("endTime");
        Boolean isAllDay = (Boolean) data.get("isAllDay");
        String reason = (String) data.get("reason");
        
        System.out.println("   ID: " + id);
        System.out.println("   Doctor: " + doctorId);
        System.out.println("   Period: " + startDate + " to " + endDate);
        System.out.println("   All Day: " + isAllDay);
        System.out.println("   Reason: " + reason);
        
        DoctorUnavailabilityDAO unavailabilityDAO = new DoctorUnavailabilityDAO();
        
        boolean success = unavailabilityDAO.createUnavailability(
            id, doctorId, startDate, endDate, startTime, endTime, 
            isAllDay != null ? isAllDay : true, reason
        );
        
        if (success) {
            System.out.println("Unavailability saved to database");
        } else {
            System.err.println("Failed to save unavailability");
        }
        
        return new GenericResponse(
            request.getRequestId(),
            "CREATE_UNAVAILABILITY_RESPONSE",
            success,
            success ? "Unavailability created successfully" : "Failed to create unavailability"
        );
    }
    
    private BaseResponse handleGetUnavailabilities(BaseRequest request) {
        System.out.println("Getting unavailabilities");
        
        Map<String, Object> data = request.getData();
        String doctorId = (String) data.get("doctorId");
        
        System.out.println("   Doctor ID: " + doctorId);
        
        DoctorUnavailabilityDAO unavailabilityDAO = new DoctorUnavailabilityDAO();
        List<Map<String, Object>> unavailabilities = unavailabilityDAO.getUnavailabilitiesByDoctor(doctorId);
        
        GenericResponse response = new GenericResponse(
            request.getRequestId(),
            "UNAVAILABILITIES_RESPONSE",
            true,
            "Retrieved " + unavailabilities.size() + " unavailability periods"
        );
        response.addData("unavailabilities", unavailabilities);
        
        return response;
    }
    
    private BaseResponse handleGetAllUnavailabilities(BaseRequest request) {
        System.out.println("Getting all unavailabilities");
        
        DoctorUnavailabilityDAO unavailabilityDAO = new DoctorUnavailabilityDAO();
        List<Map<String, Object>> unavailabilities = unavailabilityDAO.getAllUnavailabilities();
        
        System.out.println("   Found " + unavailabilities.size() + " total periods");
        
        GenericResponse response = new GenericResponse(
            request.getRequestId(),
            "ALL_UNAVAILABILITIES_RESPONSE",
            true,
            "Retrieved all unavailability periods"
        );
        response.addData("unavailabilities", unavailabilities);
        
        return response;
    }
    
    private BaseResponse handleDeleteUnavailability(BaseRequest request) {
        System.out.println("Deleting unavailability");
        
        Map<String, Object> data = request.getData();
        String id = (String) data.get("id");
        
        System.out.println("   ID: " + id);
        
        DoctorUnavailabilityDAO unavailabilityDAO = new DoctorUnavailabilityDAO();
        boolean success = unavailabilityDAO.deleteUnavailability(id);
        
        return new GenericResponse(
            request.getRequestId(),
            "DELETE_UNAVAILABILITY_RESPONSE",
            success,
            success ? "Unavailability deleted successfully" : "Failed to delete unavailability"
        );
    }
    
    // ==================== End Unavailability Handlers ====================
    
    /**
     * Handle GET_USERS request
     */
    private BaseResponse handleGetUsers(BaseRequest request) {
        System.out.println("Getting all users");
        
        AuthDAO authDAO = new AuthDAO();
        java.util.List<java.util.Map<String, Object>> users = authDAO.getAllUsers();
        
        GenericResponse response = new GenericResponse(
            request.getRequestId(),
            "USERS_RESPONSE",
            true,
            "Retrieved " + users.size() + " users"
        );
        response.addData("users", users);
        
        return response;
    }
    
    private BaseResponse handlePing(BaseRequest request) {
        return new GenericResponse(request.getRequestId(), "PONG", true, "Server is alive");
    }
    
    private void sendResponse(BaseResponse response) {
        try {
            objectOut.writeObject(response);
            objectOut.flush();
            System.out.println("📤 Response sent: " + response.getResponseType() + " to " + clientId);
        } catch (IOException e) {
            System.err.println("❌ Failed to send response to " + clientId + ": " + e.getMessage());
        }
    }
    
    private void cleanup() {
        try {
            if (objectIn != null) objectIn.close();
            if (objectOut != null) objectOut.close();
            if (clientSocket != null) clientSocket.close();
            
            server.clientDisconnected();
            System.out.println("🧹 Cleanup complete for: " + clientId);
            
        } catch (IOException e) {
            System.err.println("❌ Error during cleanup for " + clientId + ": " + e.getMessage());
        }
    }
}