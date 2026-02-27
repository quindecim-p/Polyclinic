package server.services;

import common.entities.Doctor;
import common.entities.Patient;
import common.entities.PersonData;
import common.entities.User;
import common.enums.errors.ServerError;
import server.dao.UserDAO;
import server.interfaces.Service;

import java.util.List;

public class UserService implements Service<User> {

    private final UserDAO userDAO = new UserDAO();
    private final PatientService patientService = new PatientService();
    private final DoctorService doctorService = new DoctorService();

    @Override
    public User findEntity(int id) {
        return userDAO.findById(id);
    }

    @Override
    public void saveEntity(User entity) {

    }

    @Override
    public void deleteEntity(int id) {

    }

    @Override
    public void updateEntity(User entity) {
        User existingUser = findEntity(entity.getId());

        if (existingUser == null) {
            throw new IllegalArgumentException(ServerError.USER_NOT_FOUND.getMessage());
        }

        String username = entity.getUsername();

        User existingUserByUsername = userDAO.findByUsername(username);
        if (existingUserByUsername != null && existingUserByUsername.getId() != entity.getId()) {
            throw new IllegalArgumentException(ServerError.USERNAME_EXISTS.getMessage());
        }

        existingUser.setUsername(entity.getUsername());
        existingUser.setPassword(entity.getPassword());

        userDAO.update(existingUser);
    }

    @Override
    public List<User> findAllEntities() {
        return userDAO.findAll();
    }

    public boolean isUsernameExists(String username) {
        return userDAO.findByUsername(username) != null;
    }

    public User login(User user) {
        User foundUser = findByUsername(user.getUsername());
        if (foundUser == null) {
            throw new IllegalArgumentException(ServerError.INVALID_CREDENTIALS.getMessage());
        }

        if (!foundUser.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException(ServerError.INVALID_CREDENTIALS.getMessage());
        }

        return foundUser;
    }

    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public Object findUserById(int userId) {
        Patient patient = patientService.findByUserId(userId);
        if (patient != null) {
            return patient;
        }

        Doctor doctor = doctorService.findByUserId(userId);
        if (doctor != null) {
            return doctor;
        }

        throw new IllegalArgumentException(ServerError.USER_NOT_FOUND.getMessage());
    }

    public Object findUserByPhone(String phone) {
        Patient patient = patientService.findByPhone(phone);
        if (patient != null) {
            return patient;
        }

        Doctor doctor = doctorService.findByPhone(phone);
        if (doctor != null) {
            return doctor;
        }

        throw new IllegalArgumentException(ServerError.USER_NOT_FOUND.getMessage());
    }

    public PersonData getProfile(int userId) {
        Object userDetails = findUserById(userId);

        if (userDetails instanceof Patient patient) {
            return patient.getPersonData();
        } else if (userDetails instanceof Doctor doctor) {
            return doctor.getPersonData();
        } else {
            throw new IllegalArgumentException(ServerError.USER_NOT_FOUND.getMessage());
        }
    }

    public void updateProfile(PersonData personData) {
        Object userDetails = findUserByPhone(personData.getPhone());

        switch (userDetails) {
            case null -> throw new IllegalArgumentException(ServerError.USER_NOT_FOUND.getMessage());
            case Patient patient -> {
                patient.setPersonData(personData);
                patientService.updateEntity(patient);
            }
            case Doctor doctor -> {
                doctor.setPersonData(personData);
                doctorService.updateEntity(doctor);
            }
            default -> throw new IllegalArgumentException(ServerError.UPDATE_FAILED.getMessage());
        }

    }

}