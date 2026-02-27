package server.interfaces;

import java.util.List;

public interface DAO<T> {
    void save(T object);

    void update(T object);

    void delete(T object);

    T findById(int id);

    List<T> findAll();
}
