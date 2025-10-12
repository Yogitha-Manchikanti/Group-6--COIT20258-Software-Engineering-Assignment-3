package com.mycompany.coit20258assignment2;

import java.io.Serializable;

/**
 * Appointment status enum
 * Must match database ENUM values: SCHEDULED, BOOKED, CONFIRMED, COMPLETED, CANCELLED, RESCHEDULED
 */
public enum AppointmentStatus implements Serializable {
    SCHEDULED,    // Default status in database
    BOOKED,       // Patient books appointment
    CONFIRMED,    // Doctor confirms
    COMPLETED,    // Appointment finished
    CANCELLED,    // Cancelled (note: database uses CANCELLED)
    RESCHEDULED   // Rescheduled to new time
}
