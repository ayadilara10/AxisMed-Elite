package axismed;

import axismed.model.clinic.Clinic;
import axismed.repository.AppointmentRepository;
import axismed.repository.DiagnosisRepository;
import axismed.repository.MedicalRecordRepository;
import axismed.repository.PatientRepository;
import axismed.repository.StaffRepository;
import axismed.repository.TreatmentPlanRepository;
import axismed.service.AuditService;
import axismed.service.AuthService;
import axismed.service.ClinicService;
import axismed.util.ConsoleMenu;

public class Main {

    public static void main(String[] args) {

        System.out.println("Starting AxisMed Elite...");

        // --- Step 1: Load all repositories (each reads its CSV file in its constructor) ---
        PatientRepository       patientRepository       = new PatientRepository();
        StaffRepository         staffRepository         = new StaffRepository();
        AppointmentRepository   appointmentRepository   = new AppointmentRepository(
                                                              patientRepository, staffRepository);
        DiagnosisRepository     diagnosisRepository     = new DiagnosisRepository();
        TreatmentPlanRepository treatmentPlanRepository = new TreatmentPlanRepository();
        MedicalRecordRepository medicalRecordRepository = new MedicalRecordRepository();

        System.out.println("  Repositories loaded.");

        // --- Step 2: Initialise singleton services ---
        AuditService auditService = AuditService.getInstance();
        AuthService  authService  = AuthService.getInstance();

        // --- Step 3: Wire repositories into AuthService ---
        authService.setRepositories(patientRepository, staffRepository);

        // --- Step 4: Build ClinicService (also links loaded data into object graph) ---
        ClinicService clinicService = new ClinicService(
            patientRepository,
            staffRepository,
            appointmentRepository,
            diagnosisRepository,
            treatmentPlanRepository,
            medicalRecordRepository
        );

        System.out.println("  Services initialised.");

        // --- Step 5: Register the 6 clinic locations ---
        clinicService.addClinic(new Clinic("CLN001", "Bucharest", "Calea Victoriei 1",   "+40 21 000 0001", "bucharest@axismed.com"));
        clinicService.addClinic(new Clinic("CLN002", "London",    "Harley Street 10",    "+44 20 000 0002", "london@axismed.com"));
        clinicService.addClinic(new Clinic("CLN003", "Dubai",     "Sheikh Zayed Road 5", "+971 4 000 0003", "dubai@axismed.com"));
        clinicService.addClinic(new Clinic("CLN004", "Istanbul",  "Bağcılar Caddesi 3",  "+90 212 000 0004","istanbul@axismed.com"));
        clinicService.addClinic(new Clinic("CLN005", "Madrid",    "Gran Vía 22",          "+34 91 000 0005", "madrid@axismed.com"));
        clinicService.addClinic(new Clinic("CLN006", "Doha",      "Al Corniche Street 7","+974 4 000 0006", "doha@axismed.com"));

        System.out.println("  6 clinic locations registered.");

        // --- Step 6: Launch the console menu ---
        System.out.println("  Ready.\n");
        ConsoleMenu menu = new ConsoleMenu(clinicService);
        menu.start();
    }
}
