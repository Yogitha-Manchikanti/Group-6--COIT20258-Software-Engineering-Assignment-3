package com.mycompany.coit20258assignment2;

/**
 * Prescription status enum
 * Must match database ENUM values: ACTIVE, COMPLETED, CANCELLED
 */
public enum PrescriptionStatus {
    ACTIVE,      // Currently active prescription (database default)
    COMPLETED,   // Prescription completed
    CANCELLED    // Prescription cancelled
}
