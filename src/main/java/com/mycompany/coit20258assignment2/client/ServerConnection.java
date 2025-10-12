package com.mycompany.coit20258assignment2.client;

import com.mycompany.coit20258assignment2.common.*;
import java.io.*;
import java.net.Socket;
import java.net.ConnectException;


public class ServerConnection {
    private static ServerConnection instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String serverHost = "localhost";
    private int serverPort = 8080;
    private boolean connected = false;
    
    private ServerConnection() {
        // Private constructor for singleton
    }
    
    public static ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }
    
    /**
     * Connect to the server
     */
    public boolean connect() {
        try {
            System.out.println("🔌 Connecting to server at " + serverHost + ":" + serverPort);
            
            socket = new Socket(serverHost, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            connected = true;
            System.out.println("✅ Connected to server successfully!");
            return true;
            
        } catch (ConnectException e) {
            System.err.println("❌ Connection refused. Is the server running?");
            System.err.println("   Start server with: java -cp \"target/classes;mysql-connector-j-8.0.33.jar\" com.mycompany.coit20258assignment2.server.THSServer");
            connected = false;
            return false;
        } catch (IOException e) {
            System.err.println("❌ Connection error: " + e.getMessage());
            connected = false;
            return false;
        }
    }
    
    /**
     * Disconnect from the server
     */
    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            connected = false;
            System.out.println("🔌 Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
    
    /**
     * Send request to server and receive response
     */
    public BaseResponse sendRequest(BaseRequest request) {
        if (!connected) {
            if (!connect()) {
                return createErrorResponse(request.getRequestId(), "Not connected to server");
            }
        }
        
        try {
            // Send request
            out.writeObject(request);
            out.flush();
            System.out.println("📤 Sent: " + request.getRequestType());
            
            // Receive response
            Object response = in.readObject();
            System.out.println("📥 Received: " + ((BaseResponse)response).getResponseType());
            
            return (BaseResponse) response;
            
        } catch (IOException e) {
            System.err.println("❌ Communication error: " + e.getMessage());
            connected = false;
            return createErrorResponse(request.getRequestId(), "Communication error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Unknown response type: " + e.getMessage());
            return createErrorResponse(request.getRequestId(), "Unknown response type");
        }
    }
    
    /**
     * Test server connection
     */
    public boolean testConnection() {
        try {
            BaseRequest pingRequest = new GenericRequest("PING", "system");
            BaseResponse response = sendRequest(pingRequest);
            return response != null && response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Create error response when connection fails
     */
    private BaseResponse createErrorResponse(String requestId, String message) {
        return new BaseResponse(requestId, "ERROR", false, message) {};
    }
    
    public boolean isConnected() {
        return connected && socket != null && socket.isConnected() && !socket.isClosed();
    }
    
    public void setServerHost(String host) {
        this.serverHost = host;
    }
    
    public void setServerPort(int port) {
        this.serverPort = port;
    }
}
