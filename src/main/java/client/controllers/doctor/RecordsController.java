package client.controllers.doctor;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import com.google.gson.GsonBuilder;
import server.dto.AppointmentDTO;
import common.enums.SceneRoute;
import common.enums.errors.ClientError;
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

public class RecordsController {

    @FXML
    private ListView<String> appointmentsList;
    @FXML
    private TextArea patientDetailsText;
    @FXML
    private TextArea symptomsText;
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
        loadScheduledAppointments();
        setupListViewListener();
        setupSearch();
    }

    private void loadScheduledAppointments() {
        int doctorId = Session.getInstance().getCurrentUserId();
        Request request = new Request(RequestType.GET_APPOINTMENT_RECORDS, String.valueOf(doctorId));
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
                appointments = appointmentList.stream()
                        .toList();

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
            appointmentsList.getItems().add("Нет запланированных приемов");
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
        symptomsText.setText(appointment.getSymptoms());
        patientDetailsText.setText(String.format("%s %s\nНомер мед. карты: %s",
                appointment.getPatientName(),
                appointment.getPatientSurname(),
                appointment.getMedicalCard()));
    }

    @FXML
    private void filterAppointments() {
        LocalDate from = dateFromPicker.getValue();
        LocalDate to = dateToPicker.getValue();
        if (from == null || to == null || to.isBefore(from)) {
            showAlert(ClientError.INVALID_DATE_RANGE.getMessage());
            return;
        }

        filteredAppointments = appointments.stream()
                .filter(app -> {
                    LocalDate date = app.getAppointmentDate().atZone(ZoneId.systemDefault()).toLocalDate();
                    return !date.isBefore(from) && !date.isAfter(to);
                })
                .toList();

        populateAppointmentsList();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchQuery = newValue.trim().toLowerCase();
            filteredAppointments = appointments.stream()
                    .filter(app -> {
                        String cardNumber = String.valueOf(app.getMedicalCard());
                        String patientName = (app.getPatientSurname() + " " + app.getPatientName()).toLowerCase();
                        return patientName.contains(searchQuery) || cardNumber.contains(searchQuery);
                    })
                    .toList();
            populateAppointmentsList();
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