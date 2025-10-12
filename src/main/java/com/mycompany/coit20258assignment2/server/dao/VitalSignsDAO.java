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
        // Validate temperature range (database column is DECIMAL(4,2) - max 99.99)
        if (vitals.getTemperature() < 0 || vitals.getTemperature() > 99.99) {
            System.err.println("❌ Invalid temperature value: " + vitals.getTemperature() + "°C (must be 0-99.99)");
            return false;
        }
        
        String sql = """
            INSERT INTO vital_signs 
            (id, patient_id, pulse_rate, body_temperature, respiration_rate, 
             blood_pressure_systolic, blood_pressure_diastolic, 
             recorded_date, recorded_time, notes) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Recorded via telehealth')
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Parse blood pressure "120/80" format
            String[] bpParts = vitals.getBloodPressure().split("/");
            int systolic = Integer.parseInt(bpParts[0].trim());
            int diastolic = Integer.parseInt(bpParts[1].trim());
            
            LocalDateTime timestamp = vitals.getTimestamp();
            
            stmt.setString(1, vitals.getId());
            stmt.setString(2, vitals.getPatientId());
            stmt.setInt(3, vitals.getPulse());               // pulse_rate
            stmt.setDouble(4, vitals.getTemperature());      // body_temperature
            stmt.setInt(5, vitals.getRespiration());         // respiration_rate
            stmt.setInt(6, systolic);                        // blood_pressure_systolic
            stmt.setInt(7, diastolic);                       // blood_pressure_diastolic
            stmt.setDate(8, Date.valueOf(timestamp.toLocalDate()));      // recorded_date
            stmt.setTime(9, Time.valueOf(timestamp.toLocalTime()));      // recorded_time
            
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
                   pulse_rate, body_temperature, respiration_rate, 
                   recorded_date, recorded_time 
            FROM vital_signs 
            WHERE patient_id = ? 
            ORDER BY recorded_date DESC, recorded_time DESC
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
                   pulse_rate, body_temperature, respiration_rate, 
                   recorded_date, recorded_time 
            FROM vital_signs 
            WHERE patient_id = ? 
            ORDER BY recorded_date DESC, recorded_time DESC 
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
        
        // Combine date and time into LocalDateTime
        LocalDateTime timestamp = LocalDateTime.of(
            rs.getDate("recorded_date").toLocalDate(),
            rs.getTime("recorded_time").toLocalTime()
        );
        
        return new VitalSigns(
            rs.getString("id"),
            rs.getString("patient_id"),
            rs.getInt("pulse_rate"),
            rs.getDouble("body_temperature"),
            rs.getInt("respiration_rate"),
            bloodPressure,
            timestamp
        );
    }
}
