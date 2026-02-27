package client.controllers.doctor;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import com.google.gson.GsonBuilder;
import common.enums.errors.ClientError;
import server.dto.AppointmentDTO;
import common.enums.SceneRoute;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.utils.LocalDateTimeAdapter;
import common.utils.Request;
import common.utils.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController {

    @FXML
    private ListView<String> appointmentsList;
    @FXML
    private TextArea patientDetailsText;
    @FXML
    private TextArea symptomsText;
    @FXML
    private TextArea diagnosisText;
    @FXML
    private TextArea prescriptionText;
    @FXML
    private TextArea referenceText;
    @FXML
    private DatePicker dateFromPicker;
    @FXML
    private DatePicker dateToPicker;
    @FXML
    private Button backButton;
    @FXML
    private TextField searchField;

    private List<AppointmentDTO> appointments;
    private List<AppointmentDTO> filteredAppointments;

    @FXML
    public void initialize() {
        loadAppointmentHistory();
        setupListViewListener();
        setupSearch();
    }

    private void loadAppointmentHistory() {
        int doctorId = Session.getInstance().getCurrentUserId();
        Request request = new Request(RequestType.GET_APPOINTMENT_HISTORY, String.valueOf(doctorId));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response != null && response.getType() == ResponseType.SUCCESS) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();

            List<AppointmentDTO> appointmentList = gson.fromJson(
                    response.getMessage(),
                    new TypeToken<List<AppointmentDTO>>() {}.getType()
            );

            if (appointmentList != null) {
                appointments = appointmentList.stream().toList();
                filteredAppointments = appointments;
                populateAppointmentsList();
            } else {
                showAlert(response.getMessage());
            }
        } else {
            showAlert(response != null ? response.getMessage() : "Ошибка получения данных");
        }
    }

    private void populateAppointmentsList() {
        appointmentsList.getItems().clear();
        if (filteredAppointments == null || filteredAppointments.isEmpty()) {
            appointmentsList.getItems().add("Нет приемов");
            return;
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        appointmentsList.getItems().addAll(
                filteredAppointments.stream()
                        .map(app -> String.format("%s - %s, %s",
                                app.getAppointmentDate().atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter),
                                app.getPatientSurname(),
                                app.getAppointmentDate().toLocalTime().format(timeFormatter)))
                        .toList()
        );
    }

    private void setupListViewListener() {
        appointmentsList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            if (index >= 0 && index < filteredAppointments.size()) {
                showAppointmentDetails(filteredAppointments.get(index));
            }
        });
    }

    private void showAppointmentDetails(AppointmentDTO appointment) {
        patientDetailsText.setText(String.format("%s %s\nНомер мед. карты: %d",
                appointment.getPatientSurname(),
                appointment.getPatientName(),
                appointment.getMedicalCard()));
        symptomsText.setText(appointment.getSymptoms());
        diagnosisText.setText(String.format("Название: %s\nОписание: %s",
                appointment.getDiagnosisName(),
                appointment.getDiagnosisDescription()));
        prescriptionText.setText(appointment.getPrescriptionInstructions());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        referenceText.setText(String.format("Рекомендации: %s\nСрок действия: %s - %s",
                appointment.getReferenceDetails(),
                appointment.getReferenceValidFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter),
                appointment.getReferenceValidUntil().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter)));
    }

    @FXML
    private void filterAppointments() {
        LocalDate from = dateFromPicker.getValue();
        LocalDate to = dateToPicker.getValue();
        if (from == null || to == null || to.isBefore(from)) {
            showAlert(ClientError.INVALID_DATE_RANGE.getMessage());
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        filteredAppointments = appointments.stream()
                .filter(app -> {
                    LocalDate date = app.getAppointmentDate().atZone(ZoneId.systemDefault()).toLocalDate();
                    return !date.isBefore(from) && !date.isAfter(to);
                })
                .toList();

        appointmentsList.getItems().clear();
        appointmentsList.getItems().addAll(
                filteredAppointments.stream()
                        .map(app -> String.format("%s - %s %s",
                                app.getAppointmentDate().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter),
                                app.getPatientSurname(),
                                app.getPatientName()))
                        .toList()
        );

        if (appointmentsList.getItems().isEmpty()) {
            appointmentsList.getItems().add("Нет приемов");
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchQuery = newValue.trim().toLowerCase();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            filteredAppointments = appointments.stream()
                    .filter(app -> {
                        String cardNumber = String.valueOf(app.getMedicalCard());
                        String patientName = (app.getPatientSurname() + " " + app.getPatientName()).toLowerCase();
                        return patientName.contains(searchQuery) || cardNumber.contains(searchQuery);
                    })
                    .toList();

            appointmentsList.getItems().clear();
            appointmentsList.getItems().addAll(
                    filteredAppointments.stream()
                            .map(app -> String.format("%s - %s %s",
                                    app.getAppointmentDate().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter),
                                    app.getPatientSurname(),
                                    app.getPatientName()))
                            .toList()
            );

            if (appointmentsList.getItems().isEmpty()) {
                appointmentsList.getItems().add("Нет приемов");
            }
        });
    }

    @FXML
    private void Back_Pressed() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.DOCTOR_MENU);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}