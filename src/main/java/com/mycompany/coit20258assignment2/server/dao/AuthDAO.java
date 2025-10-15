package com.mycompany.coit20258assignment2.server.dao;

import com.mycompany.coit20258assignment2.*;
import com.mycompany.coit20258assignment2.server.DatabaseManager;
import com.mycompany.coit20258assignment2.util.EncryptionUtil;
import java.sql.*;
import java.util.Optional;

/**
 * Data Access Object for User Authentication
 * Server Lead responsibility: Database operations for user login/auth
 * Uses EncryptionUtil for password encryption/decryption
 */
public class AuthDAO {
    private final DatabaseManager dbManager;
    
    public AuthDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Authenticate user by email/username and password
     * Decrypts stored password and compares with provided password
     */
    public Optional<User> authenticateUser(String identifier, String password) {
        String sql = """
            SELECT id, name, email, username, password, user_type, specialization 
            FROM users 
            WHERE (email = ? OR username = ?)
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String userType = rs.getString("user_type");
                String id = rs.getString("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String username = rs.getString("username");
                String encryptedPassword = rs.getString("password");
                String specialization = rs.getString("specialization");
                
                // Decrypt password and verify
                String decryptedPassword = EncryptionUtil.decrypt(encryptedPassword);
                
                if (!decryptedPassword.equals(password)) {
                    System.out.println("❌ Password mismatch for user: " + identifier);
                    return Optional.empty();
                }
                
                System.out.println("✅ Password verified for user: " + identifier);
                
                // Create appropriate user type
                User user = switch (userType) {
                    case "PATIENT" -> new Patient(id, name, email, username, decryptedPassword);
                    case "DOCTOR" -> new Doctor(id, name, email, username, decryptedPassword, specialization);
                    case "ADMINISTRATOR" -> new Administrator(id, name, email, username, decryptedPassword);
                    default -> throw new IllegalArgumentException("Unknown user type: " + userType);
                };
                
                // Log successful authentication
                logUserSession(id, "LOGIN_SUCCESS");
                
                return Optional.of(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Create a new user account
     */
    public boolean createUser(User user, String userType) {
        String sql = """
            INSERT INTO users (id, name, email, username, password, user_type, specialization) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Encrypt password before storing
            String encryptedPassword = EncryptionUtil.encrypt(user.getPassword());
            
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getUsername());
            stmt.setString(5, encryptedPassword);
            stmt.setString(6, userType);
            stmt.setString(7, user instanceof Doctor ? ((Doctor) user).getSpecialization() : null);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ User created with encrypted password: " + user.getUsername());
                logUserSession(user.getId(), "ACCOUNT_CREATED");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update user password
     * Encrypts the new password before storing
     */
    public boolean updatePassword(String userId, String newPassword) {
        String sql = "UPDATE users SET password = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Encrypt new password
            String encryptedPassword = EncryptionUtil.encrypt(newPassword);
            
            stmt.setString(1, encryptedPassword);
            stmt.setString(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Password updated with encryption for user: " + userId);
                logUserSession(userId, "PASSWORD_CHANGED");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error updating password: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Reset password by username or email
     * Encrypts the new password before storing
     */
    public boolean resetPassword(String identifier, String newPassword) {
        String sql = "UPDATE users SET password = ?, updated_at = CURRENT_TIMESTAMP WHERE username = ? OR email = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Encrypt new password
            String encryptedPassword = EncryptionUtil.encrypt(newPassword);
            
            stmt.setString(1, encryptedPassword);
            stmt.setString(2, identifier);
            stmt.setString(3, identifier);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Password reset successful for: " + identifier);
                return true;
            } else {
                System.out.println("❌ User not found: " + identifier);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error resetting password: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if username or email already exists
     */
    public boolean userExists(String username, String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error checking user existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Log user session for audit trail
     */
    private void logUserSession(String userId, String action) {
        String sql = "INSERT INTO session_logs (user_id, login_time, ip_address) VALUES (?, NOW(), ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, "SERVER_" + action); // Placeholder for IP
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error logging session: " + e.getMessage());
        }
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(String userId) {
        String sql = """
            SELECT id, name, email, username, password, user_type, specialization 
            FROM users WHERE id = ?
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String userType = rs.getString("user_type");
                String id = rs.getString("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String specialization = rs.getString("specialization");
                
                User user = switch (userType) {
                    case "PATIENT" -> new Patient(id, name, email, username, password);
                    case "DOCTOR" -> new Doctor(id, name, email, username, password, specialization);
                    case "ADMINISTRATOR" -> new Administrator(id, name, email, username, password);
                    default -> throw new IllegalArgumentException("Unknown user type: " + userType);
                };
                
                return Optional.of(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting user: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    /**
     * Get all users from database (for admin purposes)
     */
    public java.util.List<java.util.Map<String, Object>> getAllUsers() {
        String sql = """
            SELECT id, name, email, username, user_type, specialization 
            FROM users 
            ORDER BY user_type, name
            """;
        
        java.util.List<java.util.Map<String, Object>> users = new java.util.ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                java.util.Map<String, Object> user = new java.util.HashMap<>();
                user.put("id", rs.getString("id"));
                user.put("name", rs.getString("name"));
                user.put("email", rs.getString("email"));
                user.put("username", rs.getString("username"));
                user.put("user_type", rs.getString("user_type"));
                user.put("specialization", rs.getString("specialization"));
                users.add(user);
            }
            
            System.out.println(" Retrieved " + users.size() + " users from database");
            
        } catch (SQLException e) {
            System.err.println("Database error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
}
