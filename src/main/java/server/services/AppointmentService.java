package server.services;

import common.entities.Appointment;
import server.dao.AppointmentDAO;
import server.interfaces.Service;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService implements Service<Appointment> {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @Override
    public Appointment findEntity(int id) {
        return null;
    }

    @Override
    public void saveEntity(Appointment entity) {
        appointmentDAO.save(entity);
    }

    @Override
    public void deleteEntity(int id) {
        appointmentDAO.delete(id);
    }

    @Override
    public void updateEntity(Appointment entity) {
        appointmentDAO.update(entity);
    }

    @Override
    public List<Appointment> findAllEntities() {
        return appointmentDAO.findAll();
    }

    public List<Appointment> findByMedicalCardId(int medicalCardId) {
        return appointmentDAO.findByMedicalCardId(medicalCardId);
    }

    public List<Appointment> findByDoctorIdHistory(int doctorId) {
        return appointmentDAO.findByDoctorIdHistory(doctorId);
    }

    public List<Appointment> findByDoctorIdRecords(int doctorId) {
        return appointmentDAO.findByDoctorIdRecords(doctorId);
    }

    public List<Appointment> findByDoctorIdToday(int doctorId) {
        return appointmentDAO.findByDoctorIdToday(doctorId);
    }

    public boolean isTimeOccupied(int doctorId, LocalDateTime dateTime) {
        return appointmentDAO.existsByDoctorIdAndDateTime(doctorId, dateTime);
    }

    public boolean isAlreadyBooked(int doctorId, int medicalCardId) {
        return appointmentDAO.isAlreadyBooked(doctorId, medicalCardId);
    }

    public void deleteById(int appointmentId) {
        appointmentDAO.deleteById(appointmentId);
    }

}
