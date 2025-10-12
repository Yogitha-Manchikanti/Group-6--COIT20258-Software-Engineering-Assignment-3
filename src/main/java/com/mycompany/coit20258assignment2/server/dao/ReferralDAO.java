package com.mycompany.coit20258assignment2.server.dao;

import com.mycompany.coit20258assignment2.server.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;

/**
 * Data Access Object for Referral operations
 * Handles saving referral records to database
 */
public class ReferralDAO {
    private final DatabaseManager dbManager;
    
    public ReferralDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Create a new referral record
     * 
     * Database schema (actual):
     * - id (varchar 50, PK)
     * - patient_id (varchar 50, FK)
     * - referring_doctor_id (varchar 50, FK) - the doctor making the referral
     * - referred_to_doctor_id (varchar 50, FK) - the doctor being referred to (nullable)
     * - appointment_id (varchar 50, FK) - nullable
     * - specialty_required (varchar 100) - the specialty needed
     * - reason (text)
     * - urgency (enum: LOW, MEDIUM, HIGH, URGENT) - defaults to MEDIUM
     * - status (enum: PENDING, ACCEPTED, COMPLETED, REJECTED) - defaults to PENDING
     * - referral_date (date)
     * - created_at, updated_at (timestamps)
     */
    public boolean createReferral(String id, String patientId, String doctorId,
                                 String destination, String specialty, String reason, String referralDate) {
        String sql = """
            INSERT INTO referrals 
            (id, patient_id, referring_doctor_id, specialty_required, reason, referral_date) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            LocalDate date = LocalDate.parse(referralDate);
            
            stmt.setString(1, id);
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.setString(4, specialty.isEmpty() ? "General" : specialty);
            stmt.setString(5, reason);
            stmt.setDate(6, Date.valueOf(date));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Referral saved to database: " + id);
                System.out.println("   Patient: " + patientId);
                System.out.println("   Referring Doctor: " + doctorId);
                System.out.println("   Specialty Required: " + specialty);
                System.out.println("   Date: " + date);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error creating referral: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error parsing referral data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get referrals by patient ID or doctor ID
     * @param patientId If not null, filter by patient
     * @param doctorId If not null, filter by referring doctor
     * @return List of referral data as maps
     */
    public java.util.List<java.util.Map<String, Object>> getReferrals(String patientId, String doctorId) {
        StringBuilder sql = new StringBuilder("""
            SELECT id, patient_id, referring_doctor_id, referred_to_doctor_id,
                   specialty_required, reason, urgency, status, referral_date,
                   created_at, updated_at
            FROM referrals
            WHERE 1=1
            """);
        
        if (patientId != null && !patientId.isEmpty()) {
            sql.append(" AND patient_id = ?");
        }
        
        if (doctorId != null && !doctorId.isEmpty()) {
            sql.append(" AND referring_doctor_id = ?");
        }
        
        sql.append(" ORDER BY referral_date DESC");
        
        java.util.List<java.util.Map<String, Object>> referrals = new java.util.ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (patientId != null && !patientId.isEmpty()) {
                stmt.setString(paramIndex++, patientId);
            }
            if (doctorId != null && !doctorId.isEmpty()) {
                stmt.setString(paramIndex++, doctorId);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                java.util.Map<String, Object> referral = new java.util.HashMap<>();
                referral.put("id", rs.getString("id"));
                referral.put("patient_id", rs.getString("patient_id"));
                referral.put("referring_doctor_id", rs.getString("referring_doctor_id"));
                referral.put("referred_to_doctor_id", rs.getString("referred_to_doctor_id"));
                referral.put("specialty_required", rs.getString("specialty_required"));
                referral.put("reason", rs.getString("reason"));
                referral.put("urgency", rs.getString("urgency"));
                referral.put("status", rs.getString("status"));
                referral.put("referral_date", rs.getDate("referral_date").toString());
                referral.put("created_at", rs.getTimestamp("created_at").toString());
                referral.put("updated_at", rs.getTimestamp("updated_at").toString());
                referrals.add(referral);
            }
            
            System.out.println("✅ Retrieved " + referrals.size() + " referrals from database");
            
        } catch (SQLException e) {
            System.err.println("Database error getting referrals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return referrals;
    }
}
