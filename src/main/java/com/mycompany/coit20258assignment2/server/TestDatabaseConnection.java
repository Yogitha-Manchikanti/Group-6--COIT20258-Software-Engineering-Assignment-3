package com.mycompany.coit20258assignment2.server;

/**
 * Quick test to verify database connection
 * Run this file directly to test MySQL connection
 */
public class TestDatabaseConnection {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  DATABASE CONNECTION TEST");
        System.out.println("========================================\n");
        
        System.out.println("Attempting to connect to MySQL...");
        System.out.println("Database: ths_enhanced");
        System.out.println("User: root");
        System.out.println("Host: localhost:3306\n");
        
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.testConnection();
        
        System.out.println("\n========================================");
        System.out.println("If you see 'Database connection successful!'");
        System.out.println("then your database is working correctly.");
        System.out.println("\nIf you see errors:");
        System.out.println("1. Check MySQL is running: net start MySQL80");
        System.out.println("2. Check database exists: mysql -u root -p -e \"SHOW DATABASES;\"");
        System.out.println("3. Run setup script: mysql -u root -p < setup_database.sql");
        System.out.println("4. Check password in DatabaseManager.java");
        System.out.println("========================================\n");
    }
}
