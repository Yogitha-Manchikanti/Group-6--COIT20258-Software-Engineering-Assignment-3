package com.mycompany.coit20258assignment2.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Multi-threaded TCP Server for THS-Enhanced
 * Server Lead responsibility: Handle multiple client connections
 * 
 * Features:
 * - Multi-threaded client handling
 * - Connection pooling
 * - Graceful shutdown
 * - Client session management
 */
public class THSServer {
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_CLIENTS = 100;
    
    private ServerSocket serverSocket;
    private ExecutorService clientThreadPool;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private volatile boolean isRunning = false;
    
    public THSServer() {
        this(DEFAULT_PORT);
    }
    
    public THSServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.clientThreadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
            System.out.println("THS-Enhanced Server initialized on port " + port);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server", e);
        }
    }
    
    public void start() {
        isRunning = true;
        System.out.println("🚀 THS-Enhanced Server starting...");
        
        // Test database connection
        DatabaseManager.getInstance().testConnection();
        
        System.out.println("✅ Server ready for client connections on port " + serverSocket.getLocalPort());
        
        while (isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                
                if (activeConnections.get() >= MAX_CLIENTS) {
                    System.out.println("❌ Maximum clients reached. Rejecting connection from: " 
                                     + clientSocket.getInetAddress());
                    clientSocket.close();
                    continue;
                }
                
                activeConnections.incrementAndGet();
                System.out.println("✅ New client connected: " + clientSocket.getInetAddress() 
                                 + " (Active connections: " + activeConnections.get() + ")");
                
                // Handle client in separate thread
                clientThreadPool.submit(new ClientHandler(clientSocket, this));
                
            } catch (IOException e) {
                if (isRunning) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }
    
    public void stop() {
        System.out.println("🛑 Shutting down THS-Enhanced Server...");
        isRunning = false;
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            clientThreadPool.shutdown();
            if (!clientThreadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                clientThreadPool.shutdownNow();
            }
            
            System.out.println("✅ Server shutdown complete");
        } catch (Exception e) {
            System.err.println("Error during server shutdown: " + e.getMessage());
        }
    }
    
    public void clientDisconnected() {
        int current = activeConnections.decrementAndGet();
        System.out.println("📤 Client disconnected (Active connections: " + current + ")");
    }
    
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default: " + DEFAULT_PORT);
            }
        }
        
        THSServer server = new THSServer(port);
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        
        // Start the server
        server.start();
    }
}