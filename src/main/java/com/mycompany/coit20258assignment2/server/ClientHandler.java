package com.mycompany.coit20258assignment2.server;

import com.mycompany.coit20258assignment2.common.*;
import java.io.*;
import java.net.Socket;

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
    
    public ClientHandler(Socket clientSocket, THSServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.clientId = "Client-" + clientSocket.getInetAddress() + ":" + clientSocket.getPort();
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
                case "GET_PRESCRIPTIONS":
                    response = handleGetPrescriptions(request);
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
            BaseResponse errorResponse = new GenericResponse(request.getRequestId(), "ERROR", false, 
                                                           "Server error: " + e.getMessage());
            sendResponse(errorResponse);
        }
    }
    
    private BaseResponse handleLogin(BaseRequest request) {
        // TODO: Implement login logic with AuthDAO
        System.out.println("🔐 Processing login request for user: " + request.getUserId());
        
        // Placeholder response
        return new GenericResponse(request.getRequestId(), "LOGIN_RESPONSE", true, 
                                 "Login successful (placeholder)");
    }
    
    private BaseResponse handleGetAppointments(BaseRequest request) {
        // TODO: Implement with AppointmentDAO
        System.out.println("📅 Getting appointments for user: " + request.getUserId());
        
        return new GenericResponse(request.getRequestId(), "APPOINTMENTS_RESPONSE", true, 
                                 "Appointments retrieved (placeholder)");
    }
    
    private BaseResponse handleCreateAppointment(BaseRequest request) {
        // TODO: Implement with AppointmentDAO
        System.out.println("➕ Creating appointment for user: " + request.getUserId());
        
        return new GenericResponse(request.getRequestId(), "CREATE_APPOINTMENT_RESPONSE", true, 
                                 "Appointment created (placeholder)");
    }
    
    private BaseResponse handleGetPrescriptions(BaseRequest request) {
        // TODO: Implement with PrescriptionDAO
        System.out.println("💊 Getting prescriptions for user: " + request.getUserId());
        
        return new GenericResponse(request.getRequestId(), "PRESCRIPTIONS_RESPONSE", true, 
                                 "Prescriptions retrieved (placeholder)");
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
    
    // Generic response class for simple responses
    private static class GenericResponse extends BaseResponse {
        public GenericResponse(String requestId, String responseType, boolean success, String message) {
            super(requestId, responseType, success, message);
        }
    }
}