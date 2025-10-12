package com.mycompany.coit20258assignment2;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a period when a doctor is unavailable
 * (vacation, sick leave, conference, etc.)
 */
public class DoctorUnavailability implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String doctorId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;  // null = all day
    private LocalTime endTime;    // null = all day
    private String reason;
    private boolean isAllDay;
    
    public DoctorUnavailability(String id, String doctorId, LocalDate startDate, LocalDate endDate, 
                                LocalTime startTime, LocalTime endTime, String reason) {
        this.id = id;
        this.doctorId = doctorId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.isAllDay = (startTime == null && endTime == null);
    }
    
    // All-day unavailability constructor
    public DoctorUnavailability(String id, String doctorId, LocalDate startDate, LocalDate endDate, String reason) {
        this(id, doctorId, startDate, endDate, null, null, reason);
        this.isAllDay = true;
    }
    
    // Getters
    public String getId() { return id; }
    public String getDoctorId() { return doctorId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getReason() { return reason; }
    public boolean isAllDay() { return isAllDay; }
    
    // Setters
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStartTime(LocalTime startTime) { 
        this.startTime = startTime;
        this.isAllDay = (startTime == null && endTime == null);
    }
    public void setEndTime(LocalTime endTime) { 
        this.endTime = endTime;
        this.isAllDay = (startTime == null && endTime == null);
    }
    public void setReason(String reason) { this.reason = reason; }
    
    /**
     * Check if a given date/time conflicts with this unavailability period
     */
    public boolean conflictsWith(LocalDate date, LocalTime time) {
        // Check if date is within range
        if (date.isBefore(startDate) || date.isAfter(endDate)) {
            return false;
        }
        
        // If all-day unavailability, any time conflicts
        if (isAllDay) {
            return true;
        }
        
        // Check time range
        if (time == null || startTime == null || endTime == null) {
            return isAllDay;
        }
        
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }
    
    @Override
    public String toString() {
        String timeStr = isAllDay ? "All Day" : startTime + " - " + endTime;
        return startDate + " to " + endDate + " (" + timeStr + "): " + reason;
    }
}
