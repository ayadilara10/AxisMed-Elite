# AxisMed Elite
### International Sports Medicine & Performance Clinic — Management System

> A console-based Java OOP application managing patients, medical staff, appointments, diagnoses, and treatment plans across an international premium sports medicine clinic operating in 6 cities.

---

## Design Rationale

Most introductory OOP projects choose a deliberately simple domain — a library, a to-do list, a school catalog — because the goal is to practice syntax, not to stress-test a design. I chose the opposite approach: a domain complicated enough that the OOP principles had to be load-bearing, not decorative.

AxisMed Elite is a fictional clinic, invented entirely for this project. But the *constraints* it operates under are taken seriously: doctors split across distinct specialties, staff with different and non-overlapping permissions, patients who range from local chronic-condition cases to athletes flying in for a single pre-match clearance, and a privacy boundary where a team delegate can confirm an athlete is cleared to play without ever seeing why a teammate wasn't, last month.

None of this is meant to be impressive on its own. It's meant to make the architecture necessary rather than arbitrary — an abstract `Staff` class, polymorphic permission checks, and a record that travels with the patient across six cities aren't requirements I imposed on a simple problem. They're the structure the problem itself demands once it gets complicated enough to be interesting.

---

## Overview

AxisMed Elite is a fully functional clinic management system built in Java, demonstrating core Object-Oriented Programming principles including abstract classes, interfaces, inheritance, polymorphism, generics, custom exceptions, and enums.

The clinic operates across **Bucharest · London · Dubai · Istanbul · Madrid · Doha** and serves elite athletes, visiting sports teams, and patients with chronic conditions. All patient records are **global** — a patient who visits any clinic location has one unified medical record accessible from all locations.

---

## Specification Coverage

This project was built against a defined technical specification. Every requirement is fully implemented:

| Requirement | Implementation |
|---|---|
| 8+ object types | 18 model classes across 6 packages |
| 10+ actions | 17 operations exposed by ClinicService |
| Simple classes with private attributes | All model classes use private fields with getters/setters |
| Abstract classes and interfaces | Staff (abstract), Diagnosable, Schedulable, Recordable |
| Default interface behaviour | One default method per interface |
| 2+ List implementations | ArrayList and LinkedList — chosen for different performance characteristics |
| 2+ Map implementations | HashMap and TreeMap — chosen for different access patterns |
| Inheritance and polymorphism | Staff hierarchy, ClearanceTest extends Appointment, polymorphic permission system |
| Service class exposing operations | ClinicService |
| Main class | Main.java — composition root, assembles all dependencies |
| Custom exceptions | 6 checked exceptions used throughout the service layer |
| Enums | 13 enums eliminating invalid states at compile time |
| Generic singleton CSV services | CSVReaderService\<T\>, CSVWriterService\<T\> |
| CSV storage for 4+ object types | 6 CSV files for model objects |
| Data loaded at startup | All repositories initialize from CSV in their constructors |
| Audit service | AuditService → audit\_log.csv with action, timestamp, and user |

---

## Features

### Role-Based Access Control
Four user roles with separate menus and enforced permissions:

| Role | Capabilities |
|---|---|
| `ADMIN` | Full system access — manages staff, patients, clinics, sporting events |
| `DOCTOR` | Records diagnoses, creates treatment plans, prescribes medication, runs functional and clearance tests |
| `PATIENT` | Views own appointments, medical record, diagnoses, treatment plans, prescriptions, test results |
| `TEAM_DELEGATE` | Registers athletes, links them to sporting events, views clearance status only |

### Clinical Operations
- Patient registration with type classification (LOCAL, VISITING\_INDIVIDUAL, VISITING\_TEAM, CHRONIC)
- Appointment scheduling with conflict detection
- Diagnosis recording (Doctor only — enforced via polymorphism)
- Treatment plan creation with structured rehabilitation protocols and free-text recommendations
- Functional test recording (VO2 max, FMS, ECG, blood panels, strength assessments)
- Medical and doping clearance tests with official CLEARED / NOT\_CLEARED results
- Medication and supplementation prescribing
- Global medical record — complete patient history across all clinic locations
- Performance report generation per patient
- Sporting event management — team delegations, athlete registration, event linking

