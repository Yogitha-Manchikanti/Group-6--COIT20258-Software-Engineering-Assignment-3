package com.mycompany.coit20258assignment2;

import java.util.List;

/**
 * Validation and retrieval for VitalSigns.
 */
public class VitalSignsService {

    private final DataStore store;

    public VitalSignsService(DataStore store) {
        this.store = store;
    }

    /** Basic range checks (extend as needed). */
    public void validate(VitalSigns v) {
        if (v.getPulse() <= 0 || v.getPulse() > 220) {
            throw new IllegalArgumentException("Pulse looks invalid.");
        }
        if (v.getRespiration() <= 0 || v.getRespiration() > 60) {
            throw new IllegalArgumentException("Respiration looks invalid.");
        }
        if (v.getTemperature() < 30 || v.getTemperature() > 45) {
            throw new IllegalArgumentException("Temperature looks invalid.");
        }
    }

    public void save(VitalSigns v) {
        validate(v);
        var list = store.getVitals();
        list.add(v);
        store.saveVitals(list);
    }

    public List<VitalSigns> getByPatient(String patientId) {
        return store.getVitals().stream().filter(x -> x.getPatientId().equals(patientId)).toList();
    }
}
