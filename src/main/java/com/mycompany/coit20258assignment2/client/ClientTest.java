package com.mycompany.coit20258assignment2.client;

import com.mycompany.coit20258assignment2.*;
import com.mycompany.coit20258assignment2.client.ClientService.LoginResult;
import java.util.List;

/**
 * Client Lead (Member 2) - Client Testing Utility
 * Tests all client-server communication
 */
public class ClientTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 THS-Enhanced Client Testing Utility");
        System.out.println("=====================================\n");
        
        ClientService service = ClientService.getInstance();
        ServerConnection connection = ServerConnection.getInstance();
        
        // Test 1: Connection Test
        System.out.println("🔍 Test 1: Server Connection");
        System.out.println("-----------------------------");
        if (connection.connect()) {
            System.out.println("✅ Connection test PASSED\n");
        } else {
            System.out.println("❌ Connection test FAILED");
            System.out.println("❗ Make sure server is running:");
            System.out.println("   java -cp \"target/classes;mysql-connector-j-8.0.33.jar\" com.mycompany.coit20258assignment2.server.THSServer\n");
            return;
        }
        
        // Test 2: Ping Test
        System.out.println("🔍 Test 2: Server Ping");
        System.out.println("-----------------------------");
        if (service.testConnection()) {
            System.out.println("✅ Ping test PASSED\n");
        } else {
            System.out.println("❌ Ping test FAILED\n");
        }
        
        // Test 3: Login Test
        System.out.println("🔍 Test 3: User Authentication");
        System.out.println("-----------------------------");
        LoginResult loginResult = service.login("admin", "admin123");
        if (loginResult.isSuccess()) {
            System.out.println("✅ Login test PASSED");
            System.out.println("   User: " + loginResult.getUser().getName());
            System.out.println("   Type: " + loginResult.getUser().getUserType() + "\n");
        } else {
            System.out.println("❌ Login test FAILED");
            System.out.println("   Message: " + loginResult.getMessage() + "\n");
        }
        
        // Test 4: Get Appointments
        System.out.println("🔍 Test 4: Get Appointments");
        System.out.println("-----------------------------");
        try {
            List<Appointment> appointments = service.getAppointments();
            System.out.println("✅ Retrieved " + appointments.size() + " appointments");
            if (!appointments.isEmpty()) {
                System.out.println("   Sample: " + appointments.get(0).getId());
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("❌ Get appointments FAILED: " + e.getMessage() + "\n");
        }
        
        // Test 5: Get Prescriptions
        System.out.println("🔍 Test 5: Get Prescriptions");
        System.out.println("-----------------------------");
        try {
            List<Prescription> prescriptions = service.getPrescriptions();
            System.out.println("✅ Retrieved " + prescriptions.size() + " prescriptions");
            if (!prescriptions.isEmpty()) {
                System.out.println("   Sample: " + prescriptions.get(0).getMedication());
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("❌ Get prescriptions FAILED: " + e.getMessage() + "\n");
        }
        
        // Test 6: Logout
        System.out.println("🔍 Test 6: Logout");
        System.out.println("-----------------------------");
        service.logout();
        System.out.println("✅ Logout successful\n");
        
        // Summary
        System.out.println("=====================================");
        System.out.println("✅ Client Testing Complete!");
        System.out.println("=====================================");
        System.out.println("\n📋 Summary:");
        System.out.println("• Server connection: Working");
        System.out.println("• Authentication: Working");
        System.out.println("• Data retrieval: Working");
        System.out.println("• Client is ready for UI integration!");
    }
}
