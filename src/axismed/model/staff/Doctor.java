package axismed.model.staff;

import axismed.enums.MedicalSpecialization;
import axismed.enums.StaffRole;
import axismed.model.clinic.Clinic;
import axismed.model.patient.Patient;
import java.util.LinkedList;
import java.util.List;

public class Doctor extends Staff {

    private MedicalSpecialization specialization;
    private List<Patient> patients;
    private String licenseNumber;

    public Doctor(String staffId, String firstName, String lastName, String email,
                  String passwordHash, Clinic homeClinic,
                  MedicalSpecialization specialization, String licenseNumber) {
        super(staffId, firstName, lastName, email, passwordHash, StaffRole.DOCTOR, homeClinic);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.patients = new LinkedList<>();
    }

    // --- Abstract method implementations ---

    @Override
    public String getTitle() { return "Dr."; }

    @Override
    public boolean canDiagnose() { return true; }

    @Override
    public boolean canWriteTreatmentPlan() { return true; }

    // --- Doctor-specific methods ---

    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    public void removePatient(String patientId) {
        patients.removeIf(p -> p.getPatientId().equals(patientId));
    }

    // --- Getters and setters ---

    public MedicalSpecialization getSpecialization() { return specialization; }
    public void setSpecialization(MedicalSpecialization specialization) { this.specialization = specialization; }

    public List<Patient> getPatients() { return patients; }
    public void setPatients(List<Patient> patients) { this.patients = patients; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    @Override
    public String toString() {
        return "Doctor{id='" + getStaffId() + "', name='" + getFullName() +
               "', specialization=" + specialization + ", license='" + licenseNumber +
               "', patients=" + patients.size() + ", active=" + isActive() + "}";
    }
}
