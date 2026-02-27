package server.dao;

import common.entities.Reference;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.interfaces.DAO;
import server.utils.SessionFactory;

import java.util.List;

public class ReferenceDAO implements DAO {

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

    public Reference findByAppointmentId(int appointmentId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Reference> query = session.createQuery(
                    "FROM Reference WHERE appointment.id = :appointmentId", Reference.class);
            query.setParameter("appointmentId", appointmentId);
            return query.uniqueResult();
        }
    }

    public List<Reference> findByUserId(int userId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<Reference> query = session.createQuery(
                    "SELECT r FROM Reference r " +
                            "JOIN r.appointment a " +
                            "JOIN a.medicalCard mc " +
                            "JOIN Patient p ON p.medicalCard.id = mc.id " +
                            "WHERE p.user.id = :userId", Reference.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }

}