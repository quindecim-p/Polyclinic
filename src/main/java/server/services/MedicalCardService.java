package server.services;

import common.entities.MedicalCard;
import server.dao.MedicalCardDAO;
import server.interfaces.Service;

import java.util.List;

public class MedicalCardService implements Service<MedicalCard> {

    private final MedicalCardDAO medicalCardDAO = new MedicalCardDAO();

    @Override
    public MedicalCard findEntity(int id) {
        return medicalCardDAO.findById(id);
    }

    @Override
    public void saveEntity(MedicalCard entity) {
        medicalCardDAO.save(entity);
    }

    @Override
    public void deleteEntity(int id) {
        MedicalCard entity = medicalCardDAO.findById(id);
        if (entity != null) {
            medicalCardDAO.delete(entity);
        }
    }

    @Override
    public void updateEntity(MedicalCard entity) {
        medicalCardDAO.update(entity);
    }

    @Override
    public List<MedicalCard> findAllEntities() {
        return medicalCardDAO.findAll();
    }

    public MedicalCard findByUserId(int userId) {
        return medicalCardDAO.findByUserId(userId);
    }

}
