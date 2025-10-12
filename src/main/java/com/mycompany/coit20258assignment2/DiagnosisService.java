package com.mycompany.coit20258assignment2;

import java.util.stream.Collectors;

/** Stores doctor diagnoses + treatment plans. */
public class DiagnosisService {

    private final DataStore store; // persistence layer

    public DiagnosisService(DataStore s){
        this.store = s;            // inject shared DataStore (e.g., "data" folder)
    }

    /** Add a new diagnosis after validating required fields. */
    public Diagnosis add(Diagnosis d){
        if (d == null) throw new IllegalArgumentException("Diagnosis is required."); // null guard

        // Validate all required fields before persisting
        if (isBlank(d.getPatientId()) || isBlank(d.getDoctorId())
                || isBlank(d.getNotes()) || isBlank(d.getTreatmentPlan())) {
            throw new IllegalArgumentException("Patient ID, Doctor ID, Notes, and Treatment Plan are required."); // fail-fast
        }

        var list = store.getDiagnoses(); // load current list from disk (or empty list on first run)
        list.add(d);                     // mutate in-memory list
        store.saveDiagnoses(list);       // write back entire list to disk
        return d;                        // return saved entity (with id/timestamp)
    }

    /** Get all diagnoses for a patient ID. */
    public java.util.List<Diagnosis> getByPatient(String pid){
        return store.getDiagnoses().stream()                // stream over all stored diagnoses
                .filter(dx -> dx.getPatientId().equals(pid))// only those matching patient ID
                .collect(Collectors.toList());              // materialize to List
    }

    /** Get all diagnoses authored by a doctor ID. */
    public java.util.List<Diagnosis> getByDoctor(String did){
        return store.getDiagnoses().stream()                // stream over all stored diagnoses
                .filter(dx -> dx.getDoctorId().equals(did)) // only those authored by this doctor
                .collect(Collectors.toList());              // materialize to List
    }

    // utility
    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); } // shared blank check
}
