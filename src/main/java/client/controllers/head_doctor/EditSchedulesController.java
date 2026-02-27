package client.controllers.head_doctor;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import common.enums.SceneRoute;
import common.enums.errors.ApplicationError;
import common.enums.errors.ClientError;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import server.dto.DoctorDTO;
import server.dto.WorkDayDTO;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class EditSchedulesController {

    @FXML
    private ComboBox<DoctorDTO> doctorSelection;
    @FXML
    private ComboBox<String> daySelection;
    @FXML
    private Spinner<LocalTime> startTimePicker;
    @FXML
    private Spinner<LocalTime> endTimePicker;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button backButton;
    @FXML
    private TextArea schedulePreview;

    private List<DoctorDTO> doctors;

    @FXML
    public void initialize() {
        setupTimeSpinners();
        setupDaySelection();
        loadDoctors();
        setupDoctorSelectionListener();
    }

    private void setupTimeSpinners() {
        setupSpinner(startTimePicker, LocalTime.of(8, 0));
        setupSpinner(endTimePicker, LocalTime.of(18, 0));
    }

    private void setupSpinner(Spinner<LocalTime> spinner, LocalTime defaultValue) {
        SpinnerValueFactory<LocalTime> factory = new SpinnerValueFactory<>() {
            private final LocalTime min = LocalTime.of(0, 0);
            private final LocalTime max = LocalTime.of(23, 59);

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps * 15L).isBefore(min) ? min : getValue().minusMinutes(steps * 15L));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps * 15L).isAfter(max) ? max : getValue().plusMinutes(steps * 15L));
            }
        };
        spinner.setValueFactory(factory);
        spinner.getValueFactory().setValue(defaultValue);
    }

    private void setupDaySelection() {
        daySelection.getItems().addAll("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье");
    }

    private void loadDoctors() {
        Request request = new Request(RequestType.GET_SCHEDULES, null);
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();

        if (response == null || response.getType() != ResponseType.SUCCESS) {
            showAlert(response == null ? ApplicationError.UNEXPECTED_ERROR.getMessage() : response.getMessage());
            return;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(DayOfWeek.class, new DayOfWeekAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();

        try {
            doctors = gson.fromJson(response.getMessage(), new TypeToken<List<DoctorDTO>>() {}.getType());

            doctorSelection.setConverter(new StringConverter<>() {
                @Override
                public String toString(DoctorDTO doctor) {
                    if (doctor == null) {
                        return "";
                    }
                    return String.format("%s (%s)", doctor.getSurname(), doctor.getSpecialization());
                }

                @Override
                public DoctorDTO fromString(String string) {
                    return doctors.stream()
                            .filter(doc -> String.format("%s (%s)", doc.getSurname(), doc.getSpecialization()).equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });

            doctorSelection.getItems().setAll(doctors);
        } catch (Exception e) {
            showAlert("Ошибка при обработке данных врачей: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void saveSchedule() {
        DoctorDTO selectedDoctor = doctorSelection.getValue();
        String dayName = daySelection.getValue();
        LocalTime startTime = startTimePicker.getValue();
        LocalTime endTime = endTimePicker.getValue();

        if (selectedDoctor == null || dayName == null || startTime == null || endTime == null || startTime.isAfter(endTime)) {
            showAlert(ClientError.CHOOSE_SCHEDULE_PARAMETRS.getMessage());
            return;
        }

        int doctorId = selectedDoctor.getId();
        DayOfWeek day = DayOfWeek.of(daySelection.getItems().indexOf(dayName) + 1);

        WorkDayDTO existingWorkDay = selectedDoctor.getWorkDays().stream()
                .filter(workDay -> workDay.getDay() == day)
                .findFirst()
                .orElse(null);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(DayOfWeek.class, new DayOfWeekAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();

        Request request;
        if (existingWorkDay != null) {
            existingWorkDay.setStartTime(startTime);
            existingWorkDay.setEndTime(endTime);
            request = new Request(RequestType.UPDATE_WORK_DAY, gson.toJson(new Object[]{doctorId, existingWorkDay}));
        } else {
            WorkDayDTO newWorkDay = new WorkDayDTO(day, startTime, endTime);
            selectedDoctor.getWorkDays().add(newWorkDay);
            request = new Request(RequestType.ADD_WORK_DAY, gson.toJson(new Object[]{doctorId, newWorkDay}));
        }

        ClientSocket.getInstance().sendRequest(request);
        Response response = ClientSocket.getInstance().receiveResponse();

        if (response == null || response.getType() != ResponseType.SUCCESS) {
            showAlert(response == null ? ApplicationError.UNEXPECTED_ERROR.getMessage() : response.getMessage());
        } else {
            showAlert(response.getMessage());
            previewSchedule(selectedDoctor);
        }
    }

    @FXML
    private void deleteSchedule() {
        DoctorDTO selectedDoctor = doctorSelection.getValue();
        String dayName = daySelection.getValue();

        if (selectedDoctor == null || dayName == null) {
            showAlert(ClientError.CHOOSE_SCHEDULE_PARAMETRS.getMessage());
            return;
        }

        int doctorId = selectedDoctor.getId();

        DayOfWeek day = DayOfWeek.of(daySelection.getItems().indexOf(dayName) + 1);

        WorkDayDTO existingWorkDay = selectedDoctor.getWorkDays().stream()
                .filter(workDay -> workDay.getDay() == day)
                .findFirst()
                .orElse(null);

        if (existingWorkDay == null) {
            showAlert(ClientError.DOCTOR_NOT_WORK.getMessage());
            return;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(DayOfWeek.class, new DayOfWeekAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();

        Request request = new Request(RequestType.DELETE_WORK_DAY, gson.toJson(new Object[]{doctorId, existingWorkDay}));
        ClientSocket.getInstance().sendRequest(request);
        Response response = ClientSocket.getInstance().receiveResponse();

        if (response == null || response.getType() != ResponseType.SUCCESS) {
            showAlert(ApplicationError.UNEXPECTED_ERROR.getMessage());
        } else {
            showAlert(response.getMessage());
            selectedDoctor.getWorkDays().remove(existingWorkDay);
            previewSchedule(selectedDoctor);
        }
    }

    private void setupDoctorSelectionListener() {
        doctorSelection.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                previewSchedule(newValue);
            } else {
                schedulePreview.clear();
            }
        });
    }

    private void previewSchedule(DoctorDTO doctor) {
        StringBuilder scheduleText = new StringBuilder();
        for (WorkDayDTO workDay : doctor.getWorkDays()) {
            String dayName = workDay.getDay().getDisplayName(TextStyle.FULL, new Locale("ru"));
            scheduleText.append(String.format("%s: %s - %s\n",
                    capitalize(dayName), workDay.getStartTime(), workDay.getEndTime()));
        }
        schedulePreview.setText(scheduleText.toString());
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    @FXML
    private void Back_Pressed() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.HEAD_DOCTOR_MENU);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}