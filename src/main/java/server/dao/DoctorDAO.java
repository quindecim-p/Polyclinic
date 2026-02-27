package server.dao;

import common.entities.Doctor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.interfaces.DAO;
import server.utils.SessionFactory;

import java.util.List;

public class DoctorDAO implements DAO {

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
    public Doctor findById(int id) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return session.get(Doctor.class, id);
        }
    }

    @Override
    public List<Doctor> findAll() {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Doctor> query = session.createQuery("FROM Doctor", Doctor.class);
            return query.list();
        }
    }

    public Doctor findByUserId(int userId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE user.id = :userId", Doctor.class);
            query.setParameter("userId", userId);
            return query.uniqueResult();
        }
    }

    public Doctor findByPhone(String phone) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE personData.phone = :phone", Doctor.class);
            query.setParameter("phone", phone);
            return query.uniqueResult();
        }
    }

    public Doctor findByEmail(String email) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Doctor> query = session.createQuery("FROM Doctor WHERE personData.email = :email", Doctor.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }

    public Doctor findByAppointmentId(int appointmentId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Doctor> query = session.createQuery(
                    "SELECT a.doctor FROM Appointment a WHERE a.id = :appointmentId",
                    Doctor.class
            );
            query.setParameter("appointmentId", appointmentId);
            return query.uniqueResult();
        }
    }

    public void deleteById(int doctorId) {
        Transaction transaction = null;
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Doctor doctor = session.get(Doctor.class, doctorId);
            session.delete(doctor);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}