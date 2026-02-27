package server.dao;

import common.entities.MedicalCard;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.interfaces.DAO;
import server.utils.SessionFactory;

import java.util.List;

public class MedicalCardDAO implements DAO {

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
    public MedicalCard findById(int id) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return session.get(MedicalCard.class, id);
        }
    }

    @Override
    public List<MedicalCard> findAll() {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<MedicalCard> query = session.createQuery("FROM MedicalCard", MedicalCard.class);
            return query.list();
        }
    }

    public MedicalCard findByUserId(int userId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<MedicalCard> query = session.createQuery(
                    "SELECT p.medicalCard FROM Patient p WHERE p.user.id = :userId", MedicalCard.class);
            query.setParameter("userId", userId);
            return query.uniqueResult();
        }
    }

}