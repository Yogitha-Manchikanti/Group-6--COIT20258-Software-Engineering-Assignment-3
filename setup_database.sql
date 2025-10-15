-- THS-Enhanced Database Setup Script
-- COIT20258 Software Engineering - Assignment 3
-- Group 6 - Complete database schema with sample data

DROP DATABASE IF EXISTS ths_enhanced;
CREATE DATABASE ths_enhanced;
USE ths_enhanced;

-- System settings table
CREATE TABLE IF NOT EXISTS system_settings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Users table (Patients, Doctors, Administrators)
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Base64 encoded
    user_type ENUM('PATIENT', 'DOCTOR', 'ADMINISTRATOR') NOT NULL,
    specialization VARCHAR(100), -- Only for doctors
    phone VARCHAR(20),
    address TEXT,
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
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
    status ENUM('SCHEDULED', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'NO_SHOW') DEFAULT 'SCHEDULED',
    reason TEXT,
    notes TEXT,
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
    status ENUM('PENDING', 'ACTIVE', 'APPROVED', 'REJECTED', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE',
    prescribed_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
);

-- Vital signs table
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
    icd_code VARCHAR(20),
    diagnosis_description TEXT NOT NULL,
    severity ENUM('MILD', 'MODERATE', 'SEVERE', 'CRITICAL') DEFAULT 'MILD',
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

-- Doctor unavailability table
CREATE TABLE IF NOT EXISTS doctor_unavailability (
    id VARCHAR(50) PRIMARY KEY,
    doctor_id VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Performance indexes
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_prescriptions_patient ON prescriptions(patient_id);
CREATE INDEX idx_prescriptions_doctor ON prescriptions(doctor_id);
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
('doc004', 'Dr. James Wilson', 'james.wilson@ths.com', 'drwilson', 'ZG9jdG9yMTIz', 'DOCTOR', 'Orthopedics'),
('pat001', 'John Smith', 'john.smith@email.com', 'jsmith', 'cGF0aWVudDEyMw==', 'PATIENT', NULL),
('pat002', 'Jane Doe', 'jane.doe@email.com', 'jdoe', 'cGF0aWVudDEyMw==', 'PATIENT', NULL),
('pat003', 'Bob Johnson', 'bob.johnson@email.com', 'bjohnson', 'cGF0aWVudDEyMw==', 'PATIENT', NULL),
('pat004', 'Alice Brown', 'alice.brown@email.com', 'abrown', 'cGF0aWVudDEyMw==', 'PATIENT', NULL),
('pat005', 'Charlie Davis', 'charlie.davis@email.com', 'cdavis', 'cGF0aWVudDEyMw==', 'PATIENT', NULL);

-- Sample appointments
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, status) VALUES 
('app001', 'pat001', 'doc001', '2025-10-15', '09:00:00', 'SCHEDULED'),
('app002', 'pat002', 'doc002', '2025-10-15', '10:30:00', 'CONFIRMED'),
('app003', 'pat003', 'doc003', '2025-10-16', '14:00:00', 'SCHEDULED'),
('app004', 'pat004', 'doc004', '2025-10-17', '11:00:00', 'SCHEDULED'),
('app005', 'pat005', 'doc001', '2025-10-18', '15:30:00', 'SCHEDULED'),
('app006', 'pat001', 'doc002', '2025-10-12', '09:00:00', 'COMPLETED'),
('app007', 'pat002', 'doc001', '2025-10-11', '13:30:00', 'COMPLETED');

