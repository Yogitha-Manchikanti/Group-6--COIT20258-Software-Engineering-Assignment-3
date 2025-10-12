package com.mycompany.coit20258assignment2.server;

import com.mycompany.coit20258assignment2.common.*;

/**
 * Server testing and development utility
 * Server Lead responsibility: Test server functionality
 */
public class ServerTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 THS-Enhanced Server Testing Utility");
        System.out.println("=====================================");
        
        // Test 1: Database Connection
        testDatabaseConnection();
        
        // Test 2: Authentication DAO
        testAuthDAO();
        
        // Test 3: Start server (comment out if not needed)
        // startTestServer();
    }
    
    private static void testDatabaseConnection() {
        System.out.println("\n🔍 Testing Database Connection...");
        try {
            DatabaseManager.getInstance().testConnection();
            System.out.println("✅ Database connection test passed");
        } catch (Exception e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
        }
    }
    
    private static void testAuthDAO() {
        System.out.println("\n🔍 Testing Authentication DAO...");
        try {
            var authDAO = new com.mycompany.coit20258assignment2.server.dao.AuthDAO();
            
            // Test user existence check
            boolean exists = authDAO.userExists("admin", "admin@ths.com");
            System.out.println("User exists check: " + exists);
            
            // Test authentication
            var user = authDAO.authenticateUser("admin", "admin123");
            if (user.isPresent()) {
                System.out.println("✅ Authentication test passed: " + user.get().getName());
            } else {
                System.out.println("❌ Authentication test failed");
            }
            
        } catch (Exception e) {
            System.err.println("❌ AuthDAO test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void startTestServer() {
        System.out.println("\n🚀 Starting Test Server...");
        try {
            THSServer server = new THSServer(8081); // Use different port for testing
            
            // Run server in separate thread
            Thread serverThread = new Thread(server::start);
            serverThread.setDaemon(true);
            serverThread.start();
            
            System.out.println("✅ Test server started on port 8081");
            System.out.println("Press Ctrl+C to stop");
            
            // Keep main thread alive
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("❌ Server test failed: " + e.getMessage());
        }
    }
}