package client.controllers.doctor;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import client.validations.AppointmentValidation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import common.enums.errors.ClientError;
import common.enums.types.RoleType;
import common.utils.*;
import server.dto.AppointmentDTO;
import common.enums.SceneRoute;
import common.enums.types.AppointmentStatus;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AppointmentsController {

    @FXML
    private ComboBox<String> patientsDropdown;
    @FXML
    private TextField symptomsField;
    @FXML
    private TextField diagnosisNameField;
    @FXML
    private TextField diagnosisDescriptionField;
    @FXML
    private TextField prescriptionField;
    @FXML
    private TextField referenceField;
    @FXML
    private DatePicker referenceValidFromPicker;
    @FXML
    private DatePicker referenceValidUntilPicker;
    @FXML
    private Button backButton;

    private List<AppointmentDTO> todayAppointments;

    @FXML
    public void initialize() {
        loadTodayAppointments();
        setupDropdownListener();
        disablePastDates(referenceValidFromPicker);
        disablePastDates(referenceValidUntilPicker);
    }

    private void disablePastDates(DatePicker datePicker) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    private void loadTodayAppointments() {
        int doctorId = Session.getInstance().getCurrentUserId();

        Request request = new Request(RequestType.GET_TODAY_APPOINTMENTS, String.valueOf(doctorId));
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
                todayAppointments = appointmentList.stream().toList();
                populateDropdown();
            } else {
                showAlert(response.getMessage());
            }
        } else {
            showAlert(response != null ? response.getMessage() : "Ошибка получения данных");
        }
    }

    private void populateDropdown() {
        patientsDropdown.getItems().clear();
        if (todayAppointments == null || todayAppointments.isEmpty()) {
            patientsDropdown.setPromptText("Нет пациентов на сегодня");
            return;
        }

        patientsDropdown.getItems().addAll(
                todayAppointments.stream()
                        .map(app -> String.format("%s %s (Мед. карта: %s)", app.getPatientName(), app.getPatientSurname(), app.getMedicalCard()))
                        .toList()
        );
    }

    private void setupDropdownListener() {
        patientsDropdown.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            if (index >= 0 && index < todayAppointments.size()) {
                showAppointmentDetails(todayAppointments.get(index));
            }
        });
    }

    private void showAppointmentDetails(AppointmentDTO appointment) {
        symptomsField.setText(appointment.getSymptoms());
    }

    @FXML
    private void updateVisit() {
        int selectedIndex = patientsDropdown.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(ClientError.CHOOSE_PATIENT.getMessage());
            return;
        }

        String symptoms = symptomsField.getText().trim();
        String diagnosisName = diagnosisNameField.getText().trim();
        String diagnosisDescription = diagnosisDescriptionField.getText().trim();
        String prescription = prescriptionField.getText().trim();
        String reference = referenceField.getText().trim();
        LocalDate referenceValidFrom = referenceValidFromPicker.getValue();
        LocalDate referenceValidUntil = referenceValidUntilPicker.getValue();

        ClientError validationError = AppointmentValidation.validate(
                symptoms,
                diagnosisName,
                diagnosisDescription,
                prescription,
                reference,
                referenceValidFrom,
                referenceValidUntil
        );

        if (validationError != null) {
            showAlert(validationError.getMessage());
            return;
        }

        AppointmentDTO selectedAppointment = todayAppointments.get(selectedIndex);

        try {
            selectedAppointment.setSymptoms(symptoms);
            selectedAppointment.setDiagnosisName(diagnosisName);
            selectedAppointment.setDiagnosisDescription(diagnosisDescription);
            selectedAppointment.setPrescriptionInstructions(prescription);
            selectedAppointment.setReferenceDetails(reference);

            if (referenceValidFrom != null && referenceValidUntil != null) {
                selectedAppointment.setReferenceValidFrom(
                        Date.from(referenceValidFrom.atStartOfDay(ZoneId.systemDefault()).toInstant())
                );
                selectedAppointment.setReferenceValidUntil(
                        Date.from(referenceValidUntil.atStartOfDay(ZoneId.systemDefault()).toInstant())
                );
            }

            selectedAppointment.setStatus(AppointmentStatus.COMPLETED);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                    .create();

            Request request = new Request(RequestType.UPDATE_APPOINTMENT, gson.toJson(selectedAppointment));

            ClientSocket.getInstance().sendRequest(request);

            Response response = ClientSocket.getInstance().receiveResponse();
            if (response != null && response.getType() == ResponseType.SUCCESS) {
                showAlert(response.getMessage());
                clearFields();
                loadTodayAppointments();
            } else {
                showAlert(response != null ? response.getMessage() : "Ошибка сохранения приема");
            }
        } catch (IllegalArgumentException e) {
            showAlert("Ошибка: " + e.getMessage());
        }
    }

    private void clearFields() {
        symptomsField.clear();
        diagnosisNameField.clear();
        diagnosisDescriptionField.clear();
        prescriptionField.clear();
        referenceField.clear();
        referenceValidFromPicker.setValue(null);
        referenceValidUntilPicker.setValue(null);
    }

    @FXML
    private void Back_Pressed() throws IOException {
        RoleType roleType = Session.getInstance().getCurrentUser().getRole().getRoleType();
        Stage stage;
        switch (roleType) {
            case HEAD_DOCTOR:
                stage = (Stage) backButton.getScene().getWindow();
                SceneSwitcher.switchScene(stage, SceneRoute.HEAD_DOCTOR_MENU);
                break;
            case DOCTOR:
                stage = (Stage) backButton.getScene().getWindow();
                SceneSwitcher.switchScene(stage, SceneRoute.DOCTOR_MENU);
                break;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}