-- Sample prescriptions with different statuses
INSERT INTO prescriptions (id, patient_id, doctor_id, appointment_id, medication_name, dosage, frequency, duration, instructions, status, prescribed_date) VALUES 
('pres001', 'pat001', 'doc001', 'app006', 'Lisinopril', '10mg', 'Once daily', '30 days', 'Take with food, monitor blood pressure', 'APPROVED', '2025-10-12'),
('pres002', 'pat002', 'doc001', 'app007', 'Amoxicillin', '500mg', 'Three times daily', '7 days', 'Complete full course, take with meals', 'REJECTED', '2025-10-11'),
('pres003', 'pat001', 'doc002', NULL, 'Vitamin D3', '1000 IU', 'Once daily', '90 days', 'Take with largest meal of the day', 'PENDING', '2025-10-10'),
('pres004', 'pat003', 'doc003', NULL, 'Children\'s Tylenol', '80mg', 'As needed', '14 days', 'For fever or pain, do not exceed 5 doses per day', 'ACTIVE', '2025-10-13'),
('pres005', 'pat004', 'doc002', NULL, 'Metformin', '500mg', 'Twice daily', '90 days', 'Take with meals, monitor blood sugar', 'COMPLETED', '2025-10-05');

-- Sample vital signs
INSERT INTO vital_signs (id, patient_id, recorded_by, pulse_rate, body_temperature, respiration_rate, blood_pressure_systolic, blood_pressure_diastolic, weight, height, notes, recorded_date, recorded_time) VALUES 
('vital001', 'pat001', 'doc001', 72, 98.6, 16, 120, 80, 75.5, 175.0, 'Normal vital signs', '2025-10-12', '09:15:00'),
('vital002', 'pat002', 'doc001', 68, 99.2, 18, 115, 75, 62.0, 162.0, 'Slight fever, otherwise normal', '2025-10-11', '13:45:00'),
('vital003', 'pat003', 'pat003', 85, 98.4, 20, 130, 85, 80.2, 180.0, 'Self-recorded at home', '2025-10-13', '08:00:00'),
('vital004', 'pat001', 'pat001', 74, 98.7, 16, 118, 78, 75.3, 175.0, 'Morning readings', '2025-10-13', '07:30:00');

-- Sample diagnoses
INSERT INTO diagnoses (id, patient_id, doctor_id, appointment_id, icd_code, diagnosis_description, severity, notes, diagnosis_date) VALUES 
('diag001', 'pat001', 'doc001', 'app006', 'I10', 'Essential hypertension', 'MODERATE', 'Patient shows elevated blood pressure readings. Lifestyle modifications recommended along with medication.', '2025-10-12'),
('diag002', 'pat002', 'doc001', 'app007', 'J06.9', 'Acute upper respiratory infection, unspecified', 'MILD', 'Common cold symptoms. Rest and symptomatic treatment recommended.', '2025-10-11');

-- Sample referrals with different statuses
INSERT INTO referrals (id, patient_id, referring_doctor_id, referred_to_doctor_id, appointment_id, specialty_required, reason, urgency, status, referral_date) VALUES 
('ref001', 'pat001', 'doc002', 'doc001', NULL, 'Cardiology', 'Patient requires cardiac evaluation due to persistent chest pain and family history of heart disease', 'HIGH', 'ACCEPTED', '2025-10-10'),
('ref002', 'pat003', 'doc002', 'doc003', NULL, 'Pediatrics', 'Child patient needs specialized pediatric consultation for developmental assessment', 'MEDIUM', 'PENDING', '2025-10-12'),
('ref003', 'pat002', 'doc001', 'doc002', NULL, 'Dermatology', 'Patient has recurring skin condition that requires specialist evaluation', 'LOW', 'COMPLETED', '2025-10-08'),
('ref004', 'pat004', 'doc002', NULL, NULL, 'Orthopedics', 'Patient complains of joint pain and mobility issues, needs orthopedic assessment', 'MEDIUM', 'REJECTED', '2025-10-09');

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

-- Show prescription status distribution
SELECT 'Prescription Status Distribution' as Info;
SELECT status, COUNT(*) as count FROM prescriptions GROUP BY status ORDER BY status;

-- Show referral status distribution  
SELECT 'Referral Status Distribution' as Info;
SELECT status, COUNT(*) as count FROM referrals GROUP BY status ORDER BY status;