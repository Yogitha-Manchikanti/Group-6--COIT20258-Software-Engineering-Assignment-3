package com.mycompany.coit20258assignment2.server.dao;

import com.mycompany.coit20258assignment2.*;
import com.mycompany.coit20258assignment2.server.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Prescription operations
 * Server Lead responsibility: Database operations for prescription management
 */
public class PrescriptionDAO {
    private final DatabaseManager dbManager;
    
    public PrescriptionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Create a new prescription
     */
    public boolean createPrescription(Prescription prescription) {
        String sql = """
            INSERT INTO prescriptions 
            (id, patient_id, doctor_id, medication_name, dosage, frequency, 
             prescribed_date, status, instructions) 
            VALUES (?, ?, ?, ?, ?, 'As prescribed', ?, ?, 'Follow doctor instructions')
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, prescription.getId());
            stmt.setString(2, prescription.getPatientId());
            stmt.setString(3, prescription.getDoctorId());
            stmt.setString(4, prescription.getMedication());
            stmt.setString(5, prescription.getDosage());
            stmt.setDate(6, Date.valueOf(prescription.getDate()));
            stmt.setString(7, prescription.getStatus().name());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Prescription created: " + prescription.getId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error creating prescription: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get all prescriptions for a specific patient
     */
    public List<Prescription> getPrescriptionsByPatient(String patientId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = """
            SELECT id, patient_id, doctor_id, medication_name, dosage, frequency, 
                   prescribed_date, status, instructions 
            FROM prescriptions 
            WHERE patient_id = ? 
            ORDER BY prescribed_date DESC
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Prescription prescription = createPrescriptionFromResultSet(rs);
                prescriptions.add(prescription);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting patient prescriptions: " + e.getMessage());
        }
        
        return prescriptions;
    }
    
    /**
     * Get all prescriptions for a specific doctor
     */
    public List<Prescription> getPrescriptionsByDoctor(String doctorId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = """
            SELECT id, patient_id, doctor_id, medication_name, dosage, frequency, 
                   prescribed_date, status, instructions 
            FROM prescriptions 
            WHERE doctor_id = ? 
            ORDER BY prescribed_date DESC
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Prescription prescription = createPrescriptionFromResultSet(rs);
                prescriptions.add(prescription);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting doctor prescriptions: " + e.getMessage());
        }
        
        return prescriptions;
    }
    
    /**
     * Get prescription by ID
     */
    public Optional<Prescription> getPrescriptionById(String prescriptionId) {
        String sql = """
            SELECT id, patient_id, doctor_id, medication_name, dosage, frequency, 
                   prescribed_date, status, instructions 
            FROM prescriptions 
            WHERE id = ?
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, prescriptionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Prescription prescription = createPrescriptionFromResultSet(rs);
                return Optional.of(prescription);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting prescription: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Update prescription status
     */
    public boolean updatePrescriptionStatus(String prescriptionId, PrescriptionStatus newStatus) {
        String sql = "UPDATE prescriptions SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus.name());
            stmt.setString(2, prescriptionId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Prescription status updated: " + prescriptionId + " -> " + newStatus);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error updating prescription status: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Process refill request
     */
    public boolean requestRefill(String prescriptionId) {
        String sql = """
            UPDATE prescriptions 
            SET status = 'PENDING', 
                updated_at = CURRENT_TIMESTAMP 
            WHERE id = ? AND status = 'ACTIVE'
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, prescriptionId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Refill requested for prescription: " + prescriptionId);
                return true;
            } else {
                System.out.println("⚠️ Prescription not found or not active: " + prescriptionId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error requesting refill: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Approve refill (by doctor)
     * Can approve PENDING refill requests or renew ACTIVE prescriptions
     */
    public boolean approveRefill(String prescriptionId) {
        String sql = """
            UPDATE prescriptions 
            SET status = 'APPROVED', 
                updated_at = CURRENT_TIMESTAMP 
            WHERE id = ? AND (status = 'PENDING' OR status = 'ACTIVE')
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, prescriptionId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Refill approved for prescription: " + prescriptionId);
                return true;
            } else {
                System.out.println("⚠️ Prescription not found: " + prescriptionId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error approving refill: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Reject refill (by doctor)
     * Can reject PENDING refill requests or cancel ACTIVE prescriptions
     */
    public boolean rejectRefill(String prescriptionId) {
        String sql = """
            UPDATE prescriptions 
            SET status = 'REJECTED', 
                updated_at = CURRENT_TIMESTAMP 
            WHERE id = ? AND (status = 'PENDING' OR status = 'ACTIVE')
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, prescriptionId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Refill rejected for prescription: " + prescriptionId);
                return true;
            } else {
                System.out.println("⚠️ Prescription not found: " + prescriptionId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error rejecting refill: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get active prescriptions for a patient
     */
    public List<Prescription> getActivePrescriptions(String patientId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = """
            SELECT id, patient_id, doctor_id, medication_name, dosage, frequency, 
                   prescribed_date, status, instructions 
            FROM prescriptions 
            WHERE patient_id = ? AND status = 'ACTIVE'
            ORDER BY prescribed_date DESC
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Prescription prescription = createPrescriptionFromResultSet(rs);
                prescriptions.add(prescription);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting active prescriptions: " + e.getMessage());
        }
        
        return prescriptions;
    }
    
    /**
     * Get prescriptions needing renewal (expiring soon)
     */
    public List<Prescription> getPrescriptionsNeedingRenewal(String patientId, int daysBeforeExpiry) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = """
            SELECT id, patient_id, doctor_id, medication_name, dosage, frequency, 
                   prescribed_date, status, instructions 
            FROM prescriptions 
            WHERE patient_id = ? 
              AND status = 'ACTIVE' 
              AND prescribed_date <= DATE_SUB(CURDATE(), INTERVAL ? DAY)
            ORDER BY prescribed_date
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            stmt.setInt(2, daysBeforeExpiry);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Prescription prescription = createPrescriptionFromResultSet(rs);
                prescriptions.add(prescription);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting prescriptions needing renewal: " + e.getMessage());
        }
        
        return prescriptions;
    }
    
    /**
     * Update prescription
     */
    public boolean updatePrescription(Prescription prescription) {
        String sql = """
            UPDATE prescriptions 
            SET medication = ?, dosage = ?, status = ?,
                updated_at = CURRENT_TIMESTAMP 
            WHERE id = ?
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, prescription.getMedication());
            stmt.setString(2, prescription.getDosage());
            stmt.setString(3, prescription.getStatus().name());
            stmt.setString(4, prescription.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Prescription updated: " + prescription.getId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error updating prescription: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete prescription
     */
    public boolean deletePrescription(String prescriptionId) {
        String sql = "DELETE FROM prescriptions WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, prescriptionId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Prescription deleted: " + prescriptionId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error deleting prescription: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Helper method to create Prescription object from ResultSet
     */
    private Prescription createPrescriptionFromResultSet(ResultSet rs) throws SQLException {
        return new Prescription(
            rs.getString("id"),
            rs.getString("patient_id"),
            rs.getString("doctor_id"),
            rs.getString("medication_name"),
            rs.getString("dosage"),
            rs.getDate("prescribed_date").toLocalDate(),
            PrescriptionStatus.valueOf(rs.getString("status"))
        );
    }
}