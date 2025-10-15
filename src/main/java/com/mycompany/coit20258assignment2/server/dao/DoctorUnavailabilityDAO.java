package com.mycompany.coit20258assignment2.server.dao;

import com.mycompany.coit20258assignment2.server.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Doctor Unavailability operations
 */
public class DoctorUnavailabilityDAO {
    private final DatabaseManager dbManager;
    
    public DoctorUnavailabilityDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Create a new unavailability period
     */
    public boolean createUnavailability(String id, String doctorId, String startDate, String endDate,
                                       String startTime, String endTime, boolean isAllDay, String reason) {
        String sql = """
            INSERT INTO doctor_unavailability 
            (id, doctor_id, start_date, end_date, start_time, end_time, reason) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.setString(2, doctorId);
            stmt.setDate(3, Date.valueOf(LocalDate.parse(startDate)));
            stmt.setDate(4, Date.valueOf(LocalDate.parse(endDate)));
            
            if (isAllDay || startTime == null || startTime.isEmpty()) {
                stmt.setNull(5, Types.TIME);
                stmt.setNull(6, Types.TIME);
            } else {
                stmt.setTime(5, Time.valueOf(LocalTime.parse(startTime)));
                stmt.setTime(6, Time.valueOf(LocalTime.parse(endTime)));
            }
            
            stmt.setString(7, reason);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Unavailability created: " + id);
                System.out.println("   Doctor: " + doctorId);
                System.out.println("   Period: " + startDate + " to " + endDate);
                System.out.println("   Reason: " + reason);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error creating unavailability: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error parsing unavailability data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get all unavailability periods for a doctor
     */
    public List<Map<String, Object>> getUnavailabilitiesByDoctor(String doctorId) {
        String sql = """
            SELECT id, doctor_id, start_date, end_date, start_time, end_time, reason, created_at
            FROM doctor_unavailability
            WHERE doctor_id = ?
            ORDER BY start_date DESC, start_time DESC
            """;
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> unavailability = new HashMap<>();
                    unavailability.put("id", rs.getString("id"));
                    unavailability.put("doctorId", rs.getString("doctor_id"));
                    unavailability.put("startDate", rs.getDate("start_date").toString());
                    unavailability.put("endDate", rs.getDate("end_date").toString());
                    
                    Time startTime = rs.getTime("start_time");
                    Time endTime = rs.getTime("end_time");
                    unavailability.put("startTime", startTime != null ? startTime.toString() : null);
                    unavailability.put("endTime", endTime != null ? endTime.toString() : null);
                    
                    // Determine if it's all day based on whether time fields are null
                    boolean isAllDay = (startTime == null || endTime == null);
                    unavailability.put("isAllDay", isAllDay);
                    
                    unavailability.put("reason", rs.getString("reason"));
                    unavailability.put("createdAt", rs.getTimestamp("created_at").toString());
                    
                    result.add(unavailability);
                }
            }
            
            System.out.println("✅ Retrieved " + result.size() + " unavailability periods for doctor: " + doctorId);
            
        } catch (SQLException e) {
            System.err.println("Database error getting unavailabilities: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Get all unavailability periods (for patients to see all doctors' unavailability)
     */
    public List<Map<String, Object>> getAllUnavailabilities() {
        String sql = """
            SELECT id, doctor_id, start_date, end_date, start_time, end_time, reason
            FROM doctor_unavailability
            ORDER BY start_date DESC, start_time DESC
            """;
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> unavailability = new HashMap<>();
                unavailability.put("id", rs.getString("id"));
                unavailability.put("doctorId", rs.getString("doctor_id"));
                unavailability.put("startDate", rs.getDate("start_date").toString());
                unavailability.put("endDate", rs.getDate("end_date").toString());
                
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");
                unavailability.put("startTime", startTime != null ? startTime.toString() : null);
                unavailability.put("endTime", endTime != null ? endTime.toString() : null);
                
                // Determine if it's all day based on whether time fields are null
                boolean isAllDay = (startTime == null || endTime == null);
                unavailability.put("isAllDay", isAllDay);
                
                unavailability.put("reason", rs.getString("reason"));
                
                result.add(unavailability);
            }
            
            System.out.println("✅ Retrieved " + result.size() + " total unavailability periods");
            
        } catch (SQLException e) {
            System.err.println("Database error getting all unavailabilities: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Delete an unavailability period
     */
    public boolean deleteUnavailability(String id) {
        String sql = "DELETE FROM doctor_unavailability WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Unavailability deleted: " + id);
                return true;
            } else {
                System.out.println("⚠️ Unavailability not found: " + id);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error deleting unavailability: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
