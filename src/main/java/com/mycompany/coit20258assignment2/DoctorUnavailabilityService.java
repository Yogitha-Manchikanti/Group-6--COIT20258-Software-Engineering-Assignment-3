package com.mycompany.coit20258assignment2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing doctor unavailability periods
 */
public class DoctorUnavailabilityService {
    
    // In-memory storage (in real system, would use database)
    private static final List<DoctorUnavailability> unavailabilities = new ArrayList<>();
    
    /**
     * Add a new unavailability period
     */
    public static void addUnavailability(DoctorUnavailability unavailability) {
        unavailabilities.add(unavailability);
    }
    
    /**
     * Get all unavailability periods for a doctor
     */
    public static List<DoctorUnavailability> getUnavailabilities(String doctorId) {
        return unavailabilities.stream()
            .filter(u -> u.getDoctorId().equals(doctorId))
            .collect(Collectors.toList());
    }
    
    /**
     * Delete an unavailability period by ID
     */
    public static boolean deleteUnavailability(String id) {
        return unavailabilities.removeIf(u -> u.getId().equals(id));
    }
    
    /**
     * Check if a doctor is available at a specific date/time
     * @return true if available, false if unavailable
     */
    public static boolean isDoctorAvailable(String doctorId, LocalDate date, LocalTime time) {
        return unavailabilities.stream()
            .filter(u -> u.getDoctorId().equals(doctorId))
            .noneMatch(u -> u.conflictsWith(date, time));
    }
    
    /**
     * Get the reason why a doctor is unavailable at a specific date/time
     * @return reason string, or null if doctor is available
     */
    public static String getUnavailabilityReason(String doctorId, LocalDate date, LocalTime time) {
        return unavailabilities.stream()
            .filter(u -> u.getDoctorId().equals(doctorId))
            .filter(u -> u.conflictsWith(date, time))
            .findFirst()
            .map(DoctorUnavailability::getReason)
            .orElse(null);
    }
    
    /**
     * Get all unavailability periods that conflict with a date/time
     */
    public static List<DoctorUnavailability> getConflictingUnavailabilities(String doctorId, LocalDate date, LocalTime time) {
        return unavailabilities.stream()
            .filter(u -> u.getDoctorId().equals(doctorId))
            .filter(u -> u.conflictsWith(date, time))
            .collect(Collectors.toList());
    }
    
    /**
     * Clear all unavailabilities (for testing)
     */
    public static void clearAll() {
        unavailabilities.clear();
    }
    
    /**
     * Get total count of unavailabilities
     */
    public static int count() {
        return unavailabilities.size();
    }
}
