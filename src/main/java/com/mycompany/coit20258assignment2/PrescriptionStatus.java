package com.mycompany.coit20258assignment2;

/**
 * Prescription status enum
 * Must match database ENUM values: PENDING, ACTIVE, APPROVED, REJECTED, COMPLETED, CANCELLED
 */
public enum PrescriptionStatus {
    PENDING,     // Prescription refill request pending doctor approval
    ACTIVE,      // Currently active prescription (database default)
    APPROVED,    // Prescription approved by doctor
    REJECTED,    // Prescription rejected by doctor
    COMPLETED,   // Prescription completed
    CANCELLED    // Prescription cancelled
}
