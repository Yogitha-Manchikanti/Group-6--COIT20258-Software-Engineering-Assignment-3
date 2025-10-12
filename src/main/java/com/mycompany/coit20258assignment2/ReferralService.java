package com.mycompany.coit20258assignment2;

import java.util.stream.Collectors;

/** Handles doctor referrals to external clinics/hospitals. */
public class ReferralService {

    private final DataStore store;

    public ReferralService(DataStore s){ this.store = s; }

    /** Persist a new referral (validates required fields). */
    public void add(Referral r){
        // Service-side validation (defense in depth)
        if (r == null) throw new IllegalArgumentException("Referral is required.");
        if (isBlank(r.getPatientId()) || isBlank(r.getDoctorId())
                || isBlank(r.getClinicName()) || isBlank(r.getReason())) {
            throw new IllegalArgumentException("Patient ID, Doctor ID, Clinic/Hospital, and Reason are required.");
        }
        var list = store.getReferrals();
        list.add(r);
        store.saveReferrals(list);
    }

    public java.util.List<Referral> getByPatient(String pid){
        return store.getReferrals().stream()
                .filter(r -> r.getPatientId().equals(pid))
                .collect(Collectors.toList());
    }

    // --- helpers ---
    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
}
