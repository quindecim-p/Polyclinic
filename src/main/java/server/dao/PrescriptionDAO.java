package server.dao;

import common.entities.Prescription;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.interfaces.DAO;
import server.utils.SessionFactory;

import java.util.List;

public class PrescriptionDAO implements DAO {

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
    public Object findById(int id) {
        return null;
    }

    @Override
    public List findAll() {
        return List.of();
    }

    public Prescription findByAppointmentId(int appointmentId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery(
                    "FROM Prescription WHERE appointment.id = :appointmentId", Prescription.class);
            query.setParameter("appointmentId", appointmentId);
            return query.uniqueResult();
        }
    }

    public List<Prescription> findByUserId(int userId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery(
                    "SELECT pr FROM Prescription pr " +
                            "JOIN pr.appointment a " +
                            "JOIN a.medicalCard mc " +
                            "JOIN Patient pa ON pa.medicalCard.id = mc.id " +
                            "JOIN pa.user u " +
                            "WHERE u.id = :userId",
                    Prescription.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }

}