package com.mycompany.coit20258assignment2;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central persistence handler for the Telehealth System.
 * Stores and retrieves serialized lists of model objects (users, appointments, etc.)
 * Each list is stored as a file under the "data" directory.
 */
public class DataStore {

    private final Path dataDir;

    public DataStore(String dirName) {
        this.dataDir = Paths.get(dirName);
        try {
            if (!Files.exists(dataDir)) Files.createDirectories(dataDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------- Generic helpers ----------
    private <T> List<T> readList(String fileName, Class<T> cls) {
        Path path = dataDir.resolve(fileName);
        if (!Files.exists(path)) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            Object obj = in.readObject();
            if (obj instanceof List<?> list) {
                return list.stream()
                        .filter(cls::isInstance)
                        .map(cls::cast)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("Error reading " + fileName + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private <T> void writeList(String fileName, List<T> list) {
        Path path = dataDir.resolve(fileName);
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(list);
        } catch (Exception e) {
            System.err.println("Error writing " + fileName + ": " + e.getMessage());
        }
    }

    // ---------- USERS ----------
    public List<User> getUsers() {
        return readList("users.ser", User.class);
    }

    public void saveUsers(List<User> users) {
        writeList("users.ser", users);
    }

    // ---------- APPOINTMENTS ----------
    public List<Appointment> getAppointments() {
        return readList("appointments.ser", Appointment.class);
    }

    public void saveAppointments(List<Appointment> list) {
        writeList("appointments.ser", list);
    }

    // ---------- PRESCRIPTIONS ----------
    public List<Prescription> getPrescriptions() {
        return readList("prescriptions.ser", Prescription.class);
    }

    public void savePrescriptions(List<Prescription> list) {
        writeList("prescriptions.ser", list);
    }

    // ---------- VITAL SIGNS ----------
    public List<VitalSigns> getVitals() {
        return readList("vitals.ser", VitalSigns.class);
    }

    public void saveVitals(List<VitalSigns> list) {
        writeList("vitals.ser", list);
    }

    // ---------- DIAGNOSES ----------
    public List<Diagnosis> getDiagnoses() {
        return readList("diagnoses.ser", Diagnosis.class);
    }

    public void saveDiagnoses(List<Diagnosis> list) {
        writeList("diagnoses.ser", list);
    }

    // ---------- REFERRALS ----------
    public List<Referral> getReferrals() {
        return readList("referrals.ser", Referral.class);
    }

    public void saveReferrals(List<Referral> list) {
        writeList("referrals.ser", list);
    }

    // ---------- Utility lookup helpers ----------

    /** Finds a user's full name by ID (returns “Unknown User” if not found). */
    public String findUserName(String id) {
        return getUsers().stream()
                .filter(u -> u.getId().equals(id))
                .map(User::getName)
                .findFirst()
                .orElse("Unknown User");
    }

    /** Finds a patient’s name by ID (returns “Unknown Patient” if not found). */
    public String findPatientName(String patientId) {
        return getUsers().stream()
                .filter(u -> u instanceof Patient && u.getId().equals(patientId))
                .map(User::getName)
                .findFirst()
                .orElse("Unknown Patient");
    }

    /** Finds a doctor’s name by ID (returns “Unknown Doctor” if not found). */
    public String findDoctorName(String doctorId) {
        return getUsers().stream()
                .filter(u -> u instanceof Doctor && u.getId().equals(doctorId))
                .map(User::getName)
                .findFirst()
                .orElse("Unknown Doctor");
    }

    // ---------- Convenience: clear all ----------
    public void clearAll() {
        for (String f : List.of("users.ser", "appointments.ser", "prescriptions.ser",
                "vitals.ser", "diagnoses.ser", "referrals.ser")) {
            try { Files.deleteIfExists(dataDir.resolve(f)); } catch (IOException ignored) {}
        }
    }
}
