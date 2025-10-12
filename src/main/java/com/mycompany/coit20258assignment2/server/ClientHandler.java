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
                case "REQUEST_REFILL":
                    response = handleRequestRefill(request);
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
        
        Appointment appointment = new Appointment(
            (String) data.get("id"),
            (String) data.get("patientId"),
            (String) data.get("doctorId"),
            LocalDate.parse((String) data.get("date")),
            LocalTime.parse((String) data.get("time")),
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
        System.out.println("🔄 Updating appointment");
        
        Map<String, Object> data = request.getData();
        String appointmentId = (String) data.get("appointmentId");
        String statusStr = (String) data.get("status");
        
        boolean success;
        if (statusStr != null) {
            success = appointmentDAO.updateAppointmentStatus(appointmentId, AppointmentStatus.valueOf(statusStr));
        } else {
            LocalDate newDate = LocalDate.parse((String) data.get("date"));
            LocalTime newTime = LocalTime.parse((String) data.get("time"));
            success = appointmentDAO.updateAppointment(appointmentId, newDate, newTime);
        }
        
        return new GenericResponse(
            request.getRequestId(), 
            "UPDATE_APPOINTMENT_RESPONSE", 
            success, 
            success ? "Appointment updated successfully" : "Failed to update appointment"
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
        System.out.println("🔄 Updating prescription");
        
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
    
    private BaseResponse handleRequestRefill(BaseRequest request) {
        System.out.println("🔄 Requesting prescription refill");
        
        String prescriptionId = (String) request.getData().get("prescriptionId");
        boolean success = prescriptionDAO.requestRefill(prescriptionId);
        
        return new GenericResponse(
            request.getRequestId(), 
            "REQUEST_REFILL_RESPONSE", 
            success, 
            success ? "Refill requested successfully" : "Failed to request refill (no refills remaining)"
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