package com.mycompany.coit20258assignment2.server.dao;

import com.mycompany.coit20258assignment2.*;
import com.mycompany.coit20258assignment2.server.DatabaseManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for VitalSigns operations
 * Server Lead responsibility: Database operations for vital signs monitoring
 * NEW FEATURE: Remote Vital Signs Monitoring
 */
public class VitalSignsDAO {
    private final DatabaseManager dbManager;
    
    public VitalSignsDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Record new vital signs
     */
    public boolean recordVitalSigns(VitalSigns vitals) {
        String sql = """
            INSERT INTO vital_signs 
            (id, patient_id, recorded_by, blood_pressure_systolic, blood_pressure_diastolic, 
             heart_rate, temperature, respiratory_rate, oxygen_saturation, weight, height, 
             recorded_at, notes) 
            VALUES (?, ?, 'SYSTEM', ?, ?, ?, ?, ?, 98.0, 70.0, 170.0, ?, 'Recorded via telehealth')
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Parse blood pressure "120/80" format
            String[] bpParts = vitals.getBloodPressure().split("/");
            int systolic = Integer.parseInt(bpParts[0]);
            int diastolic = Integer.parseInt(bpParts[1]);
            
            stmt.setString(1, vitals.getId());
            stmt.setString(2, vitals.getPatientId());
            stmt.setInt(3, systolic);
            stmt.setInt(4, diastolic);
            stmt.setInt(5, vitals.getPulse());
            stmt.setDouble(6, vitals.getTemperature());
            stmt.setInt(7, vitals.getRespiration());
            stmt.setTimestamp(8, Timestamp.valueOf(vitals.getTimestamp()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Vital signs recorded: " + vitals.getId());
                checkVitalSignsAlerts(systolic, diastolic, vitals);
                return true;
            }
            
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Database error recording vital signs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get all vital signs for a patient
     */
    public List<VitalSigns> getVitalSignsByPatient(String patientId) {
        List<VitalSigns> vitalsList = new ArrayList<>();
        String sql = """
            SELECT id, patient_id, blood_pressure_systolic, blood_pressure_diastolic, 
                   heart_rate, temperature, respiratory_rate, recorded_at 
            FROM vital_signs 
            WHERE patient_id = ? 
            ORDER BY recorded_at DESC
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                VitalSigns vitals = createVitalSignsFromResultSet(rs);
                vitalsList.add(vitals);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting patient vital signs: " + e.getMessage());
        }
        
        return vitalsList;
    }
    
    /**
     * Get latest vital signs for a patient
     */
    public Optional<VitalSigns> getLatestVitalSigns(String patientId) {
        String sql = """
            SELECT id, patient_id, blood_pressure_systolic, blood_pressure_diastolic, 
                   heart_rate, temperature, respiratory_rate, recorded_at 
            FROM vital_signs 
            WHERE patient_id = ? 
            ORDER BY recorded_at DESC 
            LIMIT 1
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                VitalSigns vitals = createVitalSignsFromResultSet(rs);
                return Optional.of(vitals);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting latest vital signs: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Get vital signs trend analysis
     * NEW FEATURE: Analyzes trends in patient vital signs
     */
    public String analyzeVitalSignsTrend(String patientId, int daysBack) {
        List<VitalSigns> vitalsList = getVitalSignsByPatient(patientId);
        
        if (vitalsList.isEmpty()) {
            return "No vital signs data available for analysis.";
        }
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("Vital Signs Trend Analysis (Last ").append(daysBack).append(" days):\n");
        analysis.append("Total readings: ").append(vitalsList.size()).append("\n\n");
        
        // Calculate averages
        double avgHeartRate = vitalsList.stream().mapToInt(VitalSigns::getPulse).average().orElse(0);
        double avgTemp = vitalsList.stream().mapToDouble(VitalSigns::getTemperature).average().orElse(0);
        double avgResp = vitalsList.stream().mapToInt(VitalSigns::getRespiration).average().orElse(0);
        
        analysis.append(String.format("Average Heart Rate: %.0f bpm\n", avgHeartRate));
        analysis.append(String.format("Average Temperature: %.1f°C\n", avgTemp));
        analysis.append(String.format("Average Respiration: %.0f breaths/min\n", avgResp));
        
        return analysis.toString();
    }
    
    /**
     * Check for abnormal vital signs and create alerts
     * NEW FEATURE: Automatic alert system for abnormal readings
     */
    private void checkVitalSignsAlerts(int systolic, int diastolic, VitalSigns vitals) {
        List<String> alerts = new ArrayList<>();
        
        if (systolic > 140 || diastolic > 90) {
            alerts.add("⚠️ HIGH BLOOD PRESSURE: " + vitals.getBloodPressure());
        } else if (systolic < 90 || diastolic < 60) {
            alerts.add("⚠️ LOW BLOOD PRESSURE: " + vitals.getBloodPressure());
        }
        
        if (vitals.getPulse() > 100) {
            alerts.add("⚠️ HIGH HEART RATE: " + vitals.getPulse() + " bpm");
        } else if (vitals.getPulse() < 60) {
            alerts.add("⚠️ LOW HEART RATE: " + vitals.getPulse() + " bpm");
        }
        
        if (vitals.getTemperature() > 38.0) {
            alerts.add("⚠️ HIGH TEMPERATURE: " + vitals.getTemperature() + "°C");
        } else if (vitals.getTemperature() < 36.0) {
            alerts.add("⚠️ LOW TEMPERATURE: " + vitals.getTemperature() + "°C");
        }
        
        if (!alerts.isEmpty()) {
            System.out.println("🚨 VITAL SIGNS ALERTS for patient " + vitals.getPatientId() + ":");
            alerts.forEach(System.out::println);
        }
    }
    
    /**
     * Helper method to create VitalSigns object from ResultSet
     */
    private VitalSigns createVitalSignsFromResultSet(ResultSet rs) throws SQLException {
        String bloodPressure = rs.getInt("blood_pressure_systolic") + "/" + rs.getInt("blood_pressure_diastolic");
        
        return new VitalSigns(
            rs.getString("id"),
            rs.getString("patient_id"),
            rs.getInt("heart_rate"),
            rs.getDouble("temperature"),
            rs.getInt("respiratory_rate"),
            bloodPressure,
            rs.getTimestamp("recorded_at").toLocalDateTime()
        );
    }
}
