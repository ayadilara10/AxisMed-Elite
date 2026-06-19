package axismed.model;

import axismed.model.appointment.Appointment;
import java.time.LocalDate;
import java.util.List;

public interface Schedulable {
    void scheduleAppointment(Appointment appointment);
    void cancelAppointment(String appointmentId);
    List<Appointment> getUpcomingAppointments();
    default boolean hasAppointmentOn(LocalDate date) {
        return getUpcomingAppointments().stream()
            .anyMatch(a -> a.getDate().equals(date));
    }
}
