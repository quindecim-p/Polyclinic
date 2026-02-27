package server.services;

import common.entities.WorkingSchedule;
import server.dao.WorkingScheduleDAO;
import server.interfaces.Service;

import java.util.List;

public class WorkingScheduleService implements Service<WorkingSchedule> {

    WorkingScheduleDAO workingScheduleDAO = new WorkingScheduleDAO();

    @Override
    public WorkingSchedule findEntity(int id) {
        return null;
    }

    @Override
    public void saveEntity(WorkingSchedule entity) {
        workingScheduleDAO.save(entity);
    }

    @Override
    public void deleteEntity(int id) {

    }

    @Override
    public void updateEntity(WorkingSchedule entity) {

    }

    @Override
    public List<WorkingSchedule> findAllEntities() {
        return List.of();
    }

    public WorkingSchedule findByDoctorId(int doctorId) {
        return workingScheduleDAO.findByDoctorId(doctorId);
    }
}
