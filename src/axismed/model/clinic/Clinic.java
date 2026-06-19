package axismed.model.clinic;

import axismed.model.appointment.Appointment;
import axismed.model.staff.Staff;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Clinic {

    private String clinicId;
    private String city;
    private String address;
    private String phone;
    private String email;
    private List<Staff> localStaff;
    private List<Staff> visitingStaff;
    private HashMap<String, Appointment> appointments;

    public Clinic(String clinicId, String city, String address, String phone, String email) {
        this.clinicId = clinicId;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.localStaff = new ArrayList<>();
        this.visitingStaff = new ArrayList<>();
        this.appointments = new HashMap<>();
    }

    public String getClinicId() { return clinicId; }
    public void setClinicId(String clinicId) { this.clinicId = clinicId; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Staff> getLocalStaff() { return localStaff; }
    public void setLocalStaff(List<Staff> localStaff) { this.localStaff = localStaff; }

    public List<Staff> getVisitingStaff() { return visitingStaff; }
    public void setVisitingStaff(List<Staff> visitingStaff) { this.visitingStaff = visitingStaff; }

    public List<Appointment> getAppointments() {
        return new ArrayList<>(appointments.values());
    }

    public void addLocalStaff(Staff staff) {
        localStaff.add(staff);
    }

    public void addVisitingStaff(Staff staff) {
        visitingStaff.add(staff);
    }

    public void removeVisitingStaff(String staffId) {
        visitingStaff.removeIf(s -> s.getStaffId().equals(staffId));
    }

    public void addAppointment(Appointment appointment) {
        appointments.put(appointment.getAppointmentId(), appointment);
    }

    public List<Staff> getAllStaff() {
        List<Staff> all = new ArrayList<>(localStaff);
        all.addAll(visitingStaff);
        return all;
    }

    @Override
    public String toString() {
        return "Clinic{id='" + clinicId + "', city='" + city + "', address='" + address +
               "', localStaff=" + localStaff.size() + ", visitingStaff=" + visitingStaff.size() +
               ", appointments=" + appointments.size() + "}";
    }
}
