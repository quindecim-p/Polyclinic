package server.dto;

import common.entities.*;
import common.enums.types.SpecializationType;
import server.services.*;

import java.util.List;

public class AppointmentDTOMapper {

    private final DiagnosisService diagnosisService;
    private final PrescriptionService prescriptionService;
    private final ReferenceService referenceService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public AppointmentDTOMapper(DiagnosisService diagnosisService, PrescriptionService prescriptionService,
                                ReferenceService referenceService, DoctorService doctorService, PatientService patientService) {
        this.diagnosisService = diagnosisService;
        this.prescriptionService = prescriptionService;
        this.referenceService = referenceService;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }



    public AppointmentDTO mapToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setSymptoms(appointment.getSymptoms());
        dto.setStatus(appointment.getStatus());

        Diagnosis diagnosis = diagnosisService.findByAppointmentId(appointment.getId());
        if (diagnosis != null) {
            dto.setDiagnosisName(diagnosis.getName());
            dto.setDiagnosisDescription(diagnosis.getDescription());
        }

        Prescription prescription = prescriptionService.findByAppointmentId(appointment.getId());
        if (prescription != null) {
            dto.setPrescriptionInstructions(prescription.getInstructions());
        }

        Reference reference = referenceService.findByAppointmentId(appointment.getId());
        if (reference != null) {
            dto.setReferenceDetails(reference.getDetails());
            dto.setReferenceValidFrom(reference.getValidFrom());
            dto.setReferenceValidUntil(reference.getValidUntil());
        }

        Doctor doctor = doctorService.findEntity(appointment.getDoctor().getId());
        if (doctor != null) {
            dto.setDoctorSurname(doctor.getPersonData().getSurname());
            SpecializationType specializationType = SpecializationType.valueOf(doctor.getSpecialization().toUpperCase());
            dto.setDoctorSpecialization(specializationType.getDescription());
        }

        MedicalCard medicalCard = appointment.getMedicalCard();
        if (medicalCard != null) {
            Patient patient = patientService.findByMedicalCardId(medicalCard.getId());
            if (patient != null) {
                dto.setPatientSurname(patient.getPersonData().getSurname());
                dto.setPatientName(patient.getPersonData().getName());
                dto.setMedicalCard(medicalCard.getId());
            }
        }

        return dto;
    }

    public List<AppointmentDTO> mapToDTOs(List<Appointment> appointments) {
        return appointments.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Appointment mapToAppointment(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setId(dto.getId());
        appointment.setSymptoms(dto.getSymptoms());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStatus(dto.getStatus());

        MedicalCard medicalCard = new MedicalCard(dto.getMedicalCard());
        appointment.setMedicalCard(medicalCard);

        Doctor doctor = doctorService.findByAppointmentId(appointment.getId());
        appointment.setDoctor(doctor);

        return appointment;
    }

    public Diagnosis mapToDiagnosis(Appointment appointment, AppointmentDTO dto) {
        return new Diagnosis(dto.getDiagnosisName(), dto.getDiagnosisDescription(), appointment);
    }

    public Prescription mapToPrescription(Appointment appointment, AppointmentDTO dto) {
        return new Prescription(dto.getPrescriptionInstructions(), appointment);
    }

    public Reference mapToReference(Appointment appointment, AppointmentDTO dto) {
        return new Reference(dto.getReferenceDetails(), dto.getReferenceValidFrom(), dto.getReferenceValidUntil(), appointment);
    }

}