### System Features
- Persistent CSV storage — all data survives application restarts
- Audit log — every action recorded with action name, timestamp, and user email
- Role-based data privacy — team delegates see clearance status only, never medical details
- Travelling doctors — staff can be assigned to multiple clinic locations

---

## Application Preview

What the system looks like when running:

```
=====================================================
       AXISMED ELITE — Sports Medicine System
=====================================================

--- LOGIN ---
Email   : doctor@axismed.com
Password: doctor123

Welcome! Logged in as DOCTOR

=== AXISMED ELITE | DOCTOR ===
1.  View my patients
2.  View my upcoming appointments
3.  Record diagnosis
4.  Create treatment plan
5.  Add rehab protocol to plan
6.  Prescribe medication
7.  Record functional test
8.  Record clearance test
9.  View patient full history
10. Track treatment plan progress
0.  Logout
Select: 3

-- Record Diagnosis --
Appointment ID : APT001
Diagnosis name : ACL Grade II Sprain
Description    : Partial tear of anterior cruciate ligament
ICD code (blank if none): S83.5
Diagnosis recorded. ID: DGN-9E1AE979C079
```

```
=== AXISMED ELITE | ADMIN ===
Select: 10
Patient ID: PAT001

====================================================
   AXISMED ELITE — PERFORMANCE REPORT
====================================================
Patient : Marcus Johnson
ID      : PAT001
Age     : 36
Sport   : FOOTBALL
Type    : LOCAL
Member  : PREMIUM
Chronic : Mild hypertension

--- Diagnoses (2) ---
  [2026-06-10] Medial Meniscus Tear (M23.2)
  [2026-06-10] Mild Hypertension (I10)

--- Treatment Plans (2) ---
  [PLN001] Status: ACTIVE | Start: 2026-06-11
    Recommendations: Avoid high-impact activities; ice therapy 2x daily

--- Functional Tests (1) ---
  [2026-06-12] NUMERIC: 54.2 ml/kg/min

--- Medications (1) ---
  Ibuprofen — 400mg, 3 times daily [pharmaceutical]

--- Appointments (4) ---
  [2026-06-20] INJURY_ASSESSMENT — SCHEDULED
  [2026-06-10] REHABILITATION — COMPLETED
  [2026-06-12] MEDICAL_CLEARANCE — COMPLETED
  [2026-06-12] FOLLOW_UP — SCHEDULED
====================================================
```

```
-- Audit Log --
  Action                         Timestamp              User
  -----------------------------------------------------------------------
  LOGIN                          2026-06-12T18:46:01    patient@axismed.com
  RECORD_DIAGNOSIS               2026-06-12T19:07:03    doctor@axismed.com
  CREATE_TREATMENT_PLAN          2026-06-12T19:08:29    doctor@axismed.com
  PRESCRIBE_MEDICATION           2026-06-12T19:10:54    doctor@axismed.com
  RECORD_FUNCTIONAL_TEST         2026-06-12T19:11:43    doctor@axismed.com
  RECORD_CLEARANCE_TEST          2026-06-12T19:12:39    doctor@axismed.com
  REGISTER_SPORTING_EVENT        2026-06-12T19:22:09    admin@axismed.com
  GENERATE_PERFORMANCE_REPORT    2026-06-12T19:22:33    admin@axismed.com
```

---

## Getting Started

### Requirements
- Java 17 or higher
- No external dependencies

### Compile
```bash
javac -d out -sourcepath src src/axismed/Main.java
```

### Run
```bash
java -cp out axismed.Main
```

### Test Accounts
Pre-loaded at startup from `data/users.csv`:

| Email | Password | Role |
|---|---|---|
| admin@axismed.com | admin123 | ADMIN |
| doctor@axismed.com | doctor123 | DOCTOR |
| patient@axismed.com | patient123 | PATIENT |
| delegate@axismed.com | delegate123 | TEAM\_DELEGATE |

