package com.mycompany.coit20258assignment2;

import java.io.Serializable;
import java.time.LocalDateTime;

public class VitalSigns implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String patientId;
    private int pulse;
    private double temperature;
    private int respiration;
    private String bloodPressure;
    private LocalDateTime timestamp;

    public VitalSigns(String id, String patientId, int pulse, double temperature,
                      int respiration, String bloodPressure, LocalDateTime timestamp) {
        this.id = id;
        this.patientId = patientId;
        this.pulse = pulse;
        this.temperature = temperature;
        this.respiration = respiration;
        this.bloodPressure = bloodPressure;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public int getPulse() { return pulse; }
    public double getTemperature() { return temperature; }
    public int getRespiration() { return respiration; }
    public String getBloodPressure() { return bloodPressure; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
