package com.mycompany.coit20258assignment2;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Diagnosis implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String patientId;
    private String doctorId;
    private String notes;
    private String treatmentPlan;
    private LocalDateTime timestamp;

    public Diagnosis(String id, String patientId, String doctorId,
                     String notes, String treatmentPlan, LocalDateTime timestamp) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.notes = notes;
        this.treatmentPlan = treatmentPlan;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getNotes() { return notes; }
    public String getTreatmentPlan() { return treatmentPlan; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
