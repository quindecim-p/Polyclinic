package server.dao;

import common.entities.Appointment;
import common.enums.types.AppointmentStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.interfaces.DAO;
import server.utils.SessionFactory;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentDAO implements DAO {

    @Override
    public void save(Object object) {
        Transaction transaction = null;
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(object);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void update(Object object) {
        Transaction transaction = null;
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(object);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Object object) {
        Transaction transaction = null;
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(object);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public Appointment findById(int id) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return session.get(Appointment.class, id);
        }
    }

    @Override
    public List<Appointment> findAll() {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Appointment> query = session.createQuery("FROM Appointment", Appointment.class);
            return query.list();
        }
    }

    public List<Appointment> findByMedicalCardId(int medicalCardId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Appointment> query = session.createQuery("FROM Appointment WHERE medicalCard.id = :medicalCardId", Appointment.class);
            query.setParameter("medicalCardId", medicalCardId);
            return query.list();
        }
    }

    public List<Appointment> findByDoctorIdHistory(int doctorId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Appointment> query = session.createQuery(
                    "FROM Appointment WHERE doctor.id = :doctorId AND status = :status",
                    Appointment.class
            );
            query.setParameter("doctorId", doctorId);
            query.setParameter("status", AppointmentStatus.COMPLETED);
            return query.list();
        }
    }

    public List<Appointment> findByDoctorIdRecords(int doctorId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Appointment> query = session.createQuery(
                    "FROM Appointment WHERE doctor.id = :doctorId AND status = :status",
                    Appointment.class
            );
            query.setParameter("doctorId", doctorId);
            query.setParameter("status", AppointmentStatus.SCHEDULED);
            return query.list();
        }
    }

    public List<Appointment> findByDoctorIdToday(int doctorId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Appointment> query = session.createQuery(
                    "FROM Appointment " +
                            "WHERE doctor.id = :doctorId " +
                            "AND status = :status " +
                            "AND date(appointmentDate) = current_date",
                    Appointment.class
            );
            query.setParameter("doctorId", doctorId);
            query.setParameter("status", AppointmentStatus.SCHEDULED);
            return query.list();
        }
    }

    public boolean existsByDoctorIdAndDateTime(int doctorId, LocalDateTime dateTime) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :dateTime",
                    Long.class
            );
            query.setParameter("doctorId", doctorId);
            query.setParameter("dateTime", dateTime);

            Long count = query.uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean isAlreadyBooked(int doctorId, int medicalCardId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(a) FROM Appointment a " +
                            "WHERE a.doctor.id = :doctorId " +
                            "AND a.medicalCard.id = :medicalCardId " +
                            "AND a.status = :status", Long.class
            );
            query.setParameter("doctorId", doctorId);
            query.setParameter("medicalCardId", medicalCardId);
            query.setParameter("status", AppointmentStatus.SCHEDULED);

            Long count = query.uniqueResult();
            return count != null && count > 0;
        }
    }

    public void deleteById(int appointmentId) {
        Transaction transaction = null;
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Appointment appointment = session.get(Appointment.class, appointmentId);
            session.delete(appointment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}