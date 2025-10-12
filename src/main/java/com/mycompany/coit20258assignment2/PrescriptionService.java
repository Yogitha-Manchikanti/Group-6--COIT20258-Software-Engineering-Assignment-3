package com.mycompany.coit20258assignment2;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Handles prescription requests and status changes.
 */
public class PrescriptionService {

    private final DataStore store;

    public PrescriptionService(DataStore store) {
        this.store = store;
    }

    /** Patient requests a prescription from a doctor. */
    public Prescription request(String patientId, String doctorId, String medication, String dosage) {
        String id = "RX" + UUID.randomUUID().toString().substring(0, 8);
        Prescription p = new Prescription(id, patientId, doctorId, medication, dosage,
                LocalDate.now(), PrescriptionStatus.ACTIVE);
        var list = store.getPrescriptions();
        list.add(p);
        store.savePrescriptions(list);
        return p;
    }

    /** Approve a prescription (doctor). */
    public void approve(String prescriptionId) {
        var list = store.getPrescriptions();
        var rx = list.stream().filter(r -> r.getId().equals(prescriptionId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + prescriptionId));
        rx.setStatus(PrescriptionStatus.ACTIVE);
        store.savePrescriptions(list);
    }

    /** Refill an approved prescription. */
    public void refill(String prescriptionId) {
        var list = store.getPrescriptions();
        var rx = list.stream().filter(r -> r.getId().equals(prescriptionId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + prescriptionId));
        rx.setStatus(PrescriptionStatus.COMPLETED);
        store.savePrescriptions(list);
    }

    public List<Prescription> getByPatient(String patientId) {
        return store.getPrescriptions().stream().filter(p -> p.getPatientId().equals(patientId)).toList();
    }

    public List<Prescription> getByDoctor(String doctorId) {
        return store.getPrescriptions().stream().filter(p -> p.getDoctorId().equals(doctorId)).toList();
    }
}
