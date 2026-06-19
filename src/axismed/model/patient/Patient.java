package axismed.model.patient;

import axismed.enums.Gender;
import axismed.enums.MembershipType;
import axismed.enums.PatientType;
import axismed.enums.Sport;
import axismed.model.Diagnosable;
import axismed.model.Recordable;
import axismed.model.appointment.Appointment;
import axismed.model.medical.Diagnosis;
import axismed.model.medical.FunctionalTest;
import axismed.model.medical.MedicalRecord;
import axismed.model.medical.Medication;
import axismed.model.medical.TreatmentPlan;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Patient implements Diagnosable, Recordable {

    private String patientId;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phone;
    private String nationality;
    private PatientType patientType;
    private MembershipType membershipType;
    private List<String> chronicConditions;
    private MedicalRecord medicalRecord;
    private List<Appointment> appointments;
    private List<Diagnosis> diagnoses;
    private List<TreatmentPlan> treatmentPlans;
    private List<FunctionalTest> functionalTests;
    private List<Medication> medications;
    private Sport primarySport;
    private boolean isActive;

    public Patient(String patientId, String firstName, String lastName, String email,
                   String passwordHash, LocalDate dateOfBirth, Gender gender, String phone,
                   String nationality, PatientType patientType, MembershipType membershipType,
                   Sport primarySport) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phone = phone;
        this.nationality = nationality;
        this.patientType = patientType;
        this.membershipType = membershipType;
        this.primarySport = primarySport;
        this.chronicConditions = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.diagnoses = new LinkedList<>();
        this.treatmentPlans = new ArrayList<>();
        this.functionalTests = new ArrayList<>();
        this.medications = new ArrayList<>();
        this.isActive = true;
    }

    // --- Diagnosable implementation ---

    @Override
    public void addDiagnosis(Diagnosis diagnosis) {
        diagnoses.add(diagnosis);
    }

    @Override
    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    // --- Recordable implementation ---

    @Override
    public void updateRecord(String note) {
        medicalRecord.addNote(note);
    }

    @Override
    public MedicalRecord getRecord() {
        return medicalRecord;
    }

    // --- Patient-specific methods ---

    public void addChronicCondition(String condition) {
        chronicConditions.add(condition);
    }

    public void addTreatmentPlan(TreatmentPlan plan) {
        treatmentPlans.add(plan);
    }

    public void addFunctionalTest(FunctionalTest test) {
        functionalTests.add(test);
    }

    public void addMedication(Medication medication) {
        medications.add(medication);
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // --- Getters and setters ---

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public PatientType getPatientType() { return patientType; }
    public void setPatientType(PatientType patientType) { this.patientType = patientType; }

    public MembershipType getMembershipType() { return membershipType; }
    public void setMembershipType(MembershipType membershipType) { this.membershipType = membershipType; }

    public List<String> getChronicConditions() { return chronicConditions; }
    public void setChronicConditions(List<String> chronicConditions) { this.chronicConditions = chronicConditions; }

    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecord medicalRecord) { this.medicalRecord = medicalRecord; }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }

    public List<TreatmentPlan> getTreatmentPlans() { return treatmentPlans; }
    public void setTreatmentPlans(List<TreatmentPlan> treatmentPlans) { this.treatmentPlans = treatmentPlans; }

    public List<FunctionalTest> getFunctionalTests() { return functionalTests; }
    public void setFunctionalTests(List<FunctionalTest> functionalTests) { this.functionalTests = functionalTests; }

    public List<Medication> getMedications() { return medications; }
    public void setMedications(List<Medication> medications) { this.medications = medications; }

    public Sport getPrimarySport() { return primarySport; }
    public void setPrimarySport(Sport primarySport) { this.primarySport = primarySport; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    @Override
    public String toString() {
        return "Patient{id='" + patientId + "', name='" + getFullName() + "', age=" + getAge() +
               ", type=" + patientType + ", membership=" + membershipType +
               ", sport=" + primarySport + ", active=" + isActive + "}";
    }
}