---

## Project Structure

```
axismed-elite/
├── src/
│   └── axismed/
│       ├── Main.java
│       ├── enums/
│       │   ├── AppointmentType.java
│       │   ├── AppointmentStatus.java
│       │   ├── MedicalSpecialization.java
│       │   ├── StaffRole.java
│       │   ├── UserRole.java
│       │   ├── PatientType.java
│       │   ├── MembershipType.java
│       │   ├── TreatmentStatus.java
│       │   ├── Gender.java
│       │   ├── Sport.java
│       │   ├── ResultType.java
│       │   ├── ClearanceResult.java
│       │   └── ClearanceType.java
│       ├── exception/
│       │   ├── PatientNotFoundException.java
│       │   ├── StaffNotFoundException.java
│       │   ├── SchedulingConflictException.java
│       │   ├── UnauthorizedActionException.java
│       │   ├── DuplicateRecordException.java
│       │   └── InvalidClearanceException.java
│       ├── model/
│       │   ├── staff/
│       │   │   ├── Staff.java              (abstract)
│       │   │   ├── Doctor.java
│       │   │   ├── Physiotherapist.java
│       │   │   ├── Nutritionist.java
│       │   │   └── Trainer.java
│       │   ├── patient/
│       │   │   ├── Patient.java
│       │   │   └── TeamDelegate.java
│       │   ├── appointment/
│       │   │   ├── Appointment.java
│       │   │   └── ClearanceTest.java
│       │   ├── medical/
│       │   │   ├── MedicalRecord.java
│       │   │   ├── Diagnosis.java
│       │   │   ├── TreatmentPlan.java
│       │   │   ├── RehabilitationProtocol.java
│       │   │   ├── Exercise.java
│       │   │   ├── Medication.java
│       │   │   └── FunctionalTest.java
│       │   ├── event/
│       │   │   └── SportingEvent.java
│       │   └── clinic/
│       │       └── Clinic.java
│       ├── service/
│       │   ├── ClinicService.java
│       │   ├── AuthService.java
│       │   └── AuditService.java
│       ├── repository/
│       │   ├── CSVReaderService.java
│       │   ├── CSVWriterService.java
│       │   ├── PatientRepository.java
│       │   ├── StaffRepository.java
│       │   ├── AppointmentRepository.java
│       │   ├── DiagnosisRepository.java
│       │   ├── TreatmentPlanRepository.java
│       │   └── MedicalRecordRepository.java
│       └── util/
│           ├── ConsoleMenu.java
│           └── DateUtils.java
├── data/
│   ├── patients.csv
│   ├── staff.csv
│   ├── appointments.csv
│   ├── diagnoses.csv
│   ├── treatment_plans.csv
│   ├── medical_records.csv
│   ├── users.csv
│   └── audit_log.csv
└── README.md
```

---

## OOP Concepts Demonstrated

### Abstract Class
`Staff` is abstract — it cannot be instantiated directly. It declares three abstract methods that every subclass must implement:

```java
public abstract String getTitle();
public abstract boolean canDiagnose();
public abstract boolean canWriteTreatmentPlan();
```

It also provides concrete shared methods inherited by all four subclasses. `getFullName()` calls `getTitle()` polymorphically — the correct subclass version is dispatched automatically at runtime:

```java
public String getFullName() {
    return getTitle() + " " + firstName + " " + lastName;
    // → "Dr. Elena Vasquez" / "Phys. Sophie Laurent" / "Nutr. Kai Tanaka"
}
```

### Interfaces with Default Methods
Three interfaces, each with at least one default method:

