package server.dao;

import common.entities.WorkingSchedule;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.interfaces.DAO;
import server.utils.SessionFactory;

import java.util.List;

public class WorkingScheduleDAO implements DAO {
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

    }

    @Override
    public void delete(Object object) {

    }

    @Override
    public Object findById(int id) {
        return null;
    }

    @Override
    public List findAll() {
        return List.of();
    }

    public WorkingSchedule findByDoctorId(int doctorId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<WorkingSchedule> query = session.createQuery("FROM WorkingSchedule WHERE doctor.id = :doctorId", WorkingSchedule.class);
            query.setParameter("doctorId", doctorId);
            return query.uniqueResult();
        }
    }
}
