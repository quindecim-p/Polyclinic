package server.services;

import common.entities.Prescription;
import server.dao.PrescriptionDAO;
import server.interfaces.Service;

import java.util.List;

public class PrescriptionService implements Service<Prescription> {

    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    @Override
    public Prescription findEntity(int id) {
        return null;
    }

    @Override
    public void saveEntity(Prescription entity) {
        prescriptionDAO.save(entity);
    }

    @Override
    public void deleteEntity(int id) {

    }

    @Override
    public void updateEntity(Prescription entity) {

    }

    @Override
    public List<Prescription> findAllEntities() {
        return List.of();
    }

    public List<Prescription> findByUserId(int userId) {
        return prescriptionDAO.findByUserId(userId);
    }

    public Prescription findByAppointmentId(int appointmentId) {
        return prescriptionDAO.findByAppointmentId(appointmentId);
    }

}
