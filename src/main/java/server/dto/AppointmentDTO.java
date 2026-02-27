package server.dto;

import common.enums.types.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.Date;

public class AppointmentDTO {
    private int id;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private String symptoms;
    private String diagnosisName;
    private String diagnosisDescription;
    private String prescriptionInstructions;
    private String referenceDetails;
    private Date referenceValidFrom;
    private Date referenceValidUntil;
    private String doctorSurname;
    private String doctorSpecialization;
    private String patientSurname;
    private String patientName;
    private int medicalCard;

    public AppointmentDTO() {}

    public AppointmentDTO(int id, LocalDateTime appointmentDate, AppointmentStatus status, String symptoms,
                          String diagnosisName, String diagnosisDescription,
                          String prescriptionInstructions, String referenceDetails,
                          Date referenceValidFrom, Date referenceValidUntil, String doctorSurname, String doctorSpecialization) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.symptoms = symptoms;
        this.diagnosisName = diagnosisName;
        this.diagnosisDescription = diagnosisDescription;
        this.prescriptionInstructions = prescriptionInstructions;
        this.referenceDetails = referenceDetails;
        this.referenceValidFrom = referenceValidFrom;
        this.referenceValidUntil = referenceValidUntil;
        this.doctorSurname = doctorSurname;
        this.doctorSpecialization = doctorSpecialization;
    }

    public AppointmentDTO(int id, LocalDateTime appointmentDate, String symptoms,
                          String diagnosisName, String diagnosisDescription,
                          String prescriptionInstructions, String referenceDetails,
                          Date referenceValidFrom, Date referenceValidUntil, String patientSurname, String patientName, int medicalCard) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.symptoms = symptoms;
        this.diagnosisName = diagnosisName;
        this.diagnosisDescription = diagnosisDescription;
        this.prescriptionInstructions = prescriptionInstructions;
        this.referenceDetails = referenceDetails;
        this.referenceValidFrom = referenceValidFrom;
        this.referenceValidUntil = referenceValidUntil;
        this.patientSurname = patientSurname;
        this.patientName = patientName;
        this.medicalCard = medicalCard;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }

    public void setDiagnosisName(String diagnosisName) {
        this.diagnosisName = diagnosisName;
    }

    public String getDiagnosisDescription() {
        return diagnosisDescription;
    }

    public void setDiagnosisDescription(String diagnosisDescription) {
        this.diagnosisDescription = diagnosisDescription;
    }

    public String getPrescriptionInstructions() {
        return prescriptionInstructions;
    }

    public void setPrescriptionInstructions(String prescriptionInstructions) {
        this.prescriptionInstructions = prescriptionInstructions;
    }

    public String getReferenceDetails() {
        return referenceDetails;
    }

    public void setReferenceDetails(String referenceDetails) {
        this.referenceDetails = referenceDetails;
    }

    public Date getReferenceValidFrom() {
        return referenceValidFrom;
    }

    public void setReferenceValidFrom(Date referenceValidFrom) {
        this.referenceValidFrom = referenceValidFrom;
    }

    public Date getReferenceValidUntil() {
        return referenceValidUntil;
    }

    public void setReferenceValidUntil(Date referenceValidUntil) {
        this.referenceValidUntil = referenceValidUntil;
    }

    public String getDoctorSurname() {
        return doctorSurname;
    }

    public void setDoctorSurname(String doctorSurname) {
        this.doctorSurname = doctorSurname;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientSurname() {
        return patientSurname;
    }

    public void setPatientSurname(String patientSurname) {
        this.patientSurname = patientSurname;
    }

    public int getMedicalCard() {
        return medicalCard;
    }

    public void setMedicalCard(int medicalCard) {
        this.medicalCard = medicalCard;
    }

}