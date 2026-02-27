package server.services;

import common.entities.Doctor;
import common.enums.errors.ServerError;
import server.dao.DoctorDAO;
import server.interfaces.Service;

import java.util.List;

public class DoctorService implements Service<Doctor> {

    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final ValidationService validationService = new ValidationService();

    @Override
    public Doctor findEntity(int id) {
        return doctorDAO.findById(id);
    }

    @Override
    public void saveEntity(Doctor entity) {
        validationService.validatePhoneAndEmail(
                entity.getPersonData().getPhone(),
                entity.getPersonData().getEmail(),
                0
        );

        doctorDAO.save(entity);
    }

    @Override
    public void deleteEntity(int id) {
        Doctor existingDoctor = findEntity(id);
        if (existingDoctor == null) {
            throw new IllegalArgumentException(ServerError.USER_NOT_FOUND.getMessage());
        }
        doctorDAO.delete(existingDoctor);
    }

    @Override
    public void updateEntity(Doctor entity) {
        Doctor existingDoctor = findEntity(entity.getId());

        if (existingDoctor == null) {
            throw new IllegalArgumentException(ServerError.USER_NOT_FOUND.getMessage());
        }

        validationService.validatePhoneAndEmail(
                entity.getPersonData().getPhone(),
                entity.getPersonData().getEmail(),
                entity.getId()
        );

        doctorDAO.update(entity);
    }

    @Override
    public List<Doctor> findAllEntities() {
        return doctorDAO.findAll();
    }

    public Doctor findByUserId(int userId) {
        return doctorDAO.findByUserId(userId);
    }

    public Doctor findByPhone(String phone) {
        return doctorDAO.findByPhone(phone);
    }

    public Doctor findByAppointmentId(int appointmentId) {
        return doctorDAO.findByAppointmentId(appointmentId);
    }

    public void deleteById(int doctorId) {
        doctorDAO.deleteById(doctorId);
    }

}