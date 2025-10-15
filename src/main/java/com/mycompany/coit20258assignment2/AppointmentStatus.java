package com.mycompany.coit20258assignment2;

import java.io.Serializable;

/**
 * Appointment status enum
 * Must match database ENUM values: SCHEDULED, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW
 */
public enum AppointmentStatus implements Serializable {
    SCHEDULED,    // Default status in database - appointment initially created
    CONFIRMED,    // Doctor confirms the appointment
    COMPLETED,    // Appointment finished successfully
    CANCELLED,    // Appointment cancelled by patient or doctor
    NO_SHOW       // Patient did not show up for appointment
}
