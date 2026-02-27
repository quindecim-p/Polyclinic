package server.services;

import common.entities.Patient;
import common.enums.errors.ServerError;
import server.dao.PatientDAO;
import server.interfaces.Service;

import java.util.List;

public class PatientService implements Service<Patient> {

    private final PatientDAO patientDAO = new PatientDAO();
    private final ValidationService validationService = new ValidationService();

    @Override
    public Patient findEntity(int id) {
        return patientDAO.findById(id);
    }

    @Override
    public void saveEntity(Patient entity) {
        validationService.validatePhoneAndEmail(
                entity.getPersonData().getPhone(),
                entity.getPersonData().getEmail(),
                0
        );

        patientDAO.save(entity);
    }

    @Override
    public void deleteEntity(int id) {
        Patient existingPatient = findEntity(id);
        if (existingPatient == null) {
            throw new IllegalArgumentException(ServerError.USER_NOT_FOUND.getMessage());
        }
        patientDAO.delete(existingPatient);
    }

    @Override
    public void updateEntity(Patient entity) {
        Patient existingPatient = findEntity(entity.getId());

        if (existingPatient == null) {
            throw new IllegalArgumentException(ServerError.USER_NOT_FOUND.getMessage());
        }

        validationService.validatePhoneAndEmail(
                entity.getPersonData().getPhone(),
                entity.getPersonData().getEmail(),
                entity.getId()
        );

        patientDAO.update(entity);
    }

    @Override
    public List<Patient> findAllEntities() {
        return patientDAO.findAll();
    }

    public Patient findByUserId(int userId) {
        return patientDAO.findByUserId(userId);
    }

    public Patient findByPhone(String phone) {
        return patientDAO.findByPhone(phone);
    }

    public Patient findByMedicalCardId(int medicalCardId) {
        return patientDAO.findByMedicalCardId(medicalCardId);
    }

    public List<Patient> findByDoctorId(int doctorId) {
        return patientDAO.findByDoctorId(doctorId);
    }

    public void deleteById(int patientId) {
        patientDAO.deleteById(patientId);
    }

}
