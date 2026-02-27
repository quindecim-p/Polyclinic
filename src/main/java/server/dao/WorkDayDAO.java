package server.dao;

import common.entities.WorkDay;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.interfaces.DAO;
import server.utils.SessionFactory;

import java.time.DayOfWeek;
import java.util.List;

public class WorkDayDAO implements DAO {
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
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return session.get(WorkDay.class, id);
        }
    }

    @Override
    public List<WorkDay> findAll() {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<WorkDay> query = session.createQuery("FROM WorkDay", WorkDay.class);
            return query.list();
        }
    }

    public List<WorkDay> findByScheduleId(int scheduleId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<WorkDay> query = session.createQuery("FROM WorkDay WHERE workingSchedule.id = :scheduleId", WorkDay.class);
            query.setParameter("scheduleId", scheduleId);
            return query.list();
        }
    }

    public WorkDay findByScheduleIdAndDay(int scheduleId, DayOfWeek day) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Query<WorkDay> query = session.createQuery(
                    "FROM WorkDay WHERE workingSchedule.id = :scheduleId AND day = :day", WorkDay.class);
            query.setParameter("scheduleId", scheduleId);
            query.setParameter("day", day);
            return query.uniqueResult();
        }
    }

    public void deleteById(int workDayId) {
        Transaction transaction = null;
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            WorkDay workDay = session.get(WorkDay.class, workDayId);
            session.delete(workDay);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}