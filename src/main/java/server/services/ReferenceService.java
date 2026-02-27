package server.services;

import common.entities.Reference;
import server.dao.ReferenceDAO;
import server.interfaces.Service;

import java.util.List;

public class ReferenceService implements Service<Reference> {

    private final ReferenceDAO referenceDAO = new ReferenceDAO();

    @Override
    public Reference findEntity(int id) {
        return null;
    }

    @Override
    public void saveEntity(Reference entity) {
        referenceDAO.save(entity);
    }

    @Override
    public void deleteEntity(int id) {

    }

    @Override
    public void updateEntity(Reference entity) {

    }

    @Override
    public List<Reference> findAllEntities() {
        return List.of();
    }

    public List<Reference> findByUserId(int userId) {
        return referenceDAO.findByUserId(userId);
    }

    public Reference findByAppointmentId(int appointmentId) {
        return referenceDAO.findByAppointmentId(appointmentId);
    }
}