```java
public interface Diagnosable {
    void addDiagnosis(Diagnosis diagnosis);
    List<Diagnosis> getDiagnoses();

    default String getDiagnosisSummary() {
        return "Total diagnoses: " + getDiagnoses().size();
    }
}

public interface Schedulable {
    void scheduleAppointment(Appointment appointment);
    void cancelAppointment(String appointmentId);
    List<Appointment> getUpcomingAppointments();

    default boolean hasAppointmentOn(LocalDate date) {
        return getUpcomingAppointments().stream()
            .anyMatch(a -> a.getDate().equals(date));
    }
}

public interface Recordable {
    void updateRecord(String note);
    MedicalRecord getRecord();

    default String getRecordSummary() {
        return "Record ID: " + getRecord().getRecordId();
    }
}
```

`Patient` implements both `Diagnosable` and `Recordable`. `Staff` implements `Schedulable` — all four subclasses inherit the implementation without duplication.

### Inheritance
Two inheritance chains:

```
Staff (abstract)
├── Doctor
├── Physiotherapist
├── Nutritionist
└── Trainer

Appointment
└── ClearanceTest
```

Each subclass calls `super()` in its constructor and adds only what is specific to that type. `ClearanceTest` auto-derives its `AppointmentType` from `ClearanceType` in the constructor, keeping both enums in sync without requiring the caller to pass both.

### Polymorphism
Permission checking uses polymorphic dispatch instead of `instanceof` chains:

```java
// Instead of: if (staff instanceof Doctor) { allow } else { deny }
if (!staff.canDiagnose()) {
    throw new UnauthorizedActionException(currentRole, "record diagnosis");
}
```

Adding a new staff type in the future requires no changes to `ClinicService` — the new class implements `canDiagnose()` returning the correct value, and the permission system works automatically.

`StaffRepository` uses a polymorphic factory — returns different subclasses under the common `Staff` type based on the role stored in CSV:

```java
switch (role) {
    case DOCTOR:          return new Doctor(...);
    case PHYSIOTHERAPIST: return new Physiotherapist(...);
    case NUTRITIONIST:    return new Nutritionist(...);
    case TRAINER:         return new Trainer(...);
}
```

### Collections
Four collection types chosen for specific performance characteristics:

| Collection | Where used | Reason |
|---|---|---|
| `ArrayList` | Patient appointments, Clinic staff, Rehab exercises | Fast index access, additions at end |
| `LinkedList` | Doctor's patients, Patient's diagnoses | Frequent insertions at head/tail |
| `HashMap` | PatientRepository, AppointmentRepository, Clinic appointments | O(1) lookup by ID |
| `TreeMap` | MedicalRecord appointment history | Automatic chronological ordering by date |

### Custom Exceptions
Six checked exceptions used throughout `ClinicService`:

```java
PatientNotFoundException       // patient ID not found in system
StaffNotFoundException         // staff ID not found in system
SchedulingConflictException    // appointment time already taken
UnauthorizedActionException    // role does not have permission
DuplicateRecordException       // email already registered
InvalidClearanceException      // CLEARED result without valid-until date
```

### Enums
13 enums eliminate invalid states across the system. Every field with a fixed set of valid values uses an enum instead of a String, making invalid values impossible at compile time.

### Generic Singleton Services
`CSVReaderService<T>` and `CSVWriterService<T>` are generic singletons handling all file I/O:

```java
public class CSVReaderService<T> {
    private static CSVReaderService instance;

    private CSVReaderService() { }

    public static CSVReaderService getInstance() {
        if (instance == null) instance = new CSVReaderService();
        return instance;
    }

    public List<String[]> readAll(String filePath) throws IOException { ... }
}
```

The writer implements RFC 4180 CSV escaping — fields containing commas or quotes are automatically wrapped in quotes to prevent misreading on load.

### Three-Layer Architecture
The system follows a clean three-layer architecture:

```
ConsoleMenu (UI layer)
      ↓
ClinicService (service layer — all business logic)
      ↓
Repositories (data layer — CSV persistence)
```

`ClinicService` is the only class `ConsoleMenu` interacts with for business operations. Repositories are injected into `ClinicService` via constructor (dependency injection). This means the UI layer never touches the data layer directly.

### Audit Service
Every action in `ClinicService` is logged before returning:

