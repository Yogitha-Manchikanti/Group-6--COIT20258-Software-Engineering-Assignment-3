package com.mycompany.coit20258assignment2.server.dao;

import com.mycompany.coit20258assignment2.server.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Access Object for Diagnosis operations
 * Handles saving diagnosis records to database
 */
public class DiagnosisDAO {
    private final DatabaseManager dbManager;
    
    public DiagnosisDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Create a new diagnosis record
     * Maps Java Diagnosis class to database diagnoses table
     * 
     * Database schema:
     * - diagnosis_description (text NOT NULL) <- maps from treatmentPlan
     * - notes (text) <- maps from notes
     * - diagnosis_date (date NOT NULL) <- maps from timestamp
     * - severity, diagnosis_code, appointment_id are set to NULL
     */
    public boolean createDiagnosis(String id, String patientId, String doctorId, 
                                   String notes, String treatmentPlan, String timestamp) {
        String sql = """
            INSERT INTO diagnoses 
            (id, patient_id, doctor_id, diagnosis_description, notes, diagnosis_date) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Parse timestamp to get date only
            LocalDateTime dateTime = LocalDateTime.parse(timestamp);
            LocalDate date = dateTime.toLocalDate();
            
            stmt.setString(1, id);
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.setString(4, treatmentPlan);  // treatmentPlan -> diagnosis_description
            stmt.setString(5, notes);           // notes -> notes
            stmt.setDate(6, Date.valueOf(date)); // timestamp -> diagnosis_date
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Diagnosis saved to database: " + id);
                System.out.println("   Patient: " + patientId);
                System.out.println("   Doctor: " + doctorId);
                System.out.println("   Date: " + date);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Database error creating diagnosis: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Error parsing diagnosis data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Create a new diagnosis record with all database fields
     * Supports: diagnosis_code, diagnosis_description, severity, notes, diagnosis_date
     */
    public boolean createDiagnosisExtended(String id, String patientId, String doctorId,
                                          String diagnosisCode, String diagnosisDescription,
                                          String severity, String notes, String timestamp) {
        String sql = """
            INSERT INTO diagnoses 
            (id, patient_id, doctor_id, diagnosis_code, diagnosis_description, severity, notes, diagnosis_date) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Parse timestamp to get date only
            LocalDateTime dateTime = LocalDateTime.parse(timestamp);
            LocalDate date = dateTime.toLocalDate();
            
            stmt.setString(1, id);
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.setString(4, diagnosisCode.isEmpty() ? null : diagnosisCode);
            stmt.setString(5, diagnosisDescription);
            stmt.setString(6, severity);
            stmt.setString(7, notes.isEmpty() ? null : notes);
            stmt.setDate(8, Date.valueOf(date));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Diagnosis saved to database: " + id);
                System.out.println("   Patient: " + patientId);
                System.out.println("   Doctor: " + doctorId);
                System.out.println("   Code: " + diagnosisCode);
                System.out.println("   Severity: " + severity);
                System.out.println("   Date: " + date);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error creating extended diagnosis: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error parsing diagnosis data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get all diagnoses for a patient
     * Returns full database fields including diagnosis_code, severity, appointment_id
     */
    public java.util.List<java.util.Map<String, Object>> getDiagnosesByPatient(String patientId) {
        String sql = """
            SELECT id, patient_id, doctor_id, appointment_id, 
                   diagnosis_code, diagnosis_description, severity, 
                   notes, diagnosis_date, created_at
            FROM diagnoses 
            WHERE patient_id = ? 
            ORDER BY diagnosis_date DESC, created_at DESC
            """;
        
        java.util.List<java.util.Map<String, Object>> diagnoses = new java.util.ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                java.util.Map<String, Object> diagnosis = new java.util.HashMap<>();
                diagnosis.put("id", rs.getString("id"));
                diagnosis.put("patientId", rs.getString("patient_id"));
                diagnosis.put("doctorId", rs.getString("doctor_id"));
                diagnosis.put("appointmentId", rs.getString("appointment_id"));
                diagnosis.put("diagnosisCode", rs.getString("diagnosis_code"));
                diagnosis.put("diagnosisDescription", rs.getString("diagnosis_description"));
                diagnosis.put("severity", rs.getString("severity"));
                diagnosis.put("notes", rs.getString("notes"));
                
                Date date = rs.getDate("diagnosis_date");
                diagnosis.put("diagnosisDate", date != null ? date.toLocalDate().toString() : null);
                
                Timestamp created = rs.getTimestamp("created_at");
                diagnosis.put("createdAt", created != null ? created.toLocalDateTime().toString() : null);
                
                diagnoses.add(diagnosis);
            }
            
            System.out.println("✅ Retrieved " + diagnoses.size() + " diagnoses for patient: " + patientId);
            
        } catch (SQLException e) {
            System.err.println("❌ Database error retrieving diagnoses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return diagnoses;
    }
    
    /**
     * Get all diagnoses by a doctor
     */
    public java.util.List<java.util.Map<String, Object>> getDiagnosesByDoctor(String doctorId) {
        String sql = """
            SELECT id, patient_id, doctor_id, appointment_id, 
                   diagnosis_code, diagnosis_description, severity, 
                   notes, diagnosis_date, created_at
            FROM diagnoses 
            WHERE doctor_id = ? 
            ORDER BY diagnosis_date DESC, created_at DESC
            """;
        
        java.util.List<java.util.Map<String, Object>> diagnoses = new java.util.ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                java.util.Map<String, Object> diagnosis = new java.util.HashMap<>();
                diagnosis.put("id", rs.getString("id"));
                diagnosis.put("patientId", rs.getString("patient_id"));
                diagnosis.put("doctorId", rs.getString("doctor_id"));
                diagnosis.put("appointmentId", rs.getString("appointment_id"));
                diagnosis.put("diagnosisCode", rs.getString("diagnosis_code"));
                diagnosis.put("diagnosisDescription", rs.getString("diagnosis_description"));
                diagnosis.put("severity", rs.getString("severity"));
                diagnosis.put("notes", rs.getString("notes"));
                
                Date date = rs.getDate("diagnosis_date");
                diagnosis.put("diagnosisDate", date != null ? date.toLocalDate().toString() : null);
                
                Timestamp created = rs.getTimestamp("created_at");
                diagnosis.put("createdAt", created != null ? created.toLocalDateTime().toString() : null);
                
                diagnoses.add(diagnosis);
            }
            
            System.out.println("✅ Retrieved " + diagnoses.size() + " diagnoses for doctor: " + doctorId);
            
        } catch (SQLException e) {
            System.err.println("❌ Database error retrieving diagnoses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return diagnoses;
    }
}
