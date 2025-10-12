package com.mycompany.coit20258assignment2.server.dao;

import com.mycompany.coit20258assignment2.*;
import com.mycompany.coit20258assignment2.server.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Appointment operations
 * Server Lead responsibility: Database operations for appointment management
 */
public class AppointmentDAO {
    private final DatabaseManager dbManager;
    
    public AppointmentDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Create a new appointment
     */
    public boolean createAppointment(Appointment appointment) {
        String sql = """
            INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, status) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, appointment.getId());
            stmt.setString(2, appointment.getPatientId());
            stmt.setString(3, appointment.getDoctorId());
            stmt.setDate(4, Date.valueOf(appointment.getDate()));
            stmt.setTime(5, Time.valueOf(appointment.getTime()));
            stmt.setString(6, appointment.getStatus().name());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Appointment created: " + appointment.getId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error creating appointment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get all appointments for a specific patient
     */
    public List<Appointment> getAppointmentsByPatient(String patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT id, patient_id, doctor_id, appointment_date, appointment_time, status 
            FROM appointments 
            WHERE patient_id = ? 
            ORDER BY appointment_date DESC, appointment_time DESC
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getString("id"),
                    rs.getString("patient_id"),
                    rs.getString("doctor_id"),
                    rs.getDate("appointment_date").toLocalDate(),
                    rs.getTime("appointment_time").toLocalTime(),
                    AppointmentStatus.valueOf(rs.getString("status"))
                );
                appointments.add(appointment);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting patient appointments: " + e.getMessage());
        }
        
        return appointments;
    }
    
    /**
     * Get all appointments for a specific doctor
     */
    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT id, patient_id, doctor_id, appointment_date, appointment_time, status 
            FROM appointments 
            WHERE doctor_id = ? 
            ORDER BY appointment_date DESC, appointment_time DESC
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getString("id"),
                    rs.getString("patient_id"),
                    rs.getString("doctor_id"),
                    rs.getDate("appointment_date").toLocalDate(),
                    rs.getTime("appointment_time").toLocalTime(),
                    AppointmentStatus.valueOf(rs.getString("status"))
                );
                appointments.add(appointment);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting doctor appointments: " + e.getMessage());
        }
        
        return appointments;
    }
    
    /**
     * Get all appointments
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT id, patient_id, doctor_id, appointment_date, appointment_time, status 
            FROM appointments 
            ORDER BY appointment_date DESC, appointment_time DESC
            """;
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getString("id"),
                    rs.getString("patient_id"),
                    rs.getString("doctor_id"),
                    rs.getDate("appointment_date").toLocalDate(),
                    rs.getTime("appointment_time").toLocalTime(),
                    AppointmentStatus.valueOf(rs.getString("status"))
                );
                appointments.add(appointment);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting all appointments: " + e.getMessage());
        }
        
        return appointments;
    }
    
    /**
     * Get appointment by ID
     */
    public Optional<Appointment> getAppointmentById(String appointmentId) {
        String sql = """
            SELECT id, patient_id, doctor_id, appointment_date, appointment_time, status 
            FROM appointments 
            WHERE id = ?
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getString("id"),
                    rs.getString("patient_id"),
                    rs.getString("doctor_id"),
                    rs.getDate("appointment_date").toLocalDate(),
                    rs.getTime("appointment_time").toLocalTime(),
                    AppointmentStatus.valueOf(rs.getString("status"))
                );
                return Optional.of(appointment);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting appointment: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Update appointment status
     */
    public boolean updateAppointmentStatus(String appointmentId, AppointmentStatus newStatus) {
        String sql = "UPDATE appointments SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus.name());
            stmt.setString(2, appointmentId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Appointment status updated: " + appointmentId + " -> " + newStatus);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error updating appointment status: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update appointment date and time
     */
    public boolean updateAppointment(String appointmentId, LocalDate newDate, LocalTime newTime) {
        String sql = """
            UPDATE appointments 
            SET appointment_date = ?, appointment_time = ?, updated_at = CURRENT_TIMESTAMP 
            WHERE id = ?
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(newDate));
            stmt.setTime(2, Time.valueOf(newTime));
            stmt.setString(3, appointmentId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Appointment updated: " + appointmentId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error updating appointment: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete appointment
     */
    public boolean deleteAppointment(String appointmentId) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, appointmentId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Appointment deleted: " + appointmentId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error deleting appointment: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if time slot is available
     */
    public boolean isTimeSlotAvailable(String doctorId, LocalDate date, LocalTime time) {
        String sql = """
            SELECT COUNT(*) FROM appointments 
            WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? 
            AND status NOT IN ('CANCELLED')
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctorId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(time));
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error checking time slot: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get upcoming appointments (for reminders/notifications)
     */
    public List<Appointment> getUpcomingAppointments(LocalDate fromDate) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT id, patient_id, doctor_id, appointment_date, appointment_time, status 
            FROM appointments 
            WHERE appointment_date >= ? AND status IN ('SCHEDULED', 'CONFIRMED')
            ORDER BY appointment_date, appointment_time
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fromDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getString("id"),
                    rs.getString("patient_id"),
                    rs.getString("doctor_id"),
                    rs.getDate("appointment_date").toLocalDate(),
                    rs.getTime("appointment_time").toLocalTime(),
                    AppointmentStatus.valueOf(rs.getString("status"))
                );
                appointments.add(appointment);
            }
            
        } catch (SQLException e) {
            System.err.println("Database error getting upcoming appointments: " + e.getMessage());
        }
        
        return appointments;
    }
}