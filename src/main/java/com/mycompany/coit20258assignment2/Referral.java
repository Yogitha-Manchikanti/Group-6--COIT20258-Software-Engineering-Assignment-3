package com.mycompany.coit20258assignment2;

import java.io.Serializable;
import java.time.LocalDate;

public class Referral implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String patientId;
    private String doctorId;
    private String clinicName;
    private String reason;
    private LocalDate date;

    public Referral(String id, String patientId, String doctorId,
                    String clinicName, String reason, LocalDate date) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.clinicName = clinicName;
        this.reason = reason;
        this.date = date;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getClinicName() { return clinicName; }
    public String getReason() { return reason; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return id + " | " + clinicName + " | " + reason + " | " + date;
    }
}
