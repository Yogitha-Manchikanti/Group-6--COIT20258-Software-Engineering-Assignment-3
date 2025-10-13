package com.mycompany.coit20258assignment2;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String patientId;
    private String doctorId;
    private LocalDate date;
    private LocalTime time;
    private AppointmentStatus status;

    public Appointment(String id, String patientId, String doctorId,
                       LocalDate date, LocalTime time, AppointmentStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public AppointmentStatus getStatus() { return status; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setTime(LocalTime time) { this.time = time; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    @Override
    public String toString() {
        return id + " | " + date + " " + time + " | P:" + patientId + " D:" + doctorId + " | " + status;
    }
}