```
LOGIN,2026-06-12T18:46:01,patient@axismed.com
RECORD_DIAGNOSIS,2026-06-12T19:07:03,doctor@axismed.com
SCHEDULE_APPOINTMENT,2026-06-12T19:20:49,admin@axismed.com
GENERATE_PERFORMANCE_REPORT,2026-06-12T19:22:33,admin@axismed.com
```

---

## Data Storage

All data is loaded from CSV files at startup and persisted after each operation.

**Model data:**

| File | Object type |
|---|---|
| `patients.csv` | Patient |
| `staff.csv` | Staff and all subtypes |
| `appointments.csv` | Appointment, ClearanceTest |
| `diagnoses.csv` | Diagnosis |
| `treatment_plans.csv` | TreatmentPlan |
| `medical_records.csv` | MedicalRecord |

**System data:**

| File | Content |
|---|---|
| `users.csv` | Login credentials and user roles |
| `audit_log.csv` | Complete action history — append only |

Multi-value fields (chronic conditions, assigned clinics) are stored as semicolon-separated values within a single CSV column.

---

## Design Decisions

A few choices made during development that are worth explaining:

**TreeMap for medical record history instead of ArrayList**
Appointment history in `MedicalRecord` uses `TreeMap<LocalDateTime, Appointment>` instead of a list. This means appointments are always stored and retrieved in chronological order automatically — no manual sorting needed every time the history is displayed. The date becomes the key, and TreeMap's natural ordering does the rest.

**LinkedList for doctor's patients and patient's diagnoses instead of ArrayList**
These two lists are almost never accessed by index — they're iterated sequentially or appended to. LinkedList is the correct choice when insertions at either end are the primary operation and random access by position is not needed.

**Abstract class for Staff instead of just an interface**
Staff needed to both define a contract (abstract methods `canDiagnose()`, `getTitle()`) AND provide shared concrete implementation (scheduling logic, `getFullName()`, clinic assignment). An interface alone can't hold instance fields or a constructor. An abstract class provides both — the contract and the shared code — in one place.

**Polymorphic permission checking instead of instanceof**
`ClinicService` never checks `if (staff instanceof Doctor)` to decide what a staff member can do. Instead it calls `staff.canDiagnose()` — a method each subclass implements returning its own correct answer. This means adding a new staff type in the future requires no changes to the service layer at all.

**Double permission check for sensitive operations**
Operations like recording a diagnosis check both the `UserRole` (is this user logged in as DOCTOR?) and `staff.canDiagnose()` (is the actual Staff object a type that can diagnose?). Two independent layers of enforcement — one at the session level, one at the object level.

**TeamDelegate privacy restriction at the service layer**
The `TeamDelegate` class itself holds no restriction logic. The restriction — delegates see clearance status only, never medical details — is enforced entirely in `ClinicService.viewFullPatientHistory()`, which blocks TEAM\_DELEGATE role from accessing full records. Business rules belong in the service layer, not in model classes.

---

## Planned Improvements

Given more time, the three most impactful improvements would be:

**Migrate from CSV to a relational database (SQLite or PostgreSQL)**
CSV works but has real limitations — relationships between objects (patient → appointments → diagnoses) require manual wiring in `ClinicService.linkLoadedData()` on every startup. A database handles these relationships natively with foreign keys and JOIN queries. The repository layer is already structured to make this swap straightforward — only the repository implementations would need to change, not the service layer or model classes.

**Add a unit test suite with JUnit**
Every method in `ClinicService` should have automated tests covering both the happy path and the exception paths. Currently testing is done manually through the console. Automated tests would catch regressions immediately when fixing bugs, and would have caught several of the issues found during manual testing.

**Expose the service layer as a REST API**
`ClinicService` already has clean, well-defined methods that map naturally to HTTP endpoints. Wrapping it in Spring Boot would turn this console application into a real backend API that any frontend — web, mobile — could connect to. The three-layer architecture (UI → Service → Repository) was designed with exactly this kind of migration in mind.

---

## Author

**Aya-Dilara**
Informatics — Year 2
Advanced Programming Methods | Semester 2
