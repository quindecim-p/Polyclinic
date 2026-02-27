package client.controllers.patient;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import server.dto.AppointmentDTO;
import common.enums.SceneRoute;
import common.enums.errors.ApplicationError;
import common.enums.errors.ClientError;
import common.enums.types.AppointmentStatus;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.utils.LocalDateTimeAdapter;
import common.utils.Request;
import common.utils.Response;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MedicalCardController {

    @FXML
    private ListView<String> completedVisitsList;
    @FXML
    private ListView<String> scheduledVisitsList;
    @FXML
    private TextArea symptomsArea;
    @FXML
    private TextArea diagnosisArea;
    @FXML
    private TextArea prescriptionArea;
    @FXML
    private TextArea referenceArea;
    @FXML
    private Label doctorLabel;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button backButton;

    private List<AppointmentDTO> appointments;

    @FXML
    public void initialize() {
        loadMedicalCardData();
        setupCompletedVisitsListener();
    }

    private void loadMedicalCardData() {
        int patientId = Session.getInstance().getCurrentUserId();
        Request request = new Request(RequestType.GET_MEDICAL_CARD, String.valueOf(patientId));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response == null || response.getType() != ResponseType.SUCCESS) {
            showAlert(response != null ? response.getMessage() : ApplicationError.UNEXPECTED_ERROR.getMessage());
            return;
        }

        appointments = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create()
                .fromJson(response.getMessage(), new TypeToken<List<AppointmentDTO>>() {}.getType());

        populateCompletedVisitsList();
        populateScheduledVisitsList();
    }

    private void populateCompletedVisitsList() {
        completedVisitsList.getItems().clear();
        List<AppointmentDTO> completedAppointments = appointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.COMPLETED)
                .toList();

        if (completedAppointments.isEmpty()) {
            completedVisitsList.getItems().add("Нет посещений");
        } else {
            completedAppointments.forEach(appointment -> completedVisitsList.getItems().add(formatAppointment(appointment)));
        }
    }

    private String formatScheduledAppointment(AppointmentDTO appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format("%s - %s, доктор: %s",
                appointment.getAppointmentDate().format(formatter),
                appointment.getDoctorSpecialization(),
                appointment.getDoctorSurname());
    }

    private void populateScheduledVisitsList() {
        scheduledVisitsList.getItems().clear();
        List<AppointmentDTO> scheduledAppointments = appointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.SCHEDULED)
                .toList();

        if (scheduledAppointments.isEmpty()) {
            scheduledVisitsList.getItems().add("Нет запланированных посещений");
        } else {
            scheduledAppointments.forEach(appointment -> scheduledVisitsList.getItems().add(formatScheduledAppointment(appointment)));
        }
    }

    private String formatAppointment(AppointmentDTO appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format("%s - %s",
                appointment.getAppointmentDate().format(formatter),
                appointment.getDoctorSpecialization());
    }

    private void setupCompletedVisitsListener() {
        completedVisitsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int index = completedVisitsList.getSelectionModel().getSelectedIndex();
                displayAppointmentDetails(appointments.stream()
                        .filter(app -> app.getStatus() == AppointmentStatus.COMPLETED)
                        .toList()
                        .get(index));
            }
        });
    }

    private void displayAppointmentDetails(AppointmentDTO appointment) {
        doctorLabel.setText(String.format("Доктор: %s\nСпециальность: %s",
                appointment.getDoctorSurname(), appointment.getDoctorSpecialization()));

        symptomsArea.setText(appointment.getSymptoms());
        diagnosisArea.setText(appointment.getDiagnosisName());
        prescriptionArea.setText(appointment.getPrescriptionInstructions());
        referenceArea.setText(appointment.getReferenceDetails());
    }

    @FXML
    private void applyDateFilter() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            showAlert(ClientError.INVALID_DATE_RANGE.getMessage());
            return;
        }

        List<AppointmentDTO> filteredAppointments = appointments.stream()
                .filter(appointment -> {
                    LocalDate date = appointment.getAppointmentDate().toLocalDate();
                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                })
                .toList();

        completedVisitsList.getItems().clear();
        filteredAppointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.COMPLETED)
                .forEach(appointment -> completedVisitsList.getItems().add(formatAppointment(appointment)));
    }

    @FXML
    private void cancelAppointment() {
        int selectedIndex = scheduledVisitsList.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(ClientError.CHOOSE_APPOINTMENT.getMessage());
            return;
        }

        AppointmentDTO selectedAppointment = appointments.stream()
                .filter(app -> app.getStatus() == AppointmentStatus.SCHEDULED)
                .toList()
                .get(selectedIndex);

        Request request = new Request(RequestType.CANCEL_APPOINTMENT, String.valueOf(selectedAppointment.getId()));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response == null || response.getType() != ResponseType.SUCCESS) {
            showAlert(response != null ? response.getMessage() : ApplicationError.UNEXPECTED_ERROR.getMessage());
            return;
        }

        showAlert(response.getMessage());
        loadMedicalCardData();
    }

    @FXML
    private void Back_Pressed() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.PATIENT_MENU);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}