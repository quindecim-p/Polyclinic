package server.services;

import common.entities.Diagnosis;
import server.dao.DiagnosisDAO;
import server.interfaces.Service;

import java.util.List;

public class DiagnosisService implements Service<Diagnosis> {

    private final DiagnosisDAO diagnosisDAO = new DiagnosisDAO();

    @Override
    public Diagnosis findEntity(int id) {
        return null;
    }

    @Override
    public void saveEntity(Diagnosis entity) {
        diagnosisDAO.save(entity);
    }

    @Override
    public void deleteEntity(int id) {

    }

    @Override
    public void updateEntity(Diagnosis entity) {

    }

    @Override
    public List<Diagnosis> findAllEntities() {
        return List.of();
    }

    public Diagnosis findByAppointmentId(int appointmentId) {
        return diagnosisDAO.findByAppointmentId(appointmentId);
    }
}
