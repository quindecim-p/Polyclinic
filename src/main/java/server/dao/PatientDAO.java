package server.dao;

import common.entities.Patient;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.interfaces.DAO;
import server.utils.SessionFactory;

import java.util.List;

public class PatientDAO implements DAO {

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
    public Patient findById(int id) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return session.get(Patient.class, id);
        }
    }

    @Override
    public List<Patient> findAll() {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Patient> query = session.createQuery("FROM Patient", Patient.class);
            return query.list();
        }
    }

    public Patient findByUserId(int userId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Patient> query = session.createQuery("FROM Patient WHERE user.id = :userId", Patient.class);
            query.setParameter("userId", userId);
            return query.uniqueResult();
        }
    }

    public Patient findByPhone(String phone) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Patient> query = session.createQuery("FROM Patient WHERE personData.phone = :phone", Patient.class);
            query.setParameter("phone", phone);
            return query.uniqueResult();
        }
    }

    public Patient findByEmail(String email) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Patient> query = session.createQuery("FROM Patient WHERE personData.email = :email", Patient.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }

    public Patient findByMedicalCardId(int medicalCardId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Patient> query = session.createQuery("SELECT p FROM Patient p WHERE p.medicalCard.id = :medicalCardId", Patient.class
            );
            query.setParameter("medicalCardId", medicalCardId);
            return query.uniqueResult();
        }
    }

    public List<Patient> findByDoctorId(int doctorId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Patient> query = session.createQuery(
                    "SELECT DISTINCT p " +
                            "FROM Patient p " +
                            "JOIN p.medicalCard mc " +
                            "JOIN Appointment a ON a.medicalCard.id = mc.id " +
                            "WHERE a.doctor.id = :doctorId " +
                            "AND a.status = 'COMPLETED'",
                    Patient.class
            );
            query.setParameter("doctorId", doctorId);
            return query.list();
        }
    }

    public void deleteById(int patientId) {
        Transaction transaction = null;
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Patient patient = session.get(Patient.class, patientId);
            session.delete(patient);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}