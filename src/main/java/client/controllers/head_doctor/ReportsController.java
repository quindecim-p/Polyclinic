package client.controllers.head_doctor;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import com.google.gson.GsonBuilder;
import common.entities.Appointment;
import common.entities.Doctor;
import common.entities.Patient;
import common.entities.User;
import common.enums.SceneRoute;
import common.enums.types.*;
import common.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsController {

    @FXML
    private TextArea reportTextArea;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        loadReports();
    }

    private void loadReports() {
        try {
            List<User> users = getUsers();
            List<Doctor> doctors = getDoctors();
            List<Patient> patients = getPatients();
            List<Appointment> appointments = getAppointments();

            StringBuilder reportBuilder = new StringBuilder();

            reportBuilder.append("=== Общая информация ===\n")
                    .append("Общее количество пользователей: ").append(users.size()).append("\n")
                    .append("Общее количество врачей: ").append(doctors.size()).append("\n")
                    .append("Общее количество пациентов: ").append(patients.size()).append("\n\n");

            long headDoctorsCount = users.stream()
                    .filter(user -> user.getRole().getRoleType() == RoleType.HEAD_DOCTOR)
                    .count();
            long doctorsCount = doctors.size() - headDoctorsCount;

            reportBuilder.append("=== Роли врачей ===\n")
                    .append("Главные врачи: ").append(headDoctorsCount).append("\n")
                    .append("Врачи: ").append(doctorsCount).append("\n\n");

            reportBuilder.append("=== Врачи по специальностям ===\n");
            Map<String, Long> doctorsBySpecialization = doctors.stream()
                    .collect(Collectors.groupingBy(
                            doctor -> SpecializationType.valueOf(doctor.getSpecialization()).getDescription(),
                            Collectors.counting()
                    ));

            doctorsBySpecialization.forEach((specialization, count) ->
                    reportBuilder.append(specialization).append(": ").append(count).append("\n")
            );

            long scheduledAppointments = appointments.stream()
                    .filter(app -> app.getStatus() == AppointmentStatus.SCHEDULED)
                    .count();
            long completedAppointments = appointments.stream()
                    .filter(app -> app.getStatus() == AppointmentStatus.COMPLETED)
                    .count();

            reportBuilder.append("\n=== Посещения ===\n")
                    .append("Всего посещений: ").append(scheduledAppointments + completedAppointments).append("\n")
                    .append("Запланированные: ").append(scheduledAppointments).append("\n")
                    .append("Проведенные: ").append(completedAppointments).append("\n");

            reportTextArea.setText(reportBuilder.toString());
        } catch (Exception e) {
            showAlert("Ошибка загрузки отчетов: " + e.getMessage());
        }
    }

    private List<User> getUsers() {
        Request request = new Request(RequestType.GET_ALL_USERS, null);
        return sendRequest(request, new TypeToken<>() {
        });
    }

    private List<Doctor> getDoctors() {
        Request request = new Request(RequestType.GET_ALL_DOCTORS, null);
        return sendRequest(request, new TypeToken<>() {
        });
    }

    private List<Patient> getPatients() {
        Request request = new Request(RequestType.GET_ALL_PATIENTS, null);
        return sendRequest(request, new TypeToken<>() {
        });
    }

    private List<Appointment> getAppointments() {
        Request request = new Request(RequestType.GET_ALL_APPOINTMENTS, null);
        return sendRequest(request, new TypeToken<>() {
        });
    }

    private <T> T sendRequest(Request request, TypeToken<T> typeToken) {
        ClientSocket.getInstance().sendRequest(request);
        Response response = ClientSocket.getInstance().receiveResponse();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();

        if (response != null && response.getType() == ResponseType.SUCCESS) {
            return gson.fromJson(response.getMessage(), typeToken.getType());
        } else {
            throw new RuntimeException(response != null ? response.getMessage() : "Неизвестная ошибка");
        }
    }

    @FXML
    private void Back_Pressed() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.HEAD_DOCTOR_MENU);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}