-- THS-Enhanced Database Schema
-- COIT20258 Assignment 3
-- MySQL Database Setup
-- Last Updated: October 13, 2025
--
-- Features Implemented:
-- 1. User Management (signup, password reset, encryption)
-- 2. Appointment Management (with doctor unavailability checking)
-- 3. Prescription Management (with refill requests)
-- 4. Vital Signs Monitoring (remote data submission)
-- 5. Diagnoses & Referrals
-- 6. Doctor Unavailability Management
-- 7. Session Logging & Audit Trail
-- 8. Password Encryption (Base64 encoding)
--
-- New in Latest Update:
-- - Server-based user signup (auto-generates patient IDs: pat###)
-- - Password reset functionality (resets to temporary password: reset123)
-- - Password encryption/decryption using Base64 encoding
-- - All authentication now handled by AuthDAO with database storage
-- - Removed dependency on DataStore (.dat files)
--
-- Security Features:
-- - Passwords stored in encrypted format (Base64)
-- - Automatic encryption on signup/password change
-- - Automatic decryption during authentication
--
-- Test Accounts (use original passwords to login):
-- Admin: admin / admin123
-- Doctor: drjohnson / doctor123
-- Patient: jsmith / patient123
-- 
-- Note: Passwords are encrypted in database but you login with plain text
-- The system automatically encrypts/decrypts as needed

CREATE DATABASE IF NOT EXISTS ths_enhanced;
USE ths_enhanced;

-- Users table (base table for all user types)
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type ENUM('PATIENT', 'DOCTOR', 'ADMINISTRATOR') NOT NULL,
    specialization VARCHAR(100), -- For doctors only
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id VARCHAR(50) PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    doctor_id VARCHAR(50) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('SCHEDULED', 'BOOKED', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'RESCHEDULED') DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Prescriptions table
CREATE TABLE IF NOT EXISTS prescriptions (
    id VARCHAR(50) PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    doctor_id VARCHAR(50) NOT NULL,
    appointment_id VARCHAR(50),
    medication_name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100),
    instructions TEXT,
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE',
    prescribed_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
);

