package server.services;

import common.entities.Patient;
import common.entities.Doctor;
import common.enums.errors.ServerError;
import server.dao.PatientDAO;
import server.dao.DoctorDAO;

public class ValidationService {

    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public void validatePhoneAndEmail(String phone, String email, int excludeId) {
        if (isPhoneExists(phone, excludeId)) {
            throw new IllegalArgumentException(ServerError.PHONE_EXISTS.getMessage());
        }

        if (isEmailExists(email, excludeId)) {
            throw new IllegalArgumentException(ServerError.EMAIL_EXISTS.getMessage());
        }
    }

    private boolean isPhoneExists(String phone, int excludeId) {
        Patient patient = patientDAO.findByPhone(phone);
        if (patient != null && patient.getId() != excludeId) {
            return true;
        }

        Doctor doctor = doctorDAO.findByPhone(phone);
        return doctor != null && doctor.getId() != excludeId;
    }

    private boolean isEmailExists(String email, int excludeId) {
        Patient patient = patientDAO.findByEmail(email);
        if (patient != null && patient.getId() != excludeId) {
            return true;
        }

        Doctor doctor = doctorDAO.findByEmail(email);
        return doctor != null && doctor.getId() != excludeId;
    }
}
