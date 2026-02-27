package client.controllers.patient;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import server.dto.DoctorDTO;
import server.dto.WorkDayDTO;
import common.entities.*;
import common.enums.SceneRoute;
import common.enums.errors.ClientError;
import common.enums.types.AppointmentStatus;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentController {

    @FXML
    private ComboBox<String> doctorSelector;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> timeSelector;
    @FXML
    private TextArea symptomsField;
    @FXML
    private Text selectedDoctorDetails;
    @FXML
    private Button confirmButton;
    @FXML
    private Button backButton;

    private List<DoctorDTO> doctors;

    @FXML
    private void initialize() {
        loadDoctors();
        setupDoctorSelectorListener();
        confirmButton.setDisable(true);
    }

    private void loadDoctors() {
        Request request = new Request(RequestType.GET_SCHEDULES, String.valueOf(Session.getInstance().getCurrentUserId()));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response.getType() != ResponseType.SUCCESS) {
            showAlert(response.getMessage(), true);
            return;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(DayOfWeek.class, new DayOfWeekAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();

        doctors = gson.fromJson(response.getMessage(), new TypeToken<List<DoctorDTO>>() {}.getType());

        doctorSelector.setItems(FXCollections.observableArrayList(
                doctors.stream().map(doctor -> doctor.getSurname() + " (" + doctor.getSpecialization() + ")").toList()
        ));
    }

    private void setupDoctorSelectorListener() {
        doctorSelector.setOnAction(event -> {
            int selectedIndex = doctorSelector.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < doctors.size()) {
                DoctorDTO selectedDoctor = doctors.get(selectedIndex);
                displayDoctorDetails(selectedDoctor);
                setupDatePicker(selectedDoctor);
            }
        });
    }

    private void displayDoctorDetails(DoctorDTO doctor) {
        selectedDoctorDetails.setText(String.format("Фамилия: %s\nСпециализация: %s\nКабинет: %d",
                doctor.getSurname(), doctor.getSpecialization(), doctor.getOfficeNumber()));
        timeSelector.getItems().clear();
    }

    private void setupDatePicker(DoctorDTO doctor) {
        datePicker.setDisable(false);
        datePicker.setValue(null);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        datePicker.setOnAction(event -> {
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                updateAvailableTimes(doctor, selectedDate);
            }
        });
    }

    private void updateAvailableTimes(DoctorDTO doctor, LocalDate date) {
        DayOfWeek selectedDayOfWeek = date.getDayOfWeek();

        Optional<WorkDayDTO> workDayOptional = doctor.getWorkDays().stream()
                .filter(day -> day.getDay().equals(selectedDayOfWeek))
                .findFirst();

        if (workDayOptional.isPresent()) {
            WorkDayDTO workDay = workDayOptional.get();
            List<String> availableTimes = generateTimeSlots(workDay.getStartTime(), workDay.getEndTime());

            List<String> filteredTimes = new ArrayList<>();
            for (String time : availableTimes) {
                LocalDateTime appointmentDateTime = LocalDateTime.of(date, LocalTime.parse(time));
                String requestMessage = doctor.getId() + ";" + appointmentDateTime;

                Request request = new Request(RequestType.GET_AVAILABLE_TIMES, requestMessage);
                ClientSocket.getInstance().sendRequest(request);

                Response response = ClientSocket.getInstance().receiveResponse();
                if (response.getType() == ResponseType.SUCCESS && !Boolean.parseBoolean(response.getMessage())) {
                    filteredTimes.add(time);
                }
            }

            timeSelector.setItems(FXCollections.observableArrayList(filteredTimes));
            timeSelector.setDisable(false);

            timeSelector.setOnAction(event -> confirmButton.setDisable(false));
        } else {
            timeSelector.getItems().clear();
            timeSelector.setDisable(true);
            showAlert(ClientError.DOCTOR_NOT_WORK.getMessage(), true);
        }
    }

    private List<String> generateTimeSlots(LocalTime startTime, LocalTime endTime) {
        List<String> timeSlots = new ArrayList<>();
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            timeSlots.add(currentTime.toString());
            currentTime = currentTime.plusMinutes(10);
        }
        return timeSlots;
    }

    @FXML
    private void Confirm_Appointment() {
        String selectedDoctor = doctorSelector.getValue();
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timeSelector.getValue();
        String symptoms = symptomsField.getText();

        if (selectedDoctor == null || selectedDate == null || selectedTime == null) {
            showAlert(ClientError.CHOOSE_APPOINTMENT_PARAMETERS.getMessage(), true);
            return;
        }

        int doctorIndex = doctorSelector.getSelectionModel().getSelectedIndex();
        DoctorDTO selectedDoctorDTO = doctors.get(doctorIndex);

        LocalDateTime appointmentDateTime = LocalDateTime.of(selectedDate, LocalTime.parse(selectedTime));

        int userId = Session.getInstance().getCurrentUserId();

        Request cardRequest = new Request(RequestType.GET_MEDICAL_CARD_ID, String.valueOf(userId));
        ClientSocket.getInstance().sendRequest(cardRequest);

        Response cardResponse = ClientSocket.getInstance().receiveResponse();
        if (cardResponse.getType() != ResponseType.SUCCESS) {
            showAlert(cardResponse.getMessage(), true);
            return;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(DayOfWeek.class, new DayOfWeekAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();

        MedicalCard medicalCard = gson.fromJson(cardResponse.getMessage(), MedicalCard.class);

        Doctor doctor = new Doctor();
        doctor.setId(selectedDoctorDTO.getId());
        doctor.setSpecialization(selectedDoctorDTO.getSpecialization());
        doctor.setOfficeNumber(selectedDoctorDTO.getOfficeNumber());
        doctor.setPersonData(new PersonData());
        doctor.setUser(new User());

        Appointment appointment = new Appointment(symptoms, appointmentDateTime, AppointmentStatus.SCHEDULED, medicalCard, doctor);

        Request request = new Request(RequestType.ADD_APPOINTMENT, gson.toJson(appointment));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        showAlert(response.getMessage(), response.getType() != ResponseType.SUCCESS);
    }

    @FXML
    private void Back_Pressed() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.PATIENT_MENU);
    }

    private void showAlert(String message, boolean isError) {
        Alert alert = new Alert(isError ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(isError ? "Ошибка" : "Информация");
        alert.setContentText(message);
        alert.showAndWait();
    }

}