-- Vital Signs table
CREATE TABLE IF NOT EXISTS vital_signs (
    id VARCHAR(50) PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    recorded_by VARCHAR(50), -- doctor or patient ID
    pulse_rate INT,
    body_temperature DECIMAL(4,2),
    respiration_rate INT,
    blood_pressure_systolic INT,
    blood_pressure_diastolic INT,
    weight DECIMAL(5,2),
    height DECIMAL(5,2),
    notes TEXT,
    recorded_date DATE NOT NULL,
    recorded_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Diagnoses table
CREATE TABLE IF NOT EXISTS diagnoses (
    id VARCHAR(50) PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    doctor_id VARCHAR(50) NOT NULL,
    appointment_id VARCHAR(50),
    diagnosis_code VARCHAR(20),
    diagnosis_description TEXT NOT NULL,
    severity ENUM('MILD', 'MODERATE', 'SEVERE', 'CRITICAL'),
    notes TEXT,
    diagnosis_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
);

-- Referrals table
CREATE TABLE IF NOT EXISTS referrals (
    id VARCHAR(50) PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    referring_doctor_id VARCHAR(50) NOT NULL,
    referred_to_doctor_id VARCHAR(50),
    appointment_id VARCHAR(50),
    specialty_required VARCHAR(100) NOT NULL,
    reason TEXT NOT NULL,
    urgency ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    status ENUM('PENDING', 'ACCEPTED', 'COMPLETED', 'REJECTED') DEFAULT 'PENDING',
    referral_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (referring_doctor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (referred_to_doctor_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
);

-- Session logs for security and audit trail
CREATE TABLE IF NOT EXISTS session_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP NULL,
    ip_address VARCHAR(45),
    session_duration INT, -- in minutes
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- System settings for configuration
CREATE TABLE IF NOT EXISTS system_settings (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value TEXT,
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_prescriptions_patient ON prescriptions(patient_id);
CREATE INDEX idx_vital_signs_patient ON vital_signs(patient_id);
CREATE INDEX idx_vital_signs_date ON vital_signs(recorded_date);
CREATE INDEX idx_diagnoses_patient ON diagnoses(patient_id);
CREATE INDEX idx_referrals_patient ON referrals(patient_id);

-- Insert default system settings
INSERT INTO system_settings (setting_key, setting_value, description) VALUES 
('server_port', '8080', 'Default server port for THS-Enhanced'),
('max_connections', '100', 'Maximum concurrent client connections'),
('session_timeout', '30', 'Session timeout in minutes'),
('encryption_enabled', 'true', 'Enable data encryption'),
('audit_logging', 'true', 'Enable audit logging');

-- Sample users with encrypted passwords (Base64 encoded)
-- Password Encryption: Base64 encoding for data security
-- Admin password: admin123 -> YWRtaW4xMjM=
-- Doctor password: doctor123 -> ZG9jdG9yMTIz
-- Patient password: patient123 -> cGF0aWVudDEyMw==
INSERT INTO users (id, name, email, username, password, user_type, specialization) VALUES 
('admin001', 'System Administrator', 'admin@ths.com', 'admin', 'YWRtaW4xMjM=', 'ADMINISTRATOR', NULL),
('doc001', 'Dr. Sarah Johnson', 'sarah.johnson@ths.com', 'drjohnson', 'ZG9jdG9yMTIz', 'DOCTOR', 'Cardiology'),
('doc002', 'Dr. Michael Chen', 'michael.chen@ths.com', 'drchen', 'ZG9jdG9yMTIz', 'DOCTOR', 'General Practice'),
('doc003', 'Dr. Emily Rodriguez', 'emily.rodriguez@ths.com', 'drodriguez', 'ZG9jdG9yMTIz', 'DOCTOR', 'Pediatrics'),
('doc004', 'Dr. James Wilson', 'james.wilson@ths.com', 'drwilson', 'ZG9jdG9yMTIz', 'DOCTOR', 'Dermatology'),
('pat001', 'John Smith', 'john.smith@email.com', 'jsmith', 'cGF0aWVudDEyMw==', 'PATIENT', NULL),
('pat002', 'Mary Johnson', 'mary.johnson@email.com', 'mjohnson', 'cGF0aWVudDEyMw==', 'PATIENT', NULL),
('pat003', 'Robert Brown', 'robert.brown@email.com', 'rbrown', 'cGF0aWVudDEyMw==', 'PATIENT', NULL),
('pat004', 'Lisa Davis', 'lisa.davis@email.com', 'ldavis', 'cGF0aWVudDEyMw==', 'PATIENT', NULL),
('pat005', 'David Wilson', 'david.wilson@email.com', 'dwilson', 'cGF0aWVudDEyMw==', 'PATIENT', NULL);

-- Sample appointments
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, status) VALUES 
('app001', 'pat001', 'doc001', '2025-10-15', '09:00:00', 'SCHEDULED'),
('app002', 'pat002', 'doc002', '2025-10-15', '10:30:00', 'CONFIRMED'),
('app003', 'pat003', 'doc003', '2025-10-16', '14:00:00', 'SCHEDULED'),
('app004', 'pat004', 'doc004', '2025-10-17', '11:00:00', 'SCHEDULED'),
('app005', 'pat005', 'doc001', '2025-10-18', '15:30:00', 'SCHEDULED'),
('app006', 'pat001', 'doc002', '2025-10-12', '09:00:00', 'COMPLETED'),
('app007', 'pat002', 'doc001', '2025-10-11', '13:30:00', 'COMPLETED');

-- Sample prescriptions
INSERT INTO prescriptions (id, patient_id, doctor_id, appointment_id, medication_name, dosage, frequency, duration, instructions, status, prescribed_date) VALUES 
('pres001', 'pat001', 'doc001', 'app006', 'Lisinopril', '10mg', 'Once daily', '30 days', 'Take with food, monitor blood pressure', 'ACTIVE', '2025-10-12'),
('pres002', 'pat002', 'doc001', 'app007', 'Amoxicillin', '500mg', 'Three times daily', '7 days', 'Complete full course, take with meals', 'ACTIVE', '2025-10-11'),
('pres003', 'pat001', 'doc002', NULL, 'Vitamin D3', '1000 IU', 'Once daily', '90 days', 'Take with largest meal of the day', 'ACTIVE', '2025-10-10');

-- Sample vital signs
INSERT INTO vital_signs (id, patient_id, recorded_by, pulse_rate, body_temperature, respiration_rate, blood_pressure_systolic, blood_pressure_diastolic, weight, height, notes, recorded_date, recorded_time) VALUES 
('vital001', 'pat001', 'doc001', 72, 98.6, 16, 120, 80, 75.5, 175.0, 'Normal vital signs', '2025-10-12', '09:15:00'),
('vital002', 'pat002', 'doc001', 68, 99.2, 18, 115, 75, 62.0, 162.0, 'Slight fever, otherwise normal', '2025-10-11', '13:45:00'),
('vital003', 'pat003', 'pat003', 85, 98.4, 20, 130, 85, 80.2, 180.0, 'Self-recorded at home', '2025-10-13', '08:00:00'),
('vital004', 'pat001', 'pat001', 74, 98.7, 16, 118, 78, 75.3, 175.0, 'Morning readings', '2025-10-13', '07:30:00');

-- Sample diagnoses
INSERT INTO diagnoses (id, patient_id, doctor_id, appointment_id, diagnosis_code, diagnosis_description, severity, notes, diagnosis_date) VALUES 
('diag001', 'pat001', 'doc001', 'app006', 'I10', 'Essential hypertension', 'MODERATE', 'Patient shows elevated blood pressure readings. Lifestyle modifications recommended along with medication.', '2025-10-12'),
('diag002', 'pat002', 'doc001', 'app007', 'J06.9', 'Acute upper respiratory infection, unspecified', 'MILD', 'Common cold symptoms. Rest and symptomatic treatment recommended.', '2025-10-11');

-- Sample referrals
INSERT INTO referrals (id, patient_id, referring_doctor_id, referred_to_doctor_id, appointment_id, specialty_required, reason, urgency, status, referral_date) VALUES 
('ref001', 'pat001', 'doc002', 'doc001', NULL, 'Cardiology', 'Patient requires cardiac evaluation due to persistent chest pain and family history of heart disease', 'HIGH', 'ACCEPTED', '2025-10-10'),
('ref002', 'pat003', 'doc002', 'doc003', NULL, 'Pediatrics', 'Child patient needs specialized pediatric consultation for developmental assessment', 'MEDIUM', 'PENDING', '2025-10-12');

COMMIT;

-- Display data summary
SELECT 'Database Setup Complete!' as Status;
SELECT 'Users' as Table_Name, COUNT(*) as Record_Count FROM users
UNION ALL
SELECT 'Appointments', COUNT(*) FROM appointments
UNION ALL
SELECT 'Prescriptions', COUNT(*) FROM prescriptions
UNION ALL
SELECT 'Vital Signs', COUNT(*) FROM vital_signs
UNION ALL
SELECT 'Diagnoses', COUNT(*) FROM diagnoses
UNION ALL
SELECT 'Referrals', COUNT(*) FROM referrals;