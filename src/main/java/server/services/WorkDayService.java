package server.services;

import common.entities.WorkDay;
import server.dao.WorkDayDAO;
import server.interfaces.Service;

import java.time.DayOfWeek;
import java.util.List;

public class WorkDayService implements Service<WorkDay> {

    WorkDayDAO workDayDAO = new WorkDayDAO();

    @Override
    public WorkDay findEntity(int id) {
        return null;
    }

    @Override
    public void saveEntity(WorkDay entity) {
        workDayDAO.save(entity);
    }

    @Override
    public void deleteEntity(int id) {
        workDayDAO.deleteById(id);
    }

    @Override
    public void updateEntity(WorkDay entity) {
        workDayDAO.update(entity);
    }

    @Override
    public List<WorkDay> findAllEntities() {
        return List.of();
    }

    public List<WorkDay> findByScheduleId(int scheduleId) {
        return workDayDAO.findByScheduleId(scheduleId);
    }

    public WorkDay findByScheduleIdAndDay(int scheduleId, DayOfWeek day) {
        return workDayDAO.findByScheduleIdAndDay(scheduleId, day);
    }
}
