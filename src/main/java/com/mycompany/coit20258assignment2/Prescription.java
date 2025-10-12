package com.mycompany.coit20258assignment2;

import java.io.Serializable;
import java.time.LocalDate;

public class Prescription implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String patientId;
    private String doctorId;
    private String medication;
    private String dosage;
    private LocalDate date;
    private PrescriptionStatus status;

    public Prescription(String id, String patientId, String doctorId, String medication,
                        String dosage, LocalDate date, PrescriptionStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.medication = medication;
        this.dosage = dosage;
        this.date = date;
        this.status = status;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getMedication() { return medication; }
    public String getDosage() { return dosage; }
    public LocalDate getDate() { return date; }
    public PrescriptionStatus getStatus() { return status; }

    public void setStatus(PrescriptionStatus status) { this.status = status; }

    @Override
    public String toString() {
        return id + " | " + medication + " (" + dosage + ") | " + status;
    }
}
