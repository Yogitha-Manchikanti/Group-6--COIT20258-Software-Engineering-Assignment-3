package com.mycompany.coit20258assignment2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/** Booking + doctor/patient queries + reschedule/cancel. */
public class AppointmentService {

    private final DataStore store;

    public AppointmentService(DataStore store) {
        this.store = store;
    }

    public Appointment book(String patientId, String doctorId, LocalDate date, LocalTime time) {
        // Check if doctor is available
        if (!DoctorUnavailabilityService.isDoctorAvailable(doctorId, date, time)) {
            String reason = DoctorUnavailabilityService.getUnavailabilityReason(doctorId, date, time);
            throw new IllegalStateException(
                "Doctor is unavailable at this time. Reason: " + reason
            );
        }
        
        String id = "AP" + UUID.randomUUID().toString().substring(0, 8);
        Appointment a = new Appointment(id, patientId, doctorId, date, time, AppointmentStatus.BOOKED);
        var list = store.getAppointments();
        list.add(a);
        store.saveAppointments(list);
        return a;
    }

    /** Reschedule by ID. */
    public void reschedule(String appointmentId, LocalDate newDate, LocalTime newTime) {
        var list = store.getAppointments();
        var appt = list.stream().filter(a -> a.getId().equals(appointmentId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
        
        // Check if doctor is available at new date/time
        if (!DoctorUnavailabilityService.isDoctorAvailable(appt.getDoctorId(), newDate, newTime)) {
            String reason = DoctorUnavailabilityService.getUnavailabilityReason(appt.getDoctorId(), newDate, newTime);
            throw new IllegalStateException(
                "Doctor is unavailable at this time. Reason: " + reason
            );
        }
        
        appt.setDate(newDate);
        appt.setTime(newTime);
        appt.setStatus(AppointmentStatus.RESCHEDULED);
        store.saveAppointments(list);
    }

    /** Cancel by ID. */
    public void cancel(String appointmentId) {
        var list = store.getAppointments();
        var appt = list.stream().filter(a -> a.getId().equals(appointmentId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
        appt.setStatus(AppointmentStatus.CANCELLED);
        store.saveAppointments(list);
    }

    public List<Appointment> getByPatient(String patientId) {
        return store.getAppointments().stream()
                .filter(a -> a.getPatientId().equals(patientId))
                .toList();
    }

    public List<Appointment> getByDoctor(String doctorId) {
        return store.getAppointments().stream()
                .filter(a -> a.getDoctorId().equals(doctorId))
                .toList();
    }